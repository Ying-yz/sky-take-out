package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AutoFillAspect {

    /**
     * 切入点：拦截 mapper 包下的所有方法，且方法上标注了 @AutoFill 注解
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {}

    /**
     * 前置通知：在 SQL 执行前完成赋值
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("开始进行公共字段自动填充...");

        // 1. 获取方法上的注解及其对应的操作类型 (INSERT/UPDATE)
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType operationType = autoFill.value();

        // 2. 获取方法的参数（即实体类对象，如 Category）
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) return;
        Object entity = args[0];

        // 3. 准备填充的数据
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId(); // 确保你的 BaseContext 已就绪

        // 4. 根据类型通过反射进行赋值
        if (operationType == OperationType.INSERT) {
            try {
                // 填充 4 个字段
                invokeSetter(entity, AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class, now);
                invokeSetter(entity, AutoFillConstant.SET_CREATE_USER, Long.class, currentId);
                invokeSetter(entity, AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class, now);
                invokeSetter(entity, AutoFillConstant.SET_UPDATE_USER, Long.class, currentId);
            } catch (Exception e) {
                log.error("自动填充失败：{}", e.getMessage());
            }
        } else if (operationType == OperationType.UPDATE) {
            try {
                // 仅填充更新时间/人
                invokeSetter(entity, AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class, now);
                invokeSetter(entity, AutoFillConstant.SET_UPDATE_USER, Long.class, currentId);
            } catch (Exception e) {
                log.error("自动填充失败：{}", e.getMessage());
            }
        }
    }

    // 封装一个简单的反射调用方法
    private void invokeSetter(Object obj, String methodName, Class<?> type, Object value) throws Exception {
        Method method = obj.getClass().getDeclaredMethod(methodName, type);
        method.invoke(obj, value);
    }
}