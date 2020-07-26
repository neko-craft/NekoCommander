package cn.apisium.nekocommander;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Permissions {
    @NotNull
    Permission[] value();
}
