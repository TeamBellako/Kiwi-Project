package com.kiwi.combat;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.kiwi.common.exceptions.GlobalExceptionHandler;
import com.kiwi.config.WebSecurityConfig;
import com.kiwi.features.combat.controllers.CombatController;
import com.kiwi.features.combat.controllers.CombatFacadeService;
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

import java.util.Optional;

import static com.kiwi.utils.HTTPTestUtils.getPostRequestBuilder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(CombatController.class)
@AutoConfigureAddonsWebmvcResourceServerSecurity
@Import({ GlobalExceptionHandler.class, WebSecurityConfig.class })
public class CombatControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private JwtUtils jwtUtils;
    @MockitoBean private CustomUserDetailsService userDetailsService;
    @MockitoBean private AuthEntryPointJwt authEntryPointJwt;
    @MockitoBean private UsersService usersService;
    @MockitoBean private CombatFacadeService combatFacadeService;

    private final String baseAPIUrl = "/api/combat";

    private final Long userId = 1L;

    private void mockAuthenticatedUser() {
        when(usersService.getUserByEmail(any()))
                .thenReturn(Optional.of(new UsersPersistence() {{
                    setId(userId);
                    setEmail("test@test.com");
                }}));
    }

    // ============================================================
    // START / RESUME
    // ============================================================

    @Test
    @WithMockUser(username = "test@test.com")
    public void startCombat_valid_returnsOk() throws Exception {

        mockAuthenticatedUser();

        when(combatFacadeService.startOrResumeCombat(userId, 5L))
                .thenReturn(CombatTestFactory.combatDTO(10L));

        mockMvc.perform(getPostRequestBuilder(baseAPIUrl + "/start/5", null))
                .andExpect(status().isOk());
    }

    // ============================================================
    // GET ACTIVE
    // ============================================================

    @Test
    @WithMockUser(username = "test@test.com")
    public void getActiveCombat_valid_returnsOk() throws Exception {

        mockAuthenticatedUser();

        when(combatFacadeService.getActiveCombat(userId))
                .thenReturn(Optional.of(CombatTestFactory.combatDTO(10L)));

        mockMvc.perform(get(baseAPIUrl + "/active")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com")
    public void getActiveCombat_noActive_returnsNoContent() throws Exception {

        mockAuthenticatedUser();

        when(combatFacadeService.getActiveCombat(userId))
                .thenReturn(Optional.empty());

        mockMvc.perform(get(baseAPIUrl + "/active")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    // ============================================================
    // EXECUTE TURN
    // ============================================================

    @Test
    @WithMockUser(username = "test@test.com")
    public void executeTurn_valid_returnsOk() throws Exception {

        mockAuthenticatedUser();

        when(combatFacadeService.executeTurn(userId, 10L, 7L))
                .thenReturn(CombatTestFactory.combatTurnResultDTO(10L));

        mockMvc.perform(getPostRequestBuilder(baseAPIUrl + "/10/skill/7", null))
                .andExpect(status().isOk());
    }

    // ============================================================
    // TIMEOUT
    // ============================================================

    @Test
    @WithMockUser(username = "test@test.com")
    public void timeOutCombat_valid_returnsOk() throws Exception {

        mockAuthenticatedUser();

        when(combatFacadeService.timeOut(userId, 10L))
                .thenReturn(CombatTestFactory.combatTurnResultDTO(10L));

        mockMvc.perform(getPostRequestBuilder(baseAPIUrl + "/10/timeout", null))
                .andExpect(status().isOk());
    }

    // ============================================================
    // ABANDON
    // ============================================================

    @Test
    @WithMockUser(username = "test@test.com")
    public void abandonCombat_valid_returnsOk() throws Exception {

        mockAuthenticatedUser();

        when(combatFacadeService.abandon(userId, 10L))
                .thenReturn(CombatTestFactory.combatTurnResultDTO(10L));

        mockMvc.perform(getPostRequestBuilder(baseAPIUrl + "/10/abandon", null))
                .andExpect(status().isOk());
    }
}
