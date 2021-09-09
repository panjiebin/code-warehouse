package com.code.common.script;

import java.lang.annotation.*;

/**
 * @author Pan Jiebin
 * @date 2021-02-23 16:31
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Order {
    int value();
}
