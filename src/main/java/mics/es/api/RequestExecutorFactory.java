package mics.es.api;

import mics.es.api.dto.EsInfo;
import mics.es.api.executor.RequestExecutor;

public interface RequestExecutorFactory {

    RequestExecutor createRequestExecutor(EsInfo info);
}
