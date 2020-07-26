package cn.apisium.nekocommander;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.PARAMETER })
@Repeatable(Arguments.class)
public @interface Argument {
    String[] value();
    Class<?> type() default String.class;
    String description() default "";
    String[] defaultValues() default {};
    boolean required() default false;
}
