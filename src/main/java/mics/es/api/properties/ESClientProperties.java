package mics.es.api.properties;

import lombok.Data;

/**
 * the Client info for client properties
 */
@Data
public final class ESClientProperties {

    /**
     * the hostname of es
     */
    private String  hostname;

    /**
     * the port of es
     */
    private Integer port;

    /**
     * the scheme of es (http or https)
     */
    private String scheme;

    /**
     * uri can replace the hostname port and scheme
     *  like  https://www.elastic.co/
     */
    private String uri;

    public ESClientProperties(){}

    public ESClientProperties(String uri){
        this.uri = uri;
    }

    public ESClientProperties(String hostname,Integer port,String scheme){
        this.hostname = hostname;
        this.port = port;
        this.scheme = scheme;
    }

    public static ESClientProperties createOne(String uri){
        return new ESClientProperties(uri);
    };
}
