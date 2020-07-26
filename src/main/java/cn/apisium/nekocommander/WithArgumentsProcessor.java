package cn.apisium.nekocommander;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface WithArgumentsProcessor {
    Class<? extends ArgumentsProcessor> value();
}
