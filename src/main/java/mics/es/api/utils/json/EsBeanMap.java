package mics.es.api.utils.json;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class EsBeanMap implements Map {
    public static final int REQUIRE_GETTER = 1;
    public static final int REQUIRE_SETTER = 2;
    protected Object bean;

    public static EsBeanMap create(Object bean){
        EsBeanMap.Generator gen = new EsBeanMap.Generator();
        gen.setBean(bean);
        return gen.create();
    }


    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public Object get(Object key) {
        return null;
    }

    @Override
    public Object put(Object key, Object value) {
        return null;
    }

    @Override
    public Object remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set keySet() {
        return null;
    }

    @Override
    public Collection values() {
        return null;
    }

    @Override
    public Set<Entry> entrySet() {
        return null;
    }

    public static class Generator {

        public void setBean(Object bean){

        }

        public EsBeanMap create() {
            return null;
        }
    }
}
