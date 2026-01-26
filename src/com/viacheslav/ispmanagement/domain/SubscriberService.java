package com.viacheslav.ispmanagement.domain;

import com.viacheslav.ispmanagement.dto.SubscriberCreateDto;
import com.viacheslav.ispmanagement.model.Plan;
import com.viacheslav.ispmanagement.model.Subscriber;
import com.viacheslav.ispmanagement.model.User;
import com.viacheslav.ispmanagement.repository.UnitOfWork;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SubscriberService {

  private final UnitOfWork unitOfWork;

  public SubscriberService(UnitOfWork unitOfWork) {
    this.unitOfWork = unitOfWork;
  }

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

  public Subscriber assignPlan(UUID subscriberId, UUID planId) {
    Subscriber subscriber = unitOfWork.subscribers().findById(subscriberId)
        .orElseThrow(() -> new IllegalArgumentException("Subscriber not found: " + subscriberId));

    Plan plan = unitOfWork.plans().findById(planId)
        .orElseThrow(() -> new IllegalArgumentException("Plan not found: " + planId));

    subscriber.setPlan(plan);
    unitOfWork.subscribers().save(subscriber);
    return subscriber;
  }

  public List<Subscriber> getActiveSubscribers() {
    return unitOfWork.subscribers().findAll().stream()
        .filter(s -> s.getPlan() != null)
        .toList();
  }

  public Subscriber findById(UUID id) {
    return unitOfWork.subscribers().findById(id).orElse(null);
  }

  public List<Subscriber> getAllSubscribers() {
    return unitOfWork.subscribers().findAll();
  }

  public Subscriber findByUserId(UUID userId) {
    Subscriber subscriber = unitOfWork.subscribers().findAll().stream()
        .filter(s -> s.getUser() != null && s.getUser().getId().equals(userId))
        .findFirst()
        .orElse(null);

    if (subscriber == null) {
      User user = unitOfWork.users().findById(userId).orElse(null);
      if (user != null && user.getRole() == User.Role.SUBSCRIBER) {
        subscriber = createDefaultSubscriberForUser(user);
      }
    }

    return subscriber;
  }

  private Subscriber createDefaultSubscriberForUser(User user) {
    // Get or create default plan
    Plan defaultPlan = getOrCreateDefaultPlan();

    // Create subscriber with default values
    Subscriber subscriber = new Subscriber(
        UUID.randomUUID(),
        user.getEmail(), // fullName = email (temporary default)
        "not specified", // phone = "not specified"
        LocalDate.now(), // registrationDate = now()
        null, // address = null (can be set later)
        defaultPlan, // plan = DEFAULT BASIC PLAN
        user, // link to user
        new ArrayList<>(), // payments
        new ArrayList<>(), // equipmentList
        new ArrayList<>() // tickets
    );

    unitOfWork.subscribers().save(subscriber);
    return subscriber;
  }

  private Plan getOrCreateDefaultPlan() {
    List<Plan> allPlans = unitOfWork.plans().findAll();

    if (allPlans.isEmpty()) {
      // Create Basic Plan
      Plan basicPlan = new Plan(
          UUID.randomUUID(),
          "Basic",
          BigDecimal.ZERO, // price: 0
          50, // speed: 50 Mbps
          "Default basic plan"
      );
      unitOfWork.plans().save(basicPlan);
      return basicPlan;
    }

    Plan basicPlan = allPlans.stream()
        .filter(p -> "Basic".equalsIgnoreCase(p.getName()))
        .findFirst()
        .orElse(null);

    if (basicPlan != null) {
      return basicPlan;
    }

    return allPlans.stream()
        .min((p1, p2) -> p1.getPrice().compareTo(p2.getPrice()))
        .orElse(allPlans.get(0)); // fallback to first plan
  }

  public void deleteSubscriber(UUID id) {
    Subscriber subscriber = unitOfWork.subscribers().findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Subscriber not found: " + id));
    unitOfWork.subscribers().deleteById(id);
  }

  public Subscriber updateSubscriber(UUID subscriberId, String fullName, String phone,
      UUID planId) {
    Subscriber subscriber = unitOfWork.subscribers().findById(subscriberId)
        .orElseThrow(() -> new IllegalArgumentException("Subscriber not found: " + subscriberId));

    if (fullName != null && !fullName.trim().isEmpty()) {
      subscriber.setFullName(fullName.trim());
    }

    if (phone != null && !phone.trim().isEmpty()) {
      subscriber.setPhone(phone.trim());
    }

    if (planId != null) {
      Plan plan = unitOfWork.plans().findById(planId)
          .orElseThrow(() -> new IllegalArgumentException("Plan not found: " + planId));
      subscriber.setPlan(plan);
    }

    unitOfWork.subscribers().save(subscriber);
    return subscriber;
  }
}
