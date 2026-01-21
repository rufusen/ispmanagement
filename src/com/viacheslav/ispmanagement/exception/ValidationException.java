package com.viacheslav.ispmanagement.exception;

/**
 * Thrown when validation of input data fails.
 */
public class ValidationException extends RuntimeException {

  public ValidationException(String message) {
    super(message);
  }
}
