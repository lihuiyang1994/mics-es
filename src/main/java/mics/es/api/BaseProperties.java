package mics.es.api;

import mics.es.api.properties.ESClientProperties;

import java.util.Map;

/**
 * the top class of BaseProperties
 */
public abstract class BaseProperties {

    public abstract String[] getBasePackage();

    public abstract Map<String, ESClientProperties> getClient();

    public abstract Map<String , String> getHeaders();

    public abstract String getFailureListener();

    public abstract String getNodeSelector();

    public abstract String getRequestConfigCallback();

    public abstract String getHttpClientConfigCallback();
}
