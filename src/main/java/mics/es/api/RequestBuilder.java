package mics.es.api;


import mics.es.api.dto.EsInfo;
import org.elasticsearch.action.ActionRequest;

/**
 * top of the Request Builder
 *  all in directory
 */
public interface RequestBuilder<T extends ActionRequest> {

     T createRequest(EsInfo info);
}
