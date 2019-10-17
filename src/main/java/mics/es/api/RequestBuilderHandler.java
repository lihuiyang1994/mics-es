package mics.es.api;

import mics.es.api.builder.defaults.BaseXIndexBuilder;
import mics.es.api.builder.defaults.BaseXQueryBuilder;
import mics.es.api.executor.RequestExecutor;
import mics.es.api.helper.BaseEsInfoHelper;

/**
 *
 */
public interface RequestBuilderHandler {

    void setQueryIndexRequestBuilder(BaseXQueryBuilder queryRequestBuilder);

    void setIndexRequestBuilder(BaseXIndexBuilder indexRequestBuilder);

    void setEsInfoHelper(BaseEsInfoHelper helper);

    BaseEsInfoHelper getHelper();

    RequestExecutor indexRequest(Object obj);

    RequestExecutor indexRequestWithSource(Object obj);
}
