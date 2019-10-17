package mics.es.api.utils;

import mics.es.api.aop.ESMapping;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toCollection;

public class ReflectionUtils {

    public static final Class ES_MAPPING_CLASS = ESMapping.class;

    public static ESMapping getEsMappingAnnotation(Class clazz) {
        Annotation annotation = clazz.getAnnotation(ES_MAPPING_CLASS);
        return (ESMapping) annotation;
    }

    public static Set<Field> getUnStaticDeclareFields(Class clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        HashSet<Field> fields = Stream.of(declaredFields) /* 过滤静态属性 */
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                /* 过滤 transient关键字修饰的属性 */
                .filter(field -> !Modifier.isTransient(field.getModifiers()))
                .collect(toCollection(HashSet::new));
        return fields;
    }
}
