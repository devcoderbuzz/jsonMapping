package com.yourorg.jsontransform.model;

import java.util.Map;

public class JsonTransformRequest {
    private Map<String, Object> sourceJson;
    private Map<String, String> mappingJson;

    public Map<String, Object> getSourceJson() {
        return sourceJson;
    }

    public void setSourceJson(Map<String, Object> sourceJson) {
        this.sourceJson = sourceJson;
    }

    public Map<String, String> getMappingJson() {
        return mappingJson;
    }

    public void setMappingJson(Map<String, String> mappingJson) {
        this.mappingJson = mappingJson;
    }
}