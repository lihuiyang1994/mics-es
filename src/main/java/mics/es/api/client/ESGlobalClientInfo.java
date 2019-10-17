package mics.es.api.client;

import mics.es.api.BasePropertiesConvert;
import mics.es.api.endpoint.PropertyEndpoint;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.NodeSelector;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.common.util.set.Sets;

import java.util.Set;

public class ESGlobalClientInfo {

    private HttpHost[] hosts;

    private Set<Header> headers = Sets.newConcurrentHashSet();

    private RestClient.FailureListener listener;

    private NodeSelector nodeSelector;

    private RestClientBuilder.HttpClientConfigCallback httpClientConfigCallback;

    private RestClientBuilder.RequestConfigCallback requestConfigCallback;

    private PropertyEndpoint endpoint;

    private Set<Class> mappingSource;

    public static ESGlobalClientInfo getInstance(){
        return SingleHolder.CLIENT_INFO;
    }

    private ESGlobalClientInfo(){}

    public void currentConfig(PropertyEndpoint endpoint) {
        this.endpoint = endpoint;
    }

    public PropertyEndpoint end(){
        PropertyEndpoint point = endpoint;
        endpoint = null;
        return point;
    }

    public void currentPoint(PropertyEndpoint propertyEndpoint) {
        this.endpoint = propertyEndpoint;
    }

    public void setHeaders(Set<Header> headers) {
        this.headers = headers;
    }

    public void setHosts(HttpHost[] httpHosts) {
        this.hosts = httpHosts;
    }

    public void setHttpClientConfigCallback(RestClientBuilder.HttpClientConfigCallback httpClientConfigCallback) {
        this.httpClientConfigCallback = httpClientConfigCallback;
    }



    public void setFailureListener(RestClient.FailureListener listener) {
        this.listener = listener;
    }

    public void setNodeSelector(NodeSelector nodeSelector) {
        this.nodeSelector = nodeSelector;
    }

    public void setRequestConfigCallback(RestClientBuilder.RequestConfigCallback requestConfigCallback) {
        this.requestConfigCallback = requestConfigCallback;
    }

    public void setMappingClasses(Set<Class> classes) {
        this.mappingSource = classes;
    }

    private static class SingleHolder{
        private static final ESGlobalClientInfo CLIENT_INFO = new ESGlobalClientInfo();
    }

    public ESGlobalClientInfo appendHeard(BasicHeader header){
        headers.add(header);
        return this;
    }

    public ESGlobalClientInfo appendHeard(String head, String value){
        BasicHeader header = new BasicHeader(head,value);
        headers.add(header);
        return this;
    }

    public HttpHost[] getHosts() {
        return hosts;
    }

    public Set<Class> getMappingSource() {
        return mappingSource;
    }

    public RestClient.FailureListener getListener() {
        return listener;
    }

    public RestClientBuilder.HttpClientConfigCallback getHttpClientConfigCallback() {
        return httpClientConfigCallback;
    }

    public NodeSelector getNodeSelector() {
        return nodeSelector;
    }

    public PropertyEndpoint getEndpoint() {
        return endpoint;
    }

    public RestClientBuilder.RequestConfigCallback getRequestConfigCallback() {
        return requestConfigCallback;
    }

    public Header[] getHeaders() {
        return copyFromCollection(this.headers);
    }

    public Header[] copyFromCollection(Set<Header> headers){
        if (CollectionUtils.isEmpty(headers)){
            return new BasicHeader[0];
        }
        Header[] headerArr = new Header[headers.size()];
        int i = 0;
        for (Header header : headers) {
            headerArr[i] = header;
            i++;
        }
        return headerArr;
    }
}
