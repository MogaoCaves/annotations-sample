package com.nelson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * 用于存放注解等，Java模块
 * 设置的保存策略为Class，注解用于Field上。在使用时传入一个id，直接以value的形式进行设置即可。
 * Created by Nelson on 17/4/25.
 */

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.FIELD)
public @interface Bind {
    int value();
}
