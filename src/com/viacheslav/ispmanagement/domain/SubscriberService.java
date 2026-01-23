package com.viacheslav.ispmanagement.domain;

import com.viacheslav.ispmanagement.dto.SubscriberCreateDto;
import com.viacheslav.ispmanagement.model.Plan;
import com.viacheslav.ispmanagement.model.Subscriber;
import com.viacheslav.ispmanagement.model.User;
import com.viacheslav.ispmanagement.repository.UnitOfWork;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service for subscriber business logic.
 */
public class SubscriberService {

  private final UnitOfWork unitOfWork;

  public SubscriberService(UnitOfWork unitOfWork) {
    this.unitOfWork = unitOfWork;
  }

  /**
   * Registers a new subscriber.
   *
   * @param dto subscriber creation data
   * @return created subscriber
   * @throws IllegalArgumentException if plan or user not found
   */
  public Subscriber registerSubscriber(SubscriberCreateDto dto) {
    // Validate plan exists
    Plan plan = unitOfWork.plans().findById(dto.getPlanId())
        .orElseThrow(() -> new IllegalArgumentException("Plan not found: " + dto.getPlanId()));

    // Validate user exists
    User user = unitOfWork.users().findById(dto.getUserId())
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + dto.getUserId()));

    // Create subscriber
    Subscriber subscriber = new Subscriber(
        UUID.randomUUID(),
        dto.getFullName(),
        dto.getPhone(),
        LocalDate.now(),
        dto.getAddress(),
        plan,
        user,
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>()
    );

    unitOfWork.subscribers().save(subscriber);
    return subscriber;
  }

  /**
   * Assigns a plan to a subscriber.
   *
   * @param subscriberId subscriber ID
   * @param planId       plan ID
   * @return updated subscriber
   * @throws IllegalArgumentException if subscriber or plan not found
   */
  public Subscriber assignPlan(UUID subscriberId, UUID planId) {
    Subscriber subscriber = unitOfWork.subscribers().findById(subscriberId)
        .orElseThrow(() -> new IllegalArgumentException("Subscriber not found: " + subscriberId));

    Plan plan = unitOfWork.plans().findById(planId)
        .orElseThrow(() -> new IllegalArgumentException("Plan not found: " + planId));

    subscriber.setPlan(plan);
    unitOfWork.subscribers().save(subscriber);
    return subscriber;
  }

  /**
   * Gets all active subscribers (subscribers with assigned plans).
   *
   * @return list of active subscribers
   */
  public List<Subscriber> getActiveSubscribers() {
    return unitOfWork.subscribers().findAll().stream()
        .filter(s -> s.getPlan() != null)
        .toList();
  }

  /**
   * Finds subscriber by ID.
   *
   * @param id subscriber ID
   * @return subscriber or null if not found
   */
  public Subscriber findById(UUID id) {
    return unitOfWork.subscribers().findById(id).orElse(null);
  }

  /**
   * Gets all subscribers.
   *
   * @return list of all subscribers
   */
  public List<Subscriber> getAllSubscribers() {
    return unitOfWork.subscribers().findAll();
  }
}
