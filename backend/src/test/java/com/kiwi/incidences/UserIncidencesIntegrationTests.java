package com.kiwi.incidences;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.common.exceptions.GlobalExceptionHandler;
import com.kiwi.config.JacksonConfig;
import com.kiwi.config.WebSecurityConfig;
import com.kiwi.features.incidences.controllers.IncidenceRepository;
import com.kiwi.features.incidences.controllers.UserIncidenceRepository;
import com.kiwi.features.incidences.data.IncidencePersistence;
import com.kiwi.features.incidences.data.UserIncidenceDTO;
import com.kiwi.features.incidences.data.UserIncidenceKey;
import com.kiwi.features.incidences.data.UserIncidencePersistence;
import com.kiwi.features.users.controllers.UsersRepository;
import com.kiwi.features.users.data.UsersDataMapper;
import com.kiwi.features.users.data.UsersDomain;
import com.kiwi.features.users.data.UsersPersistence;
import com.kiwi.security.JwtUtils;
import jakarta.transaction.Transactional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.kiwi.users.UsersTestFactory.validLoginDTO;
import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
@Sql(scripts = "/TestSetUp.sql")
@ActiveProfiles("test")
@AutoConfigureMockMvc
@AutoConfigureAddonsWebmvcResourceServerSecurity
@Import({ GlobalExceptionHandler.class, WebSecurityConfig.class, JwtUtils.class, JacksonConfig.class })
public class UserIncidencesIntegrationTests {

    private final String API_URL = "/api/user_incidences";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UsersRepository usersRepository;
    @Autowired private IncidenceRepository incidenceRepository;
    @Autowired private UserIncidenceRepository userIncidenceRepository;

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getUserIncidence_returnsTrueWhenUserIncidenceExists() throws Exception {
        var user = createUser();
        var incidence = createIncidence("isSorenInGroup");
        createUserIncidence(user.getId(), incidence.getId(), true);

        mockMvc.perform(get(API_URL + "/isSorenInGroup"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getUserIncidence_returnsFalseWhenIncidenceDoesNotExist() throws Exception {
        createUser();

        mockMvc.perform(get(API_URL + "/non_existing_incidence"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void postUserIncidence_createsIncidenceAndUserIncidenceWhenTheyDoNotExist() throws Exception {
        var user = createUser();
        var request = dto("stress", true);

        mockMvc.perform(
                        post(API_URL)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNoContent());

        var incidence = incidenceRepository.findByName("stress").orElseThrow();
        var userIncidence = userIncidenceRepository
                .findByIdUserIdAndIdIncidenceId(user.getId(), incidence.getId())
                .orElseThrow();

        assertEquals("stress", incidence.getName());
        assertTrue(userIncidence.isValue());
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void postUserIncidence_updatesExistingUserIncidence() throws Exception {
        var user = createUser();
        var incidence = createIncidence("sleep");
        createUserIncidence(user.getId(), incidence.getId(), false);

        var request = dto("sleep", true);

        mockMvc.perform(
                        post(API_URL)
                                .contentType(APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isNoContent());

        var updated = userIncidenceRepository
                .findByIdUserIdAndIdIncidenceId(user.getId(), incidence.getId())
                .orElseThrow();

        assertTrue(updated.isValue());
    }

    private UsersPersistence createUser() {
        UsersDomain domain = UsersDataMapper.toDomainWithoutPoints(validUserDTO());
        UsersPersistence user =
                UsersDataMapper.toPersistence(domain, validLoginDTO().getPassword());
        return usersRepository.saveAndFlush(user);
    }

    private IncidencePersistence createIncidence(String name) {
        IncidencePersistence incidence = new IncidencePersistence();
        incidence.setName(name);
        return incidenceRepository.saveAndFlush(incidence);
    }

    private UserIncidencePersistence createUserIncidence(Long userId, Long incidenceId, boolean value) {
        UserIncidencePersistence userIncidence = new UserIncidencePersistence();
        userIncidence.setId(new UserIncidenceKey(userId, incidenceId));
        userIncidence.setValue(value);
        return userIncidenceRepository.saveAndFlush(userIncidence);
    }

    private UserIncidenceDTO dto(String name, boolean value) {
        return UserIncidenceDTO.builder()
                .name(name)
                .value(value)
                .build();
    }
}