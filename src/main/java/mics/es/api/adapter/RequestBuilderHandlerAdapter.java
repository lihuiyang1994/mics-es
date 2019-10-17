package mics.es.api.adapter;

import mics.es.api.RequestBuilderHandler;
import mics.es.api.builder.defaults.BaseXIndexBuilder;
import mics.es.api.builder.defaults.BaseXQueryBuilder;
import mics.es.api.dto.EsInfo;
import mics.es.api.executor.RequestExecutor;
import mics.es.api.helper.BaseEsInfoHelper;
import org.elasticsearch.action.index.IndexRequest;

public class RequestBuilderHandlerAdapter implements RequestBuilderHandler {

    protected BaseEsInfoHelper helper;

    protected BaseXQueryBuilder queryRequestBuilder;

    protected BaseXIndexBuilder indexRequestBuilder;


    @Override
    public void setQueryIndexRequestBuilder(BaseXQueryBuilder queryRequestBuilder) {
        this.queryRequestBuilder = queryRequestBuilder;
    }

    @Override
    public void setIndexRequestBuilder(BaseXIndexBuilder indexRequestBuilder) {
        this.indexRequestBuilder = indexRequestBuilder;
    }

    @Override
    public void setEsInfoHelper(BaseEsInfoHelper helper) {
        this.helper = helper;
    }

    @Override
    public BaseEsInfoHelper getHelper() {
        return this.helper;
    }

    @Override
    public RequestExecutor indexRequest(Object obj) {
        EsInfo esInfo = helper.getEsInfoByClass(obj.getClass());
        IndexRequest request = indexRequestBuilder.createRequest(esInfo);
        RequestExecutor executor = RequestExecutor.getInstance(request);
        return executor;
    }

    @Override
    public RequestExecutor indexRequestWithSource(Object obj) {

        return null;
    }

}
