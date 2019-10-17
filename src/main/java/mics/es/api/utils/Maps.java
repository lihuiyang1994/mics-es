package mics.es.api.utils;

import java.util.HashMap;
import java.util.Map;

public class Maps {

    public static <K,V> HashMap<K,V> createSingleResultHashMap(K k , V v){
        HashMap<K,V> singleMap = new HashMap<>();
        singleMap.put(k,v);
        return singleMap;
    }

    private Maps(){}

}
