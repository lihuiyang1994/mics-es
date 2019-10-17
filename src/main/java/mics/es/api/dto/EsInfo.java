package mics.es.api.dto;

import lombok.Data;
import mics.es.api.aop.ESMapping;
import mics.es.api.aop.EsProperty;
import mics.es.api.utils.BeanUtils;
import mics.es.api.utils.ParameterSet;
import mics.es.api.utils.Strings;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.index.VersionType;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class EsInfo {

    private static final String DEFAULT_TIMEOUT = "1s";

    private String idField;

    private String index;

    private String routing;

    private String timeout;

    private String refreshPolicy;

    private Integer version;

    private VersionType versionType;

    private String opType;

    private String pipeline;

    private String preference;

    private Class<? extends ActionListener> listener;

    private Set<EsProperty> properties = new HashSet<>();

    private Class clazz ;

    public static EsInfo getNullInstance(){
        return new EsInfo();
    }

    private EsInfo(){}

    public void bindMappingInfo(ESMapping esMapping, Class clazz) {
        baseInfoBinding(esMapping,clazz);
    }

    private void baseInfoBinding(ESMapping esMapping,Class clazz) {
        this.index = esMapping.value();
        this.routing = esMapping.routing();
        this.refreshPolicy = esMapping.refreshPolicy();
        this.version = esMapping.version();
        this.versionType = esMapping.versionType();
        this.opType = esMapping.opType();
        this.pipeline = esMapping.pipeline();
        this.preference = esMapping.preference();
        this.listener = esMapping.listener();
        this.clazz = clazz;
        this.timeout = esMapping.timeout();
    }

    public void bindEsPropertyByField(Field field){
        EsProperty property = new EsProperty(field);
        properties.add(property);
    }

    public void addProperty(EsProperty property) {
        properties.add(property);
    }

    /**
     * format Object to Map
     * @param o
     * @return
     * @throws IntrospectionException
     */
    public ParameterSet mapResult(Object o) throws IntrospectionException, InvocationTargetException, IllegalAccessException {

        if (clazz == null) {
            throw new NullPointerException(" EsInfo Field is Null");
        }
        PropertyDescriptor[] propertyDescriptors = BeanUtils.propertyDescriptors(o.getClass());

        return  bindPropertyToMap(o, propertyDescriptors);
    }

    /**
     * 给map 绑定值
     * @param o
     * @param propertyDescriptors
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private ParameterSet bindPropertyToMap(Object o, PropertyDescriptor[] propertyDescriptors) throws IllegalAccessException, InvocationTargetException {
        ParameterSet parameterSet = new ParameterSet();
        Map<String,Object> resultMap = new HashMap<>();
        for (EsProperty property : properties) {
            String name = property.getName();
            boolean required = property.isRequired();
            //获取值
            for (PropertyDescriptor descriptor: propertyDescriptors) {
                if (Strings.camelToUnderline(descriptor.getName()).equals(name)){
                    Method readMethod = descriptor.getReadMethod();
                    Object value = readMethod.invoke(o);
                    if (value!=null){
                        resultMap.put(name,value);
                    }
                }
                //设置主键
                if (descriptor.getName().equalsIgnoreCase(idField)){
                    parameterSet.setIdValue(descriptor.getReadMethod().invoke(o));
                }
            }
        }
        parameterSet.setParameterMap(resultMap);

        return parameterSet;
    }

}
