package mics.es.api.aop;

import lombok.Data;
import mics.es.api.utils.Strings;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

@Data
public class EsProperty {

    private String name;

    private boolean isRequired;

    public EsProperty(){}

    public EsProperty(Field field) {
        String fieldName = field.getName();
        Property propertyAnno = field.getAnnotation(Property.class);
        if (propertyAnno==null){
            //default setting
            //获取property字段默认开启驼峰
            this.name = Strings.camelToUnderline(fieldName);
            this.isRequired = false;
        }else {
            //customer setting
            this.isRequired = propertyAnno.isRequire();
            if (StringUtils.isNotBlank(propertyAnno.propertyName())){
                this.name = propertyAnno.propertyName();
            }else{
                boolean hump = propertyAnno.openHump();
                if (hump){
                    this.name = Strings.camelToUnderline(fieldName);
                }else {
                    this.name = fieldName;
                }
            }
        }
    }





}
