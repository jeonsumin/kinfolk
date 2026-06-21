package com.terry.backend.core.messages.handler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMessages {
    private boolean success;
    private Map<String, String> data;
    private String messages;

    public static ResponseMessages success() {
        return new ResponseMessages(true, new LinkedHashMap<>(), "");
    }

    public static ResponseMessages success(String data) {
        return new ResponseMessages(true, new LinkedHashMap<>(), data);
    }

    public static ResponseMessages success(String field, String message) {
        Map<String, String> messages = new LinkedHashMap<>();
        messages.put(field, message);
        return new ResponseMessages(true, messages, "");
    }

    public static ResponseMessages success(Map<String, String> messages) {
        return new ResponseMessages(true, messages, "");
    }

    public static ResponseMessages fail() {
        return new ResponseMessages(false, new LinkedHashMap<>(), "");
    }

    public static ResponseMessages fail(String field, String message) {
        Map<String, String> errors = new LinkedHashMap<>();
        errors.put(field, message);
        return new ResponseMessages(false, errors, "");
    }

    public static ResponseMessages fail(String data) {
        return new ResponseMessages(false, new LinkedHashMap<>(), data);
    }

}
