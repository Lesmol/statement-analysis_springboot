package org.lvmp.statementanalysis_springboot.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PdfFileValidator.class)
public @interface PdfFile {
    String message() default "File must be a PDF";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
