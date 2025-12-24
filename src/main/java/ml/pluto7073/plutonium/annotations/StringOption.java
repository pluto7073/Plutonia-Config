package ml.pluto7073.plutonium.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StringOption {

    int maxLength() default Short.MAX_VALUE;
    String defaultVal() default "";
    boolean hasTooltip() default true;

}
