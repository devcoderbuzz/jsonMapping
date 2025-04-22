package com.yourorg.jsontransform.model;

import java.util.Map;

public class JsonTransformResponse {
    private Map<String, String> result;

    public JsonTransformResponse(Map<String, String> result) {
        this.result = result;
    }

    public Map<String, String> getResult() {
        return result;
    }

    public void setResult(Map<String, String> result) {
        this.result = result;
    }
}