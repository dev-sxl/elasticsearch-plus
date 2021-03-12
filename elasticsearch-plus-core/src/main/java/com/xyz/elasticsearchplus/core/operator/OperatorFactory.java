package com.xyz.elasticsearchplus.core.operator;

import java.util.Objects;

/**
 * @author sxl
 * @since 2021/3/12 15:49
 */
public final class OperatorFactory {


    private static IDocOperator docOperator;

    private static volatile boolean hasInit = false;

    private OperatorFactory() {
    }

    public static void init(IDocOperator docOperator) {
        if (hasInit) {
            return;
        }
        Objects.requireNonNull(docOperator);
        synchronized (OperatorFactory.class) {
            if (hasInit) {
                return;
            }
            OperatorFactory.docOperator = docOperator;
            hasInit = true;
        }
    }

    public static IDocOperator getOperator() {
        if (hasInit) {
            return OperatorFactory.docOperator;
        }
        throw new RuntimeException("OperatorFactory can use in after init");
    }

}
