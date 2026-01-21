package com.viacheslav.ispmanagement.util;

import com.viacheslav.ispmanagement.exception.ValidationException;

public final class NotNullValidator {

  private NotNullValidator() {
  }

  public static void validate(Object value, String fieldName) {
    if (value == null) {
      throw new ValidationException(fieldName + " must not be null");
    }
  }
}
