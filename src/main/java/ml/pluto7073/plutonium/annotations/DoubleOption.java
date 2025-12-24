package ml.pluto7073.plutonium.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DoubleOption {

    double min() default 0.0;
    double max() default -1.0;
    double defaultVal() default 0.0;
    boolean hasTooltip() default true;

}
