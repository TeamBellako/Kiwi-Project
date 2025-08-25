package com.kiwi.personality;

import com.c4_soft.springaddons.security.oauth2.test.webmvc.AutoConfigureAddonsWebmvcResourceServerSecurity;
import com.kiwi.config.WebSecurityConfig;
import com.kiwi.features.personality.PersonalityController;
import com.kiwi.features.personality.PersonalityNotFoundException;
import com.kiwi.features.personality.PersonalityService;
import com.kiwi.features.users.*;
import com.kiwi.security.AuthEntryPointJwt;
import com.kiwi.security.JwtUtils;
import com.kiwi.utils.GlobalExceptionHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.kiwi.personality.PersonalityTestFactory.*;
import static com.kiwi.users.UsersTestFactory.invalidUserDTO;
import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static com.kiwi.utils.HTTPTestUtils.getPostRequestBuilder;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(PersonalityController.class)
@AutoConfigureAddonsWebmvcResourceServerSecurity
@Import({ GlobalExceptionHandler.class, WebSecurityConfig.class })
public class PersonalityControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private JwtUtils jwtUtils;
    @MockitoBean
    private CustomUserDetailsService userDetailsService;
    @MockitoBean
    private AuthEntryPointJwt authEntryPointJwt;

    @MockitoBean
    private PersonalityService personalityService;
    
    private final String baseAPIUrl = "/api/user/personality";


    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void getPersonality_valid() throws Exception {
        when(personalityService.getPersonality(validUserDTO().getEmail())).thenReturn(validPersonality());
        mockMvc.perform(get(baseAPIUrl)).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "football")
    public void getPersonality_notFound() throws Exception {
        when(personalityService.getPersonality(invalidUserDTO().getEmail())).thenThrow(new PersonalityNotFoundException(invalidUserDTO().getEmail()));
        mockMvc.perform(get(baseAPIUrl)).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void updateRealName() throws Exception {
        when(personalityService.updateRealName(validUserDTO().getEmail(), userNameRealDTO())).thenReturn(validPersonalityDTO());
        mockMvc.perform(getPostRequestBuilder(baseAPIUrl + "/build", userNameRealDTO()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void updateKnightName() throws Exception {
        when(personalityService.updateKnightName(validUserDTO().getEmail(), userNameKnightDTO())).thenReturn(validPersonalityDTO());
        mockMvc.perform(getPostRequestBuilder(baseAPIUrl + "/build", userNameKnightDTO()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void updateBuild() throws Exception {
        when(personalityService.updateBuild(validUserDTO().getEmail(), buildDTO())).thenReturn(validPersonalityDTO());
        mockMvc.perform(getPostRequestBuilder(baseAPIUrl + "/build", buildDTO()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "finn@thehuman.com")
    public void updateApps() throws Exception {
        when(personalityService.updateApps(validUserDTO().getEmail(), appsDTO())).thenReturn(validPersonalityDTO());
        mockMvc.perform(getPostRequestBuilder(baseAPIUrl + "/apps", appsDTO()))
                .andExpect(status().isOk());
    }
}
