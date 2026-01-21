package com.viacheslav.ispmanagement.util;

import com.viacheslav.ispmanagement.exception.ValidationException;
import java.util.regex.Pattern;

public final class EmailValidator {

  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

  private EmailValidator() {
  }

  public static void validate(String email) {
    NotNullValidator.validate(email, "Email");

    if (!EMAIL_PATTERN.matcher(email).matches()) {
      throw new ValidationException("Invalid email format");
    }
  }
}
