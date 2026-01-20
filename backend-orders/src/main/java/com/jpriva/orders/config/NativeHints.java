package com.jpriva.orders.config;

import org.jspecify.annotations.NonNull;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

@Configuration
@ImportRuntimeHints(NativeHints.class)
public class NativeHints implements RuntimeHintsRegistrar {
    
    @Override
    public void registerHints(@NonNull RuntimeHints hints, ClassLoader classLoader) {
        registerByType(hints, "org.flywaydb.core.internal.exception.sqlExceptions.FlywaySqlServerUntrustedCertificateSqlException");
        registerByType(hints, "org.flywaydb.core.internal.exception.sqlExceptions.FlywaySqlNoIntegratedAuthException");
        registerByType(hints, "org.flywaydb.core.internal.exception.sqlExceptions.FlywaySqlNoDriversForInteractiveAuthException");
        registerByType(hints, "org.flywaydb.core.internal.exception.FlywaySqlException");

        registerByType(hints, "io.jsonwebtoken.impl.security.StandardSecureDigestAlgorithms");
        registerByType(hints, "io.jsonwebtoken.impl.security.StandardKeyOperations");
        registerByType(hints, "io.jsonwebtoken.impl.DefaultJwtBuilder");
        registerByType(hints, "io.jsonwebtoken.impl.DefaultJwtParser");

        registerByType(hints, "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        registerByType(hints, "com.microsoft.sqlserver.jdbc.SQLServerDataSource");
        
        registerByType(hints, "org.hibernate.bytecode.internal.bytebuddy.BytecodeProviderImpl");

        hints.resources().registerPattern("META-INF/services/java.sql.Driver");
        
        hints.reflection().registerType(com.microsoft.sqlserver.jdbc.SQLServerDriver.class, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS);
    }

    private void registerByType(RuntimeHints hints, String className) {
        hints.reflection().registerType(
            TypeReference.of(className), 
            MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
            MemberCategory.INVOKE_DECLARED_METHODS,
            MemberCategory.INVOKE_PUBLIC_METHODS
        );
    }
}
