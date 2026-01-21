package com.viacheslav.ispmanagement.util;

import com.viacheslav.ispmanagement.exception.ValidationException;

public final class StringLengthValidator {

  private StringLengthValidator() {
  }

  public static void validate(String value, String fieldName, int min, int max) {
    NotNullValidator.validate(value, fieldName);

    int length = value.length();
    if (length < min || length > max) {
      throw new ValidationException(
          fieldName + " length must be between " + min + " and " + max
      );
    }
  }
}
