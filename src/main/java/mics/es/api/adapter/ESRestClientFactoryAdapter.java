package mics.es.api.adapter;

import mics.es.api.BaseProperties;
import mics.es.api.BasePropertiesConvert;
import mics.es.api.ESRestClientFactory;
import mics.es.api.client.ESGlobalClientInfo;
import org.elasticsearch.client.RestHighLevelClient;

public class ESRestClientFactoryAdapter implements ESRestClientFactory {
    @Override
    public RestHighLevelClient createRestHighLevelClientAndStorMappingInfo(ESGlobalClientInfo clientInfo) throws Exception {
        return null;
    }
}
