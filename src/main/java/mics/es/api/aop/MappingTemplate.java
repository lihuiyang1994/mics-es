package mics.es.api.aop;

import java.lang.annotation.*;

/**
 * @author lhy
 *   Mapping
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MappingTemplate {

    /**
     *
     * @return
     */
    String value() default "";
}
