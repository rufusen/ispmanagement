package com.viacheslav.ispmanagement.repository;

public class UnitOfWork {

  private final SubscriberRepository subscriberRepository;
  private final PlanRepository planRepository;
  private final PaymentRepository paymentRepository;
  private final EquipmentRepository equipmentRepository;
  private final TicketRepository ticketRepository;

  public UnitOfWork() {
    this.subscriberRepository = new SubscriberRepository();
    this.planRepository = new PlanRepository();
    this.paymentRepository = new PaymentRepository();
    this.equipmentRepository = new EquipmentRepository();
    this.ticketRepository = new TicketRepository();
  }

  public SubscriberRepository subscribers() {
    return subscriberRepository;
  }

  public PlanRepository plans() {
    return planRepository;
  }

  public PaymentRepository payments() {
    return paymentRepository;
  }

  public EquipmentRepository equipment() {
    return equipmentRepository;
  }

  public TicketRepository tickets() {
    return ticketRepository;
  }
}
