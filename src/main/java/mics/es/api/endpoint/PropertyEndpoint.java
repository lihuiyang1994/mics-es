package mics.es.api.endpoint;

import mics.es.api.BaseProperties;
import mics.es.api.BasePropertiesConvert;
import mics.es.api.client.ESGlobalClientInfo;
import mics.es.api.exception.NotFoundException;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.elasticsearch.client.NodeSelector;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

import java.util.Set;

public final class PropertyEndpoint {

    private BaseProperties properties;

    private BasePropertiesConvert propertiesConvert;


    private final ESGlobalClientInfo globalClientInfo = ESGlobalClientInfo.getInstance();

    public PropertyEndpoint properties(BaseProperties properties){
        this.properties = properties;
        return this;
    }

    public PropertyEndpoint propertiesConvert(BasePropertiesConvert propertiesConvert){
        this.propertiesConvert = propertiesConvert;
        return this;
    }

    public void initGlobalInfo() throws Exception {
        if (properties == null){
            throw new NotFoundException("Properties is not Found");
        }

        if (propertiesConvert == null){
            throw new NotFoundException("Properties Convert is not found");
        }

        propertiesConvert.setProperties(properties);
        bindGlobalClientInfo();
    }

    private void bindGlobalClientInfo() throws Exception {

        HttpHost[] httpHosts = propertiesConvert.convertServerHttpHost();
        globalClientInfo.setHosts(httpHosts);

        RestClient.FailureListener listener = propertiesConvert.convertFailureListener();
        if (listener != null){
            globalClientInfo.setFailureListener(listener);
        }

        NodeSelector nodeSelector = propertiesConvert.convertNodeSelector();
        if (nodeSelector!=null){
            globalClientInfo.setNodeSelector(nodeSelector);
        }

        RestClientBuilder.HttpClientConfigCallback httpClientConfigCallback = propertiesConvert.convertHttpClientConfigCallback();
        if (httpClientConfigCallback != null){
            globalClientInfo.setHttpClientConfigCallback(httpClientConfigCallback);
        }

        RestClientBuilder.RequestConfigCallback requestConfigCallback = propertiesConvert.convertRequestConfigCallback();
        if (requestConfigCallback != null){
            globalClientInfo.setRequestConfigCallback(requestConfigCallback);
        }

        Set<Header> headers = propertiesConvert.convertHeaders();
        globalClientInfo.setHeaders(headers);

        Set<Class> classes = propertiesConvert.doScanEsMapping();
        globalClientInfo.setMappingClasses(classes);
    }

    public ESGlobalClientInfo global() {
        globalClientInfo.currentPoint(this);
        return this.globalClientInfo;
    }

    public BaseProperties getProperties() {
        return properties;
    }

    public BasePropertiesConvert getPropertiesConvert() {
        return propertiesConvert;
    }

}
