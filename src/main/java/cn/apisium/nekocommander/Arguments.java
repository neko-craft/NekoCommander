package cn.apisium.nekocommander;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Arguments {
    @SuppressWarnings("unused")
    Argument[] value();
}
