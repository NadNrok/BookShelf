package com.limethecoder.util.validation;


import com.limethecoder.data.domain.User;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator
        implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(PasswordMatches constraintAnnotation) {
    }
    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if(obj != null && obj instanceof User) {
            User user = (User) obj;
            return user.getMatchingPassword() == null ||
                    user.getPassword().equals(user.getMatchingPassword());
        }

        return false;
    }
}