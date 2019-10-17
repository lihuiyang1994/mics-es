package mics.es.api.builder;

import mics.es.api.dto.EsInfo;
import mics.es.api.builder.defaults.BaseXIndexBuilder;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.index.IndexRequest;

public class DefaultXIndexBuilder extends BaseXIndexBuilder {

    @Override
    public IndexRequest createRequest(EsInfo info) {
        IndexRequest request = new IndexRequest(info.getIndex());
        //routing
        if (StringUtils.isNotBlank(info.getRouting())){
            request.routing(info.getRouting());
        }
        //timeout
        if (StringUtils.isNotBlank(info.getTimeout())){
            request.timeout(info.getTimeout());
        }
        //refreshPolicy
        String refreshPolicy = info.getRefreshPolicy();
        if (StringUtils.isNotBlank(refreshPolicy)){
            request.setRefreshPolicy(refreshPolicy);
        }
        //version
        Integer version = info.getVersion();
        if (version != null){
            request.version(version);
        }
        String opType = info.getOpType();
        if (StringUtils.isNotBlank(opType)){
            request.opType(opType);
        }
        String pipeline = info.getPipeline();
        if (StringUtils.isNotBlank(pipeline)){
            request.setPipeline(pipeline);
        }
        return request;
    }
}
