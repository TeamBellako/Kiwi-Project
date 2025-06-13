package com.kiwi.common;

import java.util.HashMap;
import java.util.Map;

public class HTTPUtils {
    public static Map<String, String> createErrorResponseBody(String message) {
        Map<String, String> errorBody = new HashMap<>();
        errorBody.put("error", message);
        return errorBody;
    }
}
