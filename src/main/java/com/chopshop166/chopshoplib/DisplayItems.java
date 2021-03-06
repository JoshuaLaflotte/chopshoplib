package com.chopshop166.chopshoplib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The {@link DisplayItems} annotation provides a collection of {@link Display}
 * instances.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DisplayItems {
    /**
     * The collection of {@link Display}s.
     * 
     * @return All provided {@link Display} annotations.
     */
    Display[] value();
}