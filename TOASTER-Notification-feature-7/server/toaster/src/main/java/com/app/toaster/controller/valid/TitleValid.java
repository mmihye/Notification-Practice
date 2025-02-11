package com.app.toaster.controller.valid;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = TitleValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface TitleValid {
	String message() default "Invalid title";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
	String pattern() default "[가-힣|a-z|A-Z|0-9|]";
}
