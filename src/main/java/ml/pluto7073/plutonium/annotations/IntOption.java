package ml.pluto7073.plutonium.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IntOption {

    int min() default 0;
    int max() default -1;
    int defaultVal() default 0;
    boolean hasTooltip() default true;

}
