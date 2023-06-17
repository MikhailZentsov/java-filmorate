package ru.yandex.practicum.filmorate.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValuesAllowedConstraintValidator.class)
public @interface ValuesAllowedConstraint {

    String message() default "{value.hasWrong}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String propName();
    String[] values();
}
