package cn.apisium.nekocommander;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
@Repeatable(Commands.class)
public @interface Command {
    @NotNull
    String value();
}
