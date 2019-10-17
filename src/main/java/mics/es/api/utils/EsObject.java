package mics.es.api.utils;

import java.io.Serializable;

public class EsObject extends EsElement {

    private Serializable id;

    private final Class clazz;

    public EsObject(Object obj){
        clazz = obj.getClass();

    }
}
