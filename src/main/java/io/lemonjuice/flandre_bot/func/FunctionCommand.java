package io.lemonjuice.flandre_bot.func;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value = ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FunctionCommand {
    String value();

    boolean report() default true;
}
