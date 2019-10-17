package mics.es.api.config;

import mics.es.api.adapter.ESServerConfigureAdapter;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnMissingBean({ESServerConfigure.class})
@AutoConfigureAfter(ESDefaultPropertiesConfigure.class)
public class DefaultESServerConfigure extends ESServerConfigureAdapter {

}
