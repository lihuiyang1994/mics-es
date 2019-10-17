package mics.es.api.adapter;

import mics.es.api.BaseProperties;
import mics.es.api.BasePropertiesConvert;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.elasticsearch.client.NodeSelector;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;

import java.util.HashSet;
import java.util.Set;

public class BasePropertiesConvertAdapter extends BasePropertiesConvert {

    public BasePropertiesConvertAdapter(BaseProperties baseProperties) {
        super(baseProperties);
    }

    public BasePropertiesConvertAdapter(){}

    @Override
    public HttpHost[] convertServerHttpHost() {
        return new HttpHost[0];
    }

    @Override
    public Set<Header> convertHeaders() {
        return new HashSet<Header>();
    }

    @Override
    public RestClient.FailureListener convertFailureListener() throws Exception {
        return null;
    }

    @Override
    public NodeSelector convertNodeSelector() throws Exception {
        return null;
    }

    @Override
    public RestClientBuilder.RequestConfigCallback convertRequestConfigCallback() throws Exception {
        return null;
    }

    @Override
    public RestClientBuilder.HttpClientConfigCallback convertHttpClientConfigCallback() throws Exception {
        return null;
    }



}
