package mics.es.api.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Property {

   String propertyName() default "";

    /**
     * 是否开启驼峰
     * @return
     */
   boolean openHump() default true;

    /**
     * 是否必须
     * @return
     */
   boolean isRequire() default false;

}
