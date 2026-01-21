package com.viacheslav.ispmanagement.util;

import com.viacheslav.ispmanagement.exception.ValidationException;
import java.math.BigDecimal;

public final class PositiveNumberValidator {

  private PositiveNumberValidator() {
  }

  public static void validate(BigDecimal value, String fieldName) {
    NotNullValidator.validate(value, fieldName);

    if (value.compareTo(BigDecimal.ZERO) <= 0) {
      throw new ValidationException(fieldName + " must be positive");
    }
  }
}
