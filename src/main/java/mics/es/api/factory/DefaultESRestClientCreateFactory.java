package mics.es.api.factory;

import mics.es.api.BaseProperties;
import mics.es.api.BasePropertiesConvert;
import mics.es.api.ESRestClientFactory;
import mics.es.api.client.ESGlobalClientInfo;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

import java.util.Objects;
import java.util.Set;

/**
 * the default rest client factory
 */
public class DefaultESRestClientCreateFactory implements ESRestClientFactory {

    private BasePropertiesConvert basePropertiesConvert;


    @Override
    public RestHighLevelClient createRestHighLevelClientAndStorMappingInfo(ESGlobalClientInfo clientInfo) throws Exception {
        RestClientBuilder builder = RestClient.builder(clientInfo.getHosts());

        if (!Objects.isNull(clientInfo.getRequestConfigCallback())){
            builder.setRequestConfigCallback(clientInfo.getRequestConfigCallback());
        }

        if (!Objects.isNull(clientInfo.getHeaders())){
            builder.setDefaultHeaders(clientInfo.getHeaders());
        }

        if (!Objects.isNull(clientInfo.getHttpClientConfigCallback())){
            builder.setHttpClientConfigCallback(clientInfo.getHttpClientConfigCallback());
        }

        if (!Objects.isNull(clientInfo.getListener())){
            builder.setFailureListener(clientInfo.getListener());
        }

        if (!Objects.isNull(clientInfo.getNodeSelector())){
            builder.setNodeSelector(clientInfo.getNodeSelector());
        }

        RestHighLevelClient client = new RestHighLevelClient(builder);
        return client;
    }

    /**
     *
     */
    public void classScanToStoreESMappingInfo(){
    }
}
