package com.backend.proj.utils;

import org.springframework.stereotype.Service;

import com.backend.proj.exceptions.InvalidEnumConstantException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ValidateEnum {
    public <T extends Enum<T>> void isValidEnumConstant(Enum<T> value, Class<T> enumType) {
        boolean isValid = false;
        for (T enumConstant : enumType.getEnumConstants()) {
            if (enumConstant == value) {
                isValid = true;
                break;
            }
        }
        if (!isValid) {
            throw new InvalidEnumConstantException("Invalid value for enum " + enumType.getSimpleName() + ": " + value);
        }
    }

}
