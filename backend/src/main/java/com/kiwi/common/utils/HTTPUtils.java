package com.kiwi.common.utils;

import java.util.HashMap;
import java.util.Map;

public class HTTPUtils {
    public static Map<String, String> createSuccessResponseBody(String message) {
        Map<String, String> errorBody = new HashMap<>();
        errorBody.put("message", message);
        return errorBody;
    }
    public static Map<String, String> createErrorResponseBody(String message) {
        Map<String, String> errorBody = new HashMap<>();
        errorBody.put("error", message);
        return errorBody;
    }
}
