package cn.apisium.nekocommander;

import cn.apisium.nekocommander.completer.Completer;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.PARAMETER })
@Repeatable(Arguments.class)
public @interface Argument {
    String[] value();
    Class<?> type() default String.class;
    String description() default "";
    String[] defaultValues() default {};
    boolean required() default false;

    Class<? extends Completer> completer() default Completer.class;
    String[] completeValues() default {};
}
