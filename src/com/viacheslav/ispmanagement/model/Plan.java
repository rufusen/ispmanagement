package com.viacheslav.ispmanagement.model;

import java.math.BigDecimal;
import java.util.UUID;

public class Plan {

  private UUID id;
  private String name;
  private BigDecimal price;
  private int speedMbps;
  private String description;

  public Plan() {
  }

  public Plan(UUID id, String name, BigDecimal price, int speedMbps, String description) {
    this.id = id;
    this.name = name;
    this.price = price;
    this.speedMbps = speedMbps;
    this.description = description;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public int getSpeedMbps() {
    return speedMbps;
  }

  public void setSpeedMbps(int speedMbps) {
    this.speedMbps = speedMbps;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
