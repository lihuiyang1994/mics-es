package mics.es.api.helper;

import mics.es.api.dto.EsInfo;

public abstract class BaseEsInfoHelper {

    public abstract EsInfo storeEsInfoByClass(Class clazz);

    public abstract EsInfo getEsInfoByClass(Class clazz);

}
