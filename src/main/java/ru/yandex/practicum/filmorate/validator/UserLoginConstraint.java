package ru.yandex.practicum.filmorate.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserLoginConstraintValidator.class)
public @interface UserLoginConstraint {
    String message() default "{value.hasBlank}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
