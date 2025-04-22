package com.yourorg.jsontransform.service;

import com.yourorg.jsontransform.model.JsonTransformRequest;
import com.yourorg.jsontransform.util.PlaceholderResolver;

import java.util.HashMap;
import java.util.Map;

public class JsonTransformService {

    public Map<String, String> transform(JsonTransformRequest request) {
        Map<String, Object> source = request.getSourceJson();
        Map<String, String> mapping = request.getMappingJson();
        Map<String, String> result = new HashMap<>();

        for (Map.Entry<String, String> entry : mapping.entrySet()) {
            String key = entry.getKey();
            String template = entry.getValue();
            String resolved = PlaceholderResolver.resolve(template, source);
            result.put(key, resolved);
        }

        return result;
    }
}