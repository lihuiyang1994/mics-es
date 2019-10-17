package mics.es.api.properties;

import lombok.Data;
import mics.es.api.BaseProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author lhy
 */
import java.util.*;

@Data
@ConfigurationProperties(prefix = "mics.elasticsearch.http")
public class ESFileProperties extends BaseProperties {

    /**
     * Mapping地址
     */
    private String[] basePackage;

    /**
     * Map of ESClientProperties names to properties.
     */
    private final Map<String , ESClientProperties> client  = new HashMap<>();

    /**
     * the default setHeaders that need to be sent with each request
     */
    private Map<String,String> headers = new LinkedHashMap<>();

    /**
     * the class path of setFailureListener
     */
    private String failureListener;

    /**
     * Selects nodes that can receive requests. Used to keep requests away
     * from master nodes or to send them to nodes with a particular attribute.
     */
    private String nodeSelector;

    /**
     * Set a callback that allows to modify the default request configuration
     * (e.g. request timeouts, authentication, or anything that the
     * org.apache.http.client.config.RequestConfig.Builder allows to set)          )
     */
    private String requestConfigCallback;

    /**
     * Set a callback that allows to modify the http client configuration
     * (e.g. encrypted communication over ssl, or anything that the
     * org.apache.http.impl.nio.client.HttpAsyncClientBuilder allows to set)
     */
    private String httpClientConfigCallback;


    @Override
    public String[] getBasePackage(){
        return this.basePackage;
    };

    @Override
    public Map<String, ESClientProperties> getClient() {
        return client;
    }
    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }
    @Override
    public String getFailureListener() {
        return failureListener;
    }
    @Override
    public String getNodeSelector() {
        return nodeSelector;
    }
    @Override
    public String getRequestConfigCallback() {
        return requestConfigCallback;
    }
    @Override
    public String getHttpClientConfigCallback() {
        return httpClientConfigCallback;
    }

}