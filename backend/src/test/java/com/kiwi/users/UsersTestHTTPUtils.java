package com.kiwi.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class UsersTestHTTPUtils {
    public static MockHttpServletRequestBuilder getPostRequestBuilder(String uri, LoginDTO loginDTO) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(loginDTO));
    }
}
