package mics.es.api;

import mics.es.api.client.ESGlobalClientInfo;
import mics.es.api.config.DefaultESServerConfigure;
import mics.es.api.config.ESDefaultPropertiesConfigure;
import mics.es.api.config.ESEndPointsConfigure;
import mics.es.api.config.ESServerConfigure;
import mics.es.api.endpoint.PropertyEndpoint;
import mics.es.api.endpoint.UtilsEndpoint;
import mics.es.api.helper.BaseEsInfoHelper;
import mics.es.api.properties.ESFileProperties;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 *
 */
@Configuration
//Verify that ESClientProperties exists
@Import({ DefaultESServerConfigure.class, ESEndPointsConfigure.class,ESDefaultPropertiesConfigure.class})
@EnableAspectJAutoProxy
@Order(0)
public class ESAutoConfiguration {

    @Autowired
    private ESServerConfigure configure ;

    @Autowired
    private ESEndPointsConfigure pointsConfigure;

    @Bean
    public PropertyEndpoint propertyEndpoint(BaseEsInfoHelper helper) throws Exception {
        PropertyEndpoint endpoint = pointsConfigure.propertyEndpoint();
        configure.configure(endpoint);
        endpoint.initGlobalInfo();
        ESGlobalClientInfo info = ESGlobalClientInfo.getInstance();
        Set<Class> mappingSource = info.getMappingSource();
        if (CollectionUtils.isNotEmpty(mappingSource)){
            for (Class clazz : mappingSource) {
                helper.storeEsInfoByClass(clazz);
            }
        }
        return endpoint;
    }

    @Bean
    public UtilsEndpoint utilsEndpoint() throws Exception {
        UtilsEndpoint endpoint = pointsConfigure.utilsEndpoint();
        configure.configure(endpoint);
        return endpoint;
    }

    @Bean(destroyMethod = "close")
    public RestHighLevelClient client(UtilsEndpoint utilsEndpoint) throws Exception {
        ESRestClientFactory clientFactory = utilsEndpoint.getClientFactory();
        RestHighLevelClient client = clientFactory.createRestHighLevelClientAndStorMappingInfo(ESGlobalClientInfo.getInstance());
        return client;
    }


    @Bean
    public RestClient restClient(RestHighLevelClient client){
        return client.getLowLevelClient();
    }

    @Bean
    @Order(-1)
    @ConditionalOnBean({UtilsEndpoint.class,RestHighLevelClient.class})
    public RequestBuilderHandler requestBuilder(UtilsEndpoint endpoint){
        endpoint.initRequestBuilderHandler();
        RequestBuilderHandler handler = endpoint.getRequestBuilderHandler();
        return handler;
    }


}
