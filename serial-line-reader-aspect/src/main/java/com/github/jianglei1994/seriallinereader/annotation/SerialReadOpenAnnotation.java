package com.github.jianglei1994.seriallinereader.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 打开SerialFileReaderUtil的注解，请配置在Spring bean的入口方法上，以使得SpringAOP生效
 *
 * @author jianglei43
 * @date 2019/10/17
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SerialReadOpenAnnotation {
}
