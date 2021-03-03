package com.cc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Parameters {
    public enum ParamTypes {
        String("java.lang.String"),
        Long("java.lang.Long"),
        BigDecimal("java.math.BigDecimal"),
        Integer("java.lang.Integer"),
        Date("java.util.Date"),
        Time("java.util.Date"),
        Timestamp("java.util.Date"),
        Boolean("java.lang.Boolean"),
        LongArray("java.util.List", "java.lang.Long"),
        IntegerArray("java.util.List", "java.lang.Integer"),
        StringArray("java.util.List", "java.lang.String"),
        ByteArray("byte[]"),
        LargeObject("java.io.InputStream"),
        ModelWithId("com.cc.model.IModelWithId");
        public final String className;
        public final String subClassName;

        private ParamTypes(String className) {
            this(className,
                    null);
        }

        private ParamTypes(String className,
                           String subClassName) {
            this.className = className;
            this.subClassName = subClassName;
        }
    }

    ParamTypes[] types();

    String[] names() default {};
}
