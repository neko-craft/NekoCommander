package cn.apisium.nekocommander;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Permissions {
    @SuppressWarnings("unused")
    Permission[] value();
}
