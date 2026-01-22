package com.viacheslav.ispmanagement.model;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class Subscriber {

  private UUID id;
  private String fullName;
  private String phone;
  private LocalDate registrationDate;

  private Address address;
  private Plan plan;
  private User user;

  private List<Payment> payments;
  private List<Equipment> equipmentList;
  private List<Ticket> tickets;

  public Subscriber() {
  }

  public Subscriber(UUID id, String fullName, String phone, LocalDate registrationDate,
      Address address, Plan plan, User user,
      List<Payment> payments, List<Equipment> equipmentList, List<Ticket> tickets) {
    this.id = id;
    this.fullName = fullName;
    this.phone = phone;
    this.registrationDate = registrationDate;
    this.address = address;
    this.plan = plan;
    this.user = user;
    this.payments = payments;
    this.equipmentList = equipmentList;
    this.tickets = tickets;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public LocalDate getRegistrationDate() {
    return registrationDate;
  }

  public void setRegistrationDate(LocalDate registrationDate) {
    this.registrationDate = registrationDate;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public Plan getPlan() {
    return plan;
  }

  public void setPlan(Plan plan) {
    this.plan = plan;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public List<Payment> getPayments() {
    return payments;
  }

  public void setPayments(List<Payment> payments) {
    this.payments = payments;
  }

  public List<Equipment> getEquipmentList() {
    return equipmentList;
  }

  public void setEquipmentList(List<Equipment> equipmentList) {
    this.equipmentList = equipmentList;
  }

  public List<Ticket> getTickets() {
    return tickets;
  }

  public void setTickets(List<Ticket> tickets) {
    this.tickets = tickets;

  }

  @Override
  public String toString() {
    return "Subscriber{" +
        "id=" + id +
        ", fullName='" + fullName + '\'' +
        ", phone='" + phone + '\'' +
        ", registrationDate=" + registrationDate +
        ", plan=" + (plan != null ? plan.getName() : "none") +
        '}';
  }
}
