package com.limethecoder.util.validation;


import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ImageValidator implements ConstraintValidator<ValidImage, MultipartFile> {
    @Override
    public void initialize(ValidImage validImage) {

    }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext constraintValidatorContext) {
        if(multipartFile == null || multipartFile.isEmpty()
                || multipartFile.getContentType().equals("image/jpeg")
                || multipartFile.getContentType().equals("image/png")) {
            return true;
        }

        return false;
    }
}
