package cn.apisium.nekocommander;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Repeatable(Permissions.class)
public @interface Permission {
    String value();
}
