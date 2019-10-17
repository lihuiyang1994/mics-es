package mics.es.api.utils;

import lombok.Data;

import java.util.Map;

@Data
public class ParameterSet {

    private Object idValue;

    private Map<String , Object> parameterMap;

    public ParameterSet(Map<String, Object> parameterMap) {
        this.parameterMap = parameterMap;
    }

    public ParameterSet(Object idValue) {
        this.idValue = idValue;
    }

    public ParameterSet(Object idValue, Map<String, Object> parameterMap) {
        this.idValue = idValue;
        this.parameterMap = parameterMap;
    }

    public ParameterSet() {
    }
}
