package com.github.jianglei1994.seriallinereader.aspect;

import com.github.jianglei1994.seriallinereader.SerialLineReaderUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 初始化及销毁SerialFileReaderUtil资源的切面，与SerialReadOpenAnnotation配合使用
 *
 * @author jianglei43
 * @date 2019/10/17
 */
@Aspect
public class SerialReadOpenAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(SerialReadOpenAspect.class);

    @Pointcut("@annotation(com.jd.leo.common.util.serialreader.annotation.SerialReadOpenAnnotation)")
    private void pointCut() {
    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable{
        SerialLineReaderUtil.init();
        try {
            return proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            LOGGER.error("AOP around error", throwable);
            throw throwable;
        } finally {
            SerialLineReaderUtil.destroy();
        }
    }
}
