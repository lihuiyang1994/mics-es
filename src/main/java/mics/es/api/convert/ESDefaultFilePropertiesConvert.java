package mics.es.api.convert;

import mics.es.api.BaseProperties;
import mics.es.api.adapter.BasePropertiesConvertAdapter;
import mics.es.api.properties.ESClientProperties;
import mics.es.api.enums.NodeSelectorEnum;
import mics.es.api.exception.InvalidValueException;
import mics.es.api.utils.Maps;
import mics.es.api.utils.Strings;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.*;

import java.io.InvalidClassException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author lhy
 *  the default ES properties convert class
 */
public class ESDefaultFilePropertiesConvert extends BasePropertiesConvertAdapter {

    private static final Map<String, ESClientProperties> DEFAULT_CLIENT_INFO = Maps.createSingleResultHashMap("defalut",ESClientProperties.createOne("http://localhost:9200"));

    public ESDefaultFilePropertiesConvert(BaseProperties baseProperties) {
        super(baseProperties);
    }

    public ESDefaultFilePropertiesConvert(){}


    @Override
    public HttpHost[] convertServerHttpHost() {
        Map<String, ESClientProperties> client = this.properties.getClient();
        if (CollectionUtils.isEmpty(client.keySet())){
            client = DEFAULT_CLIENT_INFO;
        }

        return convertClientPropertiesToHttpHost(client);
    }

    @Override
    public Set<Header> convertHeaders() {
        Map<String, String> headers = this.properties.getHeaders();
        Set<String> keys = headers.keySet();
//        Header[] requestHeaders = new Header[keys.size()];
        Set<Header> requestHeaders = new HashSet<>();
        if (CollectionUtils.isNotEmpty(keys)){
            for (String header: keys) {
                String value = headers.get(header);
                Header h = new BasicHeader(header, value);
                requestHeaders.add(h);
            }
        }
        return requestHeaders;
    }

    @Override
    public RestClient.FailureListener convertFailureListener()throws Exception {
        String failureListener = this.properties.getFailureListener();
        if (StringUtils.isNotBlank(failureListener)) {
            Class<?> clazz = Class.forName(failureListener);
            Object o = clazz.newInstance();
            if (o instanceof RestClient.FailureListener) {
                return (RestClient.FailureListener) o;
            } else {
                throw new InvalidClassException(Strings.forStr(failureListener,"must instanceof the org.elasticsearch.client.RestClient.FailureListener"));
            }
        }
        return null;
    }

    @Override
    public NodeSelector convertNodeSelector() throws Exception{
        String nodeSelector = this.properties.getNodeSelector();
        if (StringUtils.isBlank(nodeSelector)){
            return null;
        }
        NodeSelectorEnum nodeSelectorEnum = NodeSelectorEnum.valueOf(nodeSelector);
        if (nodeSelectorEnum==null){
            throw new InvalidValueException(Strings.forStr(nodeSelector,"is not support(ANY or SKIP_DEDICATED_MASTERS)"));
        }
        if ("ANY".equalsIgnoreCase(nodeSelectorEnum.name())){
            return NodeSelector.ANY;
        }

        if ("SKIP_DEDICATED_MASTERS".equalsIgnoreCase(nodeSelectorEnum.name())){
            return NodeSelector.SKIP_DEDICATED_MASTERS;
        }
        return null;
    }

    @Override
    public RestClientBuilder.RequestConfigCallback convertRequestConfigCallback() throws Exception{
        String requestConfigCallbackClassPath = this.properties.getRequestConfigCallback();
        if (StringUtils.isNotBlank(requestConfigCallbackClassPath)){
            Object o = Class.forName(requestConfigCallbackClassPath).newInstance();
            if (o instanceof RestClientBuilder.RequestConfigCallback){
                return (RestClientBuilder.RequestConfigCallback)o;
            }else {
                throw new InvalidClassException(requestConfigCallbackClassPath,"must instanceof org.elasticsearch.client.RestClientBuilder.RequestConfigCallback");
            }
        }
        return null;
    }

    @Override
    public RestClientBuilder.HttpClientConfigCallback convertHttpClientConfigCallback() throws Exception {
        String httpClientConfigCallbackClassPath = this.properties.getHttpClientConfigCallback();
        if (StringUtils.isNotBlank(httpClientConfigCallbackClassPath)){
            Object o = Class.forName(httpClientConfigCallbackClassPath).newInstance();
            if (o instanceof RestClientBuilder.HttpClientConfigCallback){
                return (RestClientBuilder.HttpClientConfigCallback)o;
            }else {
                throw new InvalidClassException(Strings.forStr(httpClientConfigCallbackClassPath,"must instanceof the org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback"));
            }
        }
        return null;
    }

//    @Override
//    public RestClientBuilder convertRestClientBuilder() throws Exception {
//        RestClientBuilder builder = RestClient.builder(convertServerHttpHost());
//
//        Set<Header> setHeaders = convertHeaders();
//        if (ArrayUtils.isNotEmpty(setHeaders)){
//            builder.setDefaultHeaders(setHeaders);
//        }
//        RestClient.FailureListener setFailureListener = convertFailureListener();
//        if (setFailureListener!=null){
//            builder.setFailureListener(setFailureListener);
//        }
//        RestClientBuilder.HttpClientConfigCallback httpClientConfigCallback = convertHttpClientConfigCallback();
//        if (httpClientConfigCallback != null){
//            builder.setHttpClientConfigCallback(httpClientConfigCallback);
//        }
//        RestClientBuilder.RequestConfigCallback requestConfigCallback = convertRequestConfigCallback();
//        if (requestConfigCallback != null){
//            builder.setRequestConfigCallback(requestConfigCallback);
//        }
//        NodeSelector nodeSelector = convertNodeSelector();
//        if (nodeSelector!=null){
//            builder.setNodeSelector(nodeSelector);
//        }
//        return builder;
//    }



    /**
     *
     * @param client
     * @return
     */
    private HttpHost[] convertClientPropertiesToHttpHost(Map<String, ESClientProperties> client) {
        Set<String> keys = client.keySet();
        int size = keys.size();
        HttpHost[] httpHosts = new HttpHost[size];
        int i = 0;
        for (String key: keys) {
            ESClientProperties esClient = client.get(key);
            HttpHost httpHost = getHttpHost(esClient);
            httpHosts[i] = httpHost;
            i++;
        }
        return httpHosts;
    }

    public HttpHost getHttpHost(ESClientProperties esClient) {
        if (StringUtils.isNotBlank(esClient.getUri())){
            return HttpHost.create(esClient.getUri());
        }else {
            return new HttpHost(esClient.getHostname(),esClient.getPort(),esClient.getScheme());
        }
    }
}
