package mics.es.api.config;

import mics.es.api.client.ESGlobalClientInfo;
import mics.es.api.endpoint.PropertyEndpoint;
import mics.es.api.endpoint.UtilsEndpoint;

public interface ESServerConfigure{

    void configure(UtilsEndpoint endpoint) throws Exception;

    void configure(PropertyEndpoint endpoint) throws Exception;

    ESGlobalClientInfo clientInfo();
}
