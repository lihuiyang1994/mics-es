package mics.es.api.adapter;

import mics.es.api.BaseProperties;
import mics.es.api.BasePropertiesConvert;
import mics.es.api.ESRestClientFactory;
import mics.es.api.RequestBuilderHandler;
import mics.es.api.builder.DefaultRequestBuilderHandler;
import mics.es.api.client.ESGlobalClientInfo;
import mics.es.api.config.ESDefaultPropertiesConfigure;
import mics.es.api.config.ESServerConfigure;
import mics.es.api.convert.ESDefaultFilePropertiesConvert;
import mics.es.api.endpoint.PropertyEndpoint;
import mics.es.api.endpoint.UtilsEndpoint;
import mics.es.api.factory.DefaultESRestClientCreateFactory;
import mics.es.api.helper.BaseEsInfoHelper;
import mics.es.api.helper.EsInMemoryInfoHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

public class ESServerConfigureAdapter implements ESServerConfigure {
    protected  BaseEsInfoHelper helper;

    protected  RequestBuilderHandler requestBuilderHandler;

    protected  BaseProperties properties;

    protected  BasePropertiesConvert propertiesConvert;

    protected  ESRestClientFactory clientFactory;

    protected ESServerConfigureAdapter(BaseEsInfoHelper helper, RequestBuilderHandler requestBuilderHandler, BaseProperties properties, BasePropertiesConvert propertiesConvert, ESRestClientFactory clientFactory) {
        this.helper = helper;
        this.requestBuilderHandler = requestBuilderHandler;
        this.properties = properties;
        this.propertiesConvert = propertiesConvert;
        this.clientFactory = clientFactory;
    }

    public  ESServerConfigureAdapter(){

    }
    //配置有关于Es Client 创建相关的工具类，工厂信息，以及默认操作类
    @Override
    public void configure(UtilsEndpoint endpoint) throws Exception {

    }

    //配置有关于Es properties配置信息相关的属性
    @Override
    public void configure(PropertyEndpoint endpoint) throws Exception {

    }

    @Override
    public ESGlobalClientInfo clientInfo() {
        return null;
    }


    @Configuration
    @ConditionalOnMissingBean(BaseEsInfoHelper.class)
    protected static class BaseEsInfoHelperConfigure{

        @Bean
        public BaseEsInfoHelper esInfoHelper(){
            BaseEsInfoHelper helper = new EsInMemoryInfoHelper();
            return helper;
        }
    }

    @Configuration
    @ConditionalOnMissingBean(BaseProperties.class)
    @Import(ESDefaultPropertiesConfigure.class)
    protected static class BasePropertiesConfigure{
    }

    @Configuration
    @ConditionalOnMissingBean(BasePropertiesConvert.class)
    @ConditionalOnBean(BaseProperties.class)
    protected static class BasePropertiesConvertConfigure{

        @Bean
        public BasePropertiesConvert propertiesConvert(BaseProperties properties){
            return new ESDefaultFilePropertiesConvert(properties);
        }
    }

    @Configuration
    @ConditionalOnMissingBean(ESRestClientFactory.class)
    protected static class ESRestClientFactoryConfigure{
        @Bean
        public ESRestClientFactory esRestClientFactory(){
            return new DefaultESRestClientCreateFactory();
        }
    }
}
