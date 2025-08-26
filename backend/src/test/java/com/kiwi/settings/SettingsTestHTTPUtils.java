package com.kiwi.settings;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kiwi.features.settings.data.SettingsDTO;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class SettingsTestHTTPUtils {
    public static @NotNull MockHttpServletRequestBuilder getPUTRequestContent(
            String baseAPIUrl,
            SettingsDTO settingsDTO
    ) throws JsonProcessingException {
        return put(baseAPIUrl)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(serializeUserSettingIntoJSON(settingsDTO));
    }

    private static String serializeUserSettingIntoJSON(SettingsDTO settingsDTO) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(settingsDTO);
    }

    public static @NotNull ResultMatcher getSettingsResultMatcher(@NotNull SettingsDTO settingsDTO) {
        return jsonPath("$.email").value(settingsDTO.getEmail());
    }
}
