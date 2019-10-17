package mics.es.api.factory;

import mics.es.api.RequestExecutorFactory;
import mics.es.api.dto.EsInfo;
import mics.es.api.executor.RequestExecutor;
import org.elasticsearch.client.RestHighLevelClient;

public class DefaultRequestExecutorFactory implements RequestExecutorFactory {

    private RestHighLevelClient restHighLevelClient;

    public DefaultRequestExecutorFactory(RestHighLevelClient restHighLevelClient){
        this.restHighLevelClient = restHighLevelClient;
    }

    /**
     * create Request Executor
     * @param info EsInfo
     * @return
     */
    @Override
    public RequestExecutor createRequestExecutor(EsInfo info) {
        return new RequestExecutor(info);
    }

    public RestHighLevelClient getRestHighLevelClient() {
        return restHighLevelClient;
    }

    public void setRestHighLevelClient(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }
}
