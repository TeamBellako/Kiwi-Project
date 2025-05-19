package com.kiwi.users;

import com.kiwi.exception.GlobalExceptionHandler;
import com.kiwi.security.AuthEntryPointJwt;
import com.kiwi.security.CustomUserDetailsService;
import com.kiwi.security.JwtUtils;
import com.kiwi.security.WebSecurityConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static com.kiwi.users.UsersTestFactory.validUserDTO;
import static com.kiwi.users.UsersTestHTTPUtils.getPostRequestBuilder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(UsersController.class)
@Import({ GlobalExceptionHandler.class, WebSecurityConfig.class})
public class UsersControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtils jwtUtils;
    @MockitoBean
    private CustomUserDetailsService userDetailsService;
    @MockitoBean
    private AuthEntryPointJwt authEntryPointJwt;

    @MockitoBean
    private UsersService usersService;

    private final String baseAPIUrl = "/api/public";
    
    @Test
    public void validSignup() throws Exception {
        LoginDTO loginDTO = new LoginDTO(validUserDTO().getEmail(), validUserDTO().getPassword());
        mockMvc.perform(getPostRequestBuilder(baseAPIUrl + "/signup", loginDTO))
                .andExpect(status().isCreated());
    }
    
    @Test
    public void validLogin() throws Exception {
        String mockToken = "myToken";
        when(jwtUtils.generateToken(anyString())).thenReturn(mockToken);
        when(usersService.getUserByEmail(any(Email.class))).thenReturn(Optional.of(validUserDTO()));

        LoginDTO loginDTO = new LoginDTO(validUserDTO().getEmail(), validUserDTO().getPassword());
        mockMvc.perform(getPostRequestBuilder(baseAPIUrl + "/login", loginDTO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwt").value(mockToken));
    }
    
    
}
