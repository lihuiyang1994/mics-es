package mics.es.api.aop;

import mics.es.api.listener.DefaultActionListenerAdapter;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.index.VersionType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ESMapping {

    String value();

    String routing() default "";

    String timeout() default "30s";

    String refreshPolicy() default "";

    int version() default 0;

    VersionType versionType() default VersionType.INTERNAL;

    String opType() default "";

    String pipeline() default "";

    boolean isAsync() default false;

    Class<?> requestOptions() default Void.class;

    String defaultRequestOptionsMethod() default "options";

    Class<? extends ActionListener> listener() default DefaultActionListenerAdapter.class;

    String preference() default "";

    boolean realtime() default false;

    boolean refresh() default true;
}
