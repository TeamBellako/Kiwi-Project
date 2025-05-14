package com.kiwi.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class UsersTestHTTPUtils {
    public static MockHttpServletRequestBuilder getValidLogInPostRequestBuilder(String uri, LoginDTO loginDTO) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return post(uri)
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(loginDTO));
    }
}
