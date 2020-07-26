package cn.apisium.nekocommander;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@Repeatable(Permissions.class)
public @interface Permission {
    @NotNull
    String value();
}
