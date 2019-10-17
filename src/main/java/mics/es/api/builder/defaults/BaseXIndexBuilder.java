package mics.es.api.builder.defaults;

import mics.es.api.RequestBuilder;
import mics.es.api.dto.EsInfo;
import org.elasticsearch.action.index.IndexRequest;

public abstract class BaseXIndexBuilder implements RequestBuilder<IndexRequest> {

    @Override
    public abstract IndexRequest createRequest(EsInfo info);
}
