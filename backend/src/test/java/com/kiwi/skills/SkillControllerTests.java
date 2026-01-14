package com.kiwi.skills;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.kiwi.common.exceptions.GlobalExceptionHandler;
import com.kiwi.config.WebSecurityConfig;
import com.kiwi.features.skills.controllers.SkillController;
import com.kiwi.features.skills.controllers.SkillService;
import com.kiwi.features.users.controllers.CustomUserDetailsService;
import com.kiwi.features.users.controllers.UsersService;
import com.kiwi.features.users.data.UsersPersistence;
import com.kiwi.security.AuthEntryPointJwt;
import com.kiwi.security.JwtUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static com.kiwi.utils.HTTPTestUtils.getPostRequestBuilder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(SkillController.class)
@AutoConfigureAddonsWebmvcResourceServerSecurity
@Import({ GlobalExceptionHandler.class, WebSecurityConfig.class })
public class SkillControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private JwtUtils jwtUtils;
    @MockitoBean private CustomUserDetailsService userDetailsService;
    @MockitoBean private AuthEntryPointJwt authEntryPointJwt;
    @MockitoBean private UsersService usersService;
    @MockitoBean private SkillService skillService;

    private final String baseAPIUrl = "/api/skills";

    // ============================================================
    // GET SKILLS
    // ============================================================

    @Test
    @WithMockUser(username = "test@test.com")
    public void getAllSkills_valid_returnsOk() throws Exception {

        when(usersService.getUserByEmail(any()))
                .thenReturn(Optional.of(new UsersPersistence() {{
                    setId(1L);
                    setEmail("test@test.com");
                }}));

        when(skillService.getAllSkillsForUser(1L))
                .thenReturn(List.of(
                        SkillTestFactory.skillDto(1L, false, 0),
                        SkillTestFactory.skillDto(2L, false, 0)
                ));

        mockMvc.perform(get(baseAPIUrl)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    public void getEquippedSkills_valid_returnsOk() throws Exception {

        when(usersService.getUserByEmail(any()))
                .thenReturn(Optional.of(new UsersPersistence() {{
                    setId(1L);
                    setEmail("test@test.com");
                }}));

        when(skillService.getEquippedSkillsForUser(1L))
                .thenReturn(List.of(
                        SkillTestFactory.skillDto(1L, false, 1)
                ));

        mockMvc.perform(get(baseAPIUrl + "/equipped")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    // ============================================================
    // GIVE / LEVEL UP
    // ============================================================

    @Test
    @WithMockUser(username = "test@test.com")
    public void giveSkill_valid_returnsOk() throws Exception {

        when(usersService.getUserByEmail(any()))
                .thenReturn(Optional.of(new UsersPersistence() {{
                    setId(1L);
                }}));

        when(skillService.giveSkillToUser(1L, 1L))
                .thenReturn(SkillTestFactory.skillDto(1L, false, 0));

        mockMvc.perform(getPostRequestBuilder(baseAPIUrl + "/1/give", null))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    public void levelUpSkill_valid_returnsOk() throws Exception {

        when(usersService.getUserByEmail(any()))
                .thenReturn(Optional.of(new UsersPersistence() {{
                    setId(1L);
                }}));

        when(skillService.levelUpSkill(1L, 1L))
                .thenReturn(SkillTestFactory.skillDto(2L, false, 0));

        mockMvc.perform(getPostRequestBuilder(baseAPIUrl + "/1/levelup", null))
                .andExpect(status().isOk());
    }

    // ============================================================
    // COOLDOWN
    // ============================================================

    @Test
    @WithMockUser(username = "test@test.com")
    public void putOnCooldown_valid_returnsOk() throws Exception {

        when(usersService.getUserByEmail(any()))
                .thenReturn(Optional.of(new UsersPersistence() {{
                    setId(1L);
                }}));

        when(skillService.putSkillOnCooldown(1L, 1L))
                .thenReturn(SkillTestFactory.skillDto(1L, true, 1));

        mockMvc.perform(getPostRequestBuilder(baseAPIUrl + "/1/cooldown", null))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    public void removeCooldown_valid_returnsOk() throws Exception {

        when(usersService.getUserByEmail(any()))
                .thenReturn(Optional.of(new UsersPersistence() {{
                    setId(1L);
                }}));

        when(skillService.removeCooldown(1L, 1L))
                .thenReturn(SkillTestFactory.skillDto(1L, false, 1));

        mockMvc.perform(getPostRequestBuilder(baseAPIUrl + "/1/ready", null))
                .andExpect(status().isOk());
    }
}
