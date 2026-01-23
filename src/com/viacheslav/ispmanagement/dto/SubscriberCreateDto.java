package com.viacheslav.ispmanagement.dto;

import com.viacheslav.ispmanagement.model.Address;
import com.viacheslav.ispmanagement.util.NotNullValidator;
import com.viacheslav.ispmanagement.util.PhoneValidator;
import java.util.UUID;

public final class SubscriberCreateDto {

  private final String fullName;
  private final String phone;
  private final Address address;
  private final UUID planId;
  private final UUID userId;

  public SubscriberCreateDto(String fullName, String phone, Address address, UUID planId,
      UUID userId) {
    NotNullValidator.validate(fullName, "Full name");
    NotNullValidator.validate(phone, "Phone");
    NotNullValidator.validate(address, "Address");
    NotNullValidator.validate(planId, "Plan ID");
    NotNullValidator.validate(userId, "User ID");

    if (fullName.trim().isEmpty()) {
      throw new IllegalArgumentException("Full name cannot be empty");
    }

    PhoneValidator.validate(phone);

    this.fullName = fullName.trim();
    this.phone = phone.trim();
    this.address = address;
    this.planId = planId;
    this.userId = userId;
  }

  public String getFullName() {
    return fullName;
  }

  public String getPhone() {
    return phone;
  }

  public Address getAddress() {
    return address;
  }

  public UUID getPlanId() {
    return planId;
  }

  public UUID getUserId() {
    return userId;
  }
}
