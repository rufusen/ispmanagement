package com.viacheslav.ispmanagement.model;

import java.util.UUID;

public class Address {

  private UUID id;
  private String city;
  private String street;
  private String house;
  private String apartment;

  public Address() {
  }

  public Address(UUID id, String city, String street, String house, String apartment) {
    this.id = id;
    this.city = city;
    this.street = street;
    this.house = house;
    this.apartment = apartment;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getHouse() {
    return house;
  }

  public void setHouse(String house) {
    this.house = house;
  }

  public String getApartment() {
    return apartment;
  }

  public void setApartment(String apartment) {
    this.apartment = apartment;
  }

  @Override
  public String toString() {
    return "Address{" +
        "id=" + id +
        ", city='" + city + '\'' +
        ", street='" + street + '\'' +
        ", house='" + house + '\'' +
        ", apartment='" + apartment + '\'' +
        '}';
  }
}
