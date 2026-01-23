package com.viacheslav.ispmanagement.domain;

import com.viacheslav.ispmanagement.dto.PaymentCreateDto;
import com.viacheslav.ispmanagement.model.Payment;
import com.viacheslav.ispmanagement.model.Subscriber;
import com.viacheslav.ispmanagement.repository.UnitOfWork;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Service for payment business logic.
 */
public class PaymentService {

  private final UnitOfWork unitOfWork;

  public PaymentService(UnitOfWork unitOfWork) {
    this.unitOfWork = unitOfWork;
  }

  /**
   * Creates a payment for a subscriber.
   *
   * @param dto payment creation data
   * @return created payment
   * @throws IllegalArgumentException if subscriber not found
   */
  public Payment makePayment(PaymentCreateDto dto) {
    // Validate subscriber exists
    Subscriber subscriber = unitOfWork.subscribers().findById(dto.getSubscriberId())
        .orElseThrow(() -> new IllegalArgumentException("Subscriber not found: " + dto.getSubscriberId()));

    // Create payment
    Payment payment = new Payment(
        UUID.randomUUID(),
        LocalDateTime.now(),
        dto.getAmount(),
        dto.getMethod()
    );

    unitOfWork.payments().save(payment);

    // Add payment to subscriber's payment list
    if (subscriber.getPayments() == null) {
      subscriber.setPayments(new java.util.ArrayList<>());
    }
    subscriber.getPayments().add(payment);
    unitOfWork.subscribers().save(subscriber);

    return payment;
  }

  /**
   * Gets all payments for a subscriber.
   *
   * @param subscriberId subscriber ID
   * @return list of payments
   */
  public List<Payment> getPaymentsBySubscriber(UUID subscriberId) {
    Subscriber subscriber = unitOfWork.subscribers().findById(subscriberId)
        .orElseThrow(() -> new IllegalArgumentException("Subscriber not found: " + subscriberId));

    return subscriber.getPayments() != null ? subscriber.getPayments() : List.of();
  }

  /**
   * Calculates total amount paid by a subscriber.
   *
   * @param subscriberId subscriber ID
   * @return total amount paid
   */
  public BigDecimal getTotalPaidBySubscriber(UUID subscriberId) {
    List<Payment> payments = getPaymentsBySubscriber(subscriberId);
    return payments.stream()
        .map(Payment::getAmount)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  /**
   * Gets payments with amount >= minimum amount.
   *
   * @param minAmount minimum payment amount
   * @return list of payments
   */
  public List<Payment> getPaymentsByMinAmount(BigDecimal minAmount) {
    return unitOfWork.payments().filterByMinAmount(minAmount);
  }

  /**
   * Finds payment by ID.
   *
   * @param id payment ID
   * @return payment or null if not found
   */
  public Payment findById(UUID id) {
    return unitOfWork.payments().findById(id).orElse(null);
  }
}
