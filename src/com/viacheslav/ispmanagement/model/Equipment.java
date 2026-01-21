package com.viacheslav.ispmanagement.model;

import java.time.LocalDate;
import java.util.UUID;

public class Equipment {

  private UUID id;
  private String type;
  private String serialNumber;
  private LocalDate installDate;

  public Equipment() {
  }

  public Equipment(UUID id, String type, String serialNumber, LocalDate installDate) {
    this.id = id;
    this.type = type;
    this.serialNumber = serialNumber;
    this.installDate = installDate;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getSerialNumber() {
    return serialNumber;
  }

  public void setSerialNumber(String serialNumber) {
    this.serialNumber = serialNumber;
  }

  public LocalDate getInstallDate() {
    return installDate;
  }

  public void setInstallDate(LocalDate installDate) {
    this.installDate = installDate;
  }
}
