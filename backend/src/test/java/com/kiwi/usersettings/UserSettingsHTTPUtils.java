package com.kiwi.usersettings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class UserSettingsHTTPUtils {
    public static @NotNull MockHttpServletRequestBuilder getPOSTRequestContent(String baseAPIUrl, UserSettingsDTO userSettingsDTO) throws JsonProcessingException {
        return post(baseAPIUrl)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeUserSettingIntoJSON(userSettingsDTO));
    }

    public static @NotNull MockHttpServletRequestBuilder getPUTRequestContent(String baseAPIUrl, UserSettingsDTO userSettingsDTO) throws JsonProcessingException {
        return put(baseAPIUrl)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeUserSettingIntoJSON(userSettingsDTO));
    }

    private static String serializeUserSettingIntoJSON(UserSettingsDTO userSettingsDTO) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(userSettingsDTO);
    }

    public static @NotNull ResultMatcher getUserSettingsResultMatcher(@NotNull UserSettingsDTO userSettingsDTO) {
        return jsonPath("$.email").value(userSettingsDTO.getEmail());
    }
}
