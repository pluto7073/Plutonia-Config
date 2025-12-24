package ml.pluto7073.plutonium.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LongOption {

    long min() default 0L;
    long max() default -1L;
    long defaultVal() default 0;
    boolean hasTooltip() default true;

}
