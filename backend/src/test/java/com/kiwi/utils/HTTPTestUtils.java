package com.kiwi.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.users.LoginDTO;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

public class HTTPTestUtils {
    public static MockHttpServletRequestBuilder getPostRequestBuilder(String uri, Object content) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(content));
    }

    public static MockHttpServletRequestBuilder getPutRequestBuilder(String uri, Object content) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return put(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(content));
    }
}
