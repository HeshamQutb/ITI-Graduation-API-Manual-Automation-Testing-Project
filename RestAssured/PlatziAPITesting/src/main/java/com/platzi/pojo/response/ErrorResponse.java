package com.platzi.pojo.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse {
    private Object message;
    private String error;
    private Integer statusCode;

    public ErrorResponse() {}

    public Object getMessage() { return message; }
    public void setMessage(Object message) { this.message = message; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public Integer getStatusCode() { return statusCode; }
    public void setStatusCode(Integer statusCode) { this.statusCode = statusCode; }

    // Helper methods
    public String getMessageAsString() {
        if (message instanceof String) {
            return (String) message;
        } else if (message instanceof List) {
            return String.join(", ", (List<String>) message);
        }
        return message != null ? message.toString() : null;
    }

    @SuppressWarnings("unchecked")
    public List<String> getMessageAsList() {
        if (message instanceof List) {
            return (List<String>) message;
        } else if (message instanceof String) {
            return List.of((String) message);
        }
        return List.of();
    }
}