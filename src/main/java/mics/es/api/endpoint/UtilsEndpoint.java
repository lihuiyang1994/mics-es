package mics.es.api.endpoint;

import mics.es.api.ESRestClientFactory;
import mics.es.api.RequestBuilderHandler;
import mics.es.api.builder.defaults.BaseXIndexBuilder;
import mics.es.api.builder.defaults.BaseXQueryBuilder;
import mics.es.api.helper.BaseEsInfoHelper;

import java.util.Objects;

public class UtilsEndpoint {

    private BaseEsInfoHelper helper;

    private ESRestClientFactory clientFactory;

    private RequestBuilderHandler requestBuilderHandler;

    private BaseXIndexBuilder indexRequestBuilder;

    private BaseXQueryBuilder queryRequestBuilder;

    public UtilsEndpoint helper(BaseEsInfoHelper helper) {
        this.helper = helper;
        return this;
    }

    public UtilsEndpoint clientFactory(ESRestClientFactory clientFactory) {
        this.clientFactory = clientFactory;
        return this;
    }

    public UtilsEndpoint requestBuilderHandler(RequestBuilderHandler requestBuilderHandler) {
        this.requestBuilderHandler = requestBuilderHandler;
        return this;
    }

    public RequestBuilderHandler getRequestBuilderHandler() {
        return requestBuilderHandler;
    }

    public ESRestClientFactory getClientFactory() {
        return clientFactory;
    }

    public BaseEsInfoHelper getHelper() {
        return helper;
    }

    public UtilsEndpoint indexRequestBuilder(BaseXIndexBuilder indexRequestBuilder) {
        this.indexRequestBuilder = indexRequestBuilder;
        return this;
    }

    public UtilsEndpoint queryRequestBuilder(BaseXQueryBuilder queryRequestBuilder) {
        this.queryRequestBuilder = queryRequestBuilder;
        return this;
    }

    public BaseXIndexBuilder getIndexRequestBuilder() {
        return indexRequestBuilder;
    }

    public BaseXQueryBuilder getQueryRequestBuilder() {
        return queryRequestBuilder;
    }

    public void initRequestBuilderHandler() {
        if (Objects.isNull(requestBuilderHandler)){
            throw new NullPointerException("UtilsEndpoint.requestBuilderHandler is null");
        }
        requestBuilderHandler.setIndexRequestBuilder(indexRequestBuilder);
        requestBuilderHandler.setQueryIndexRequestBuilder(queryRequestBuilder);
        requestBuilderHandler.setEsInfoHelper(helper);
    }
}
