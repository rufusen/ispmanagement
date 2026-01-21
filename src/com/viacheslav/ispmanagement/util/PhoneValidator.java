package com.viacheslav.ispmanagement.util;

import com.viacheslav.ispmanagement.exception.ValidationException;

public final class PhoneValidator {

  private PhoneValidator() {
  }

  public static void validate(String phone) {
    NotNullValidator.validate(phone, "Phone number");

    if (!phone.matches("\\+?[0-9]{10,15}")) {
      throw new ValidationException("Invalid phone number format");
    }
  }
}
