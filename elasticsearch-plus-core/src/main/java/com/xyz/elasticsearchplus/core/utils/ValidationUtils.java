package com.xyz.elasticsearchplus.core.utils;

import com.xyz.elasticsearchplus.core.exception.ValidationException;
import org.apache.commons.collections4.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.Collection;
import java.util.Set;

public class ValidationUtils {

    private static final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

    /**
     * 校验对象
     *
     * @param obj    校验对象
     * @param groups 校验组
     * @param <T>    校验对象泛型
     */
    public static <T> void validateBean(T obj, Class<?>... groups) {
        Set<ConstraintViolation<T>> validateResult = validatorFactory.getValidator().validate(obj, groups);
        if (!validateResult.isEmpty()) {
            // 抛出检验异常
            ConstraintViolation<T> violation = validateResult.iterator().next();
            throw new ValidationException(violation.getPropertyPath() + violation.getMessage());
        }
    }

    public static <T> void validateBean(T obj) {
        Set<ConstraintViolation<T>> constraintViolations = validatorFactory.getValidator().validate(obj);
        // 抛出检验异常
        if (constraintViolations.size() > 0) {
            throw new ValidationException(constraintViolations.iterator().next().getMessage());
        }
    }

    public static <T> void validateBeans(Collection<T> beans) {
        if (CollectionUtils.isEmpty(beans)) {
            return;
        }
        for (T bean : beans) {
            validateBean(bean);
        }
    }

    public static void notBlank(String string, String message) {
        notBlank(string, null, message);
    }

    public static void notBlank(String string, String code, String message) {
        if (string == null || string.trim().length() == 0) {
            throw new ValidationException(code, message);
        }
    }

    public static void notEmpty(Collection<?> collection, String message) {
        notEmpty(collection, null, message);
    }

    public static void notEmpty(Collection<?> collection, String code, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new ValidationException(message);
        }
    }

    public static void notNull(Object object, String message) {
        notNull(object, null, message);
    }

    public static void notNull(Object object, String code, String message) {
        if (object == null) {
            throw new ValidationException(code, message);
        }
    }

    public static void isTrue(Boolean exp, String message) {
        isTrue(exp, null, message);
    }

    public static void isTrue(boolean exp, String code, String message) {
        if (!exp) {
            throw new ValidationException(code, message);
        }
    }
}
