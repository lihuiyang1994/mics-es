package mics.es.api.helper;

import lombok.extern.slf4j.Slf4j;
import mics.es.api.aop.ESMapping;
import mics.es.api.aop.EsProperty;
import mics.es.api.aop.Id;
import mics.es.api.dto.EsInfo;
import mics.es.api.exception.NotFoundException;
import mics.es.api.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
@Slf4j
public class EsInMemoryInfoHelper extends BaseEsInfoHelper{

    private static final Map<Class, EsInfo> ES_INFO_CACHE = new ConcurrentHashMap();

    private static final String DEFAULT_ID_NAME = "id";

    @Override
    public EsInfo storeEsInfoByClass(Class clazz){
        Objects.requireNonNull(clazz,"the class must not be null");
        EsInfo esInfo = getInfo(clazz);
        return esInfo;
    }

    private EsInfo getInfo(Class clazz) {
        EsInfo esInfo = EsInfo.getNullInstance();
        ESMapping esMapping = ReflectionUtils.getEsMappingAnnotation(clazz);
        Set<Field> declaredFields = ReflectionUtils.getUnStaticDeclareFields(clazz);
        esInfo.bindMappingInfo(esMapping,clazz);
        bindIdField(esInfo, declaredFields);
        bindFileProperties(clazz, esInfo, declaredFields);
        return esInfo;
    }

    private void bindFileProperties(Class clazz, EsInfo esInfo, Set<Field> declaredFields) {
        declaredFields.stream().forEach(one -> {
            EsProperty property = new EsProperty(one);
            esInfo.addProperty(property);
        });
        ES_INFO_CACHE.put(clazz,esInfo);
    }

    private void bindIdField(EsInfo esInfo, Set<Field> declaredFields) {
        boolean exist = isExistIdProperty(declaredFields,esInfo);
        if (!exist){
           boolean b = isExistDefaultId(declaredFields);
           if (!b){
               throw new NotFoundException("The id field was not found");
           }else {
               //设置id字段
               esInfo.setIdField(DEFAULT_ID_NAME);
           }
        }
    }

    /**
     * validate contain default id field
     * @param fields
     * @return
     */
    private boolean isExistDefaultId(Set<Field> fields) {
        for (Field f: fields){
            if (DEFAULT_ID_NAME.equals(f.getName())){
                fields.remove(f);
                return true;
            }
        }
        return false;
    }

    /**
     * validate contain Id Field
     * @param fields
     * @param esInfo
     * @return
     */
    private static  boolean isExistIdProperty(Set<Field> fields, EsInfo esInfo) {
        for (Field f :
                fields) {
            Id id = f.getAnnotation(Id.class);
            if (id !=null){
                esInfo.setIdField(f.getName());
                fields.remove(f);
                return true;
            }
        }
        return false;
    }

    /**
     * get EsInfo by Class
     */
    @Override
    public EsInfo getEsInfoByClass(Class clazz){
        Objects.requireNonNull(clazz,"the class must not be null");
        EsInfo esInfo = ES_INFO_CACHE.get(clazz);
        if (esInfo != null){
            return esInfo;
        }else {
            EsInfo info = storeEsInfoByClass(clazz);
            return info;
        }
    }
}
