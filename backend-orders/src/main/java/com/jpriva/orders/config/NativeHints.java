package com.jpriva.orders.config;

import org.jspecify.annotations.NonNull;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

@Configuration
@ImportRuntimeHints(NativeHints.class)
public class NativeHints implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(@NonNull RuntimeHints hints, ClassLoader classLoader) {
        registerClass(hints, "org.flywaydb.core.internal.exception.sqlExceptions.FlywaySqlServerUntrustedCertificateSqlException");
        registerClass(hints, "org.flywaydb.core.internal.exception.sqlExceptions.FlywaySqlNoIntegratedAuthException");
        registerClass(hints, "org.flywaydb.core.internal.exception.sqlExceptions.FlywaySqlNoDriversForInteractiveAuthException");
        registerClass(hints, "org.flywaydb.core.internal.exception.FlywaySqlException");}

    private void registerClass(RuntimeHints hints, String className) {
        try {
            hints.reflection().registerType(
                    Class.forName(className),
                    MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                    MemberCategory.INVOKE_DECLARED_METHODS,
                    MemberCategory.INVOKE_PUBLIC_METHODS
            );
        } catch (ClassNotFoundException e) {
            // Ignore
        }
    }
}
