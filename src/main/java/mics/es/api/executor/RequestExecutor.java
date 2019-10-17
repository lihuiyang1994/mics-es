package mics.es.api.executor;

import org.elasticsearch.action.ActionRequest;

/**
 * Request executor
 */
public class RequestExecutor<T extends ActionRequest>{

    private boolean asyn = false;

    private T request;

    private RequestExecutor(T request) {
        this.request = request;
    }

    public static<T extends ActionRequest> RequestExecutor getInstance(T request){
        return new RequestExecutor(request);
    }
    /**
     * 开启异步
     * @return
     */
    public RequestExecutor asyn(){
        this.asyn = true;
        return this;
    }
    /**
     * 是否启动异步
     * @return
     */
    public boolean isAsyn(){
        return asyn;
    }
    /**
     * 关闭异步
     * @return
     */
    public RequestExecutor disAsyn(){
        this.asyn = false;
        return this;
    }

}