package mics.es.api.config;

import mics.es.api.properties.ESFileProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;


@Configuration
//Open the ElasticSearch Config
@EnableConfigurationProperties(ESFileProperties.class)
@Order(0)
public class ESDefaultPropertiesConfigure implements ESPropertiesConfigure {

}
