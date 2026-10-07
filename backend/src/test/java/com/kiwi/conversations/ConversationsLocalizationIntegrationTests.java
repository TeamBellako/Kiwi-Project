package com.kiwi.conversations;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.kiwi.common.exceptions.GlobalExceptionHandler;
import com.kiwi.config.JacksonConfig;
import com.kiwi.config.WebSecurityConfig;
import com.kiwi.features.conversations.controllers.ConversationsRepository;
import com.kiwi.features.conversations.controllers.ConversationOptionsRepository;
import com.kiwi.features.conversations.data.ConversationOptionPersistence;
import com.kiwi.features.conversations.data.ConversationPersistence;
import com.kiwi.features.conversations.data.ConversationType;
import com.kiwi.features.conversations.data.NextEventType;
import com.kiwi.features.users.controllers.UsersRepository;
import com.kiwi.features.users.data.UsersPersistence;
import com.kiwi.security.JwtUtils;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = "/TestSetUp.sql")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureAddonsWebmvcResourceServerSecurity
@Import({GlobalExceptionHandler.class, WebSecurityConfig.class, JwtUtils.class, JacksonConfig.class})
public class ConversationsLocalizationIntegrationTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private UsersRepository usersRepository;
    @Autowired private ConversationsRepository conversationsRepository;
        @Autowired private ConversationOptionsRepository conversationOptionsRepository;
    @Autowired private JdbcTemplate jdbc;
    @Autowired private EntityManager entityManager;

    @Test
    @WithMockUser(username = "locale@test.com")
    public void getConversation_spanishUser_usesSpanishColumns() throws Exception {
        Long id = createConversationFor("es");

        mockMvc.perform(get("/api/conversations/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dialog").value("Dialogo"))
                .andExpect(jsonPath("$.dialogM").value("Dialogo masculino"))
                .andExpect(jsonPath("$.dialogW").value("Dialogo femenino"))
                .andExpect(jsonPath("$.options[0].text").value("Opcion traducida"))
                .andExpect(jsonPath("$.options[0].textM").value("Opcion masculina traducida"))
                .andExpect(jsonPath("$.options[0].textW").value("Opcion femenina traducida"));
    }

    @Test
    @WithMockUser(username = "locale@test.com")
    public void getConversation_englishUser_usesEnglishColumns() throws Exception {
        Long id = createConversationFor("en");

        mockMvc.perform(get("/api/conversations/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dialog").value("English localized"))
                .andExpect(jsonPath("$.dialogM").value("English male"))
                .andExpect(jsonPath("$.dialogW").value("English female"))
                .andExpect(jsonPath("$.options[0].text").value("English option"))
                .andExpect(jsonPath("$.options[0].textM").value("English male option"))
                .andExpect(jsonPath("$.options[0].textW").value("English female option"));
    }

    @Test
    @WithMockUser(username = "locale@test.com")
    public void getConversation_defaultEnglishUser_canSwitchQueryToSpanish() throws Exception {
        Long id = createConversationFor("en");

        mockMvc.perform(get("/api/conversations/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dialog").value("English localized"));

        UsersPersistence user = usersRepository.findByEmail("locale@test.com").orElseThrow();
        user.setLanguagePreference("es");
        usersRepository.saveAndFlush(user);

        mockMvc.perform(get("/api/conversations/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dialog").value("Dialogo"))
                .andExpect(jsonPath("$.options[0].text").value("Opcion traducida"));
    }

    private Long createConversationFor(String language) {
        UsersPersistence user = UsersPersistence.builder()
                .email("locale@test.com")
                .hashedPassword("test")
                .registerDate(LocalDate.now())
                .languagePreference(language)
                .build();
        usersRepository.saveAndFlush(user);

        ConversationPersistence conversation = conversationsRepository.saveAndFlush(ConversationPersistence.builder()
                .type(ConversationType.FULL)
                .name("locale-test")
                .sprite("sprite")
                .dark(false)
                .dialog("English dialog")
                .dialogM("English male dialog")
                .dialogW("English female dialog")
                .nextEvent(NextEventType.END)
                .build());

        ConversationOptionPersistence option = conversationOptionsRepository.saveAndFlush(
                ConversationOptionPersistence.builder()
                        .conversation(conversation)
                        .text("English option")
                        .textM("English male option")
                        .textW("English female option")
                        .nextEventId(1L)
                        .build());

        jdbc.update("""
                UPDATE conversation_options
                SET text_en = ?, text_es = ?, text_m_en = ?, text_m_es = ?, text_w_en = ?, text_w_es = ?
                WHERE id = ?
                """, "English option", "Opcion traducida", "English male option", "Opcion masculina traducida",
                "English female option", "Opcion femenina traducida", option.getId());

        jdbc.update("""
                UPDATE conversations
                SET dialog_en = ?, dialog_es = ?, dialog_m_en = ?, dialog_m_es = ?, dialog_w_en = ?, dialog_w_es = ?
                WHERE id = ?
                """, "English localized", "Dialogo", "English male", "Dialogo masculino",
                "English female", "Dialogo femenino", conversation.getId());

        // Evita que la query nativa devuelva la entidad ya gestionada.
        entityManager.clear();
        return conversation.getId();
    }
}