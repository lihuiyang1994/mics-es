package mics.es.api;

import mics.es.api.client.ESGlobalClientInfo;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * Top Class for ESClientFactory
 */
public interface ESRestClientFactory {
    /**
     * get RestHighLevelClient
     * @param clientInfo the Global info For ElasticSearch  linkage information
     * @return
     */
    RestHighLevelClient createRestHighLevelClientAndStorMappingInfo(ESGlobalClientInfo clientInfo)throws Exception;

}
