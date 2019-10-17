package mics.es.api.config;

import mics.es.api.BaseProperties;
import mics.es.api.RequestBuilderHandler;
import mics.es.api.builder.DefaultRequestBuilderHandler;
import mics.es.api.builder.DefaultXIndexBuilder;
import mics.es.api.builder.DefaultXQueryBuilder;
import mics.es.api.convert.ESDefaultFilePropertiesConvert;
import mics.es.api.endpoint.PropertyEndpoint;
import mics.es.api.endpoint.UtilsEndpoint;
import mics.es.api.factory.DefaultESRestClientCreateFactory;
import mics.es.api.helper.EsInMemoryInfoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ESEndPointsConfigure {

    @Autowired
    private BaseProperties properties;

    public PropertyEndpoint propertyEndpoint() throws Exception {
        PropertyEndpoint endpoint = new PropertyEndpoint();
        endpoint.propertiesConvert(new ESDefaultFilePropertiesConvert())
                .properties(properties);
        return endpoint;
    }

    public UtilsEndpoint utilsEndpoint(){
        UtilsEndpoint endpoint = new UtilsEndpoint();
        endpoint.clientFactory(new DefaultESRestClientCreateFactory())
                .helper(new EsInMemoryInfoHelper())
                .requestBuilderHandler(new DefaultRequestBuilderHandler())
                .indexRequestBuilder(new DefaultXIndexBuilder())
                .queryRequestBuilder(new DefaultXQueryBuilder());
        return endpoint;
    }

}
