package com.viacheslav.ispmanagement.domain;

import com.viacheslav.ispmanagement.model.Plan;
import com.viacheslav.ispmanagement.repository.UnitOfWork;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class PlanService {

  private final UnitOfWork unitOfWork;

  public PlanService(UnitOfWork unitOfWork) {
    this.unitOfWork = unitOfWork;
  }

  public Plan findById(UUID id) {
    return unitOfWork.plans().findById(id).orElse(null);
  }

  public List<Plan> getAllPlans() {
    return unitOfWork.plans().findAll();
  }

  public List<Plan> getPlansByMaxPrice(double maxPrice) {
    return unitOfWork.plans().filterByMaxPrice(maxPrice);
  }

  public List<Plan> searchPlansByName(String name) {
    return unitOfWork.plans().findByName(name);
  }

  public Plan getCheapestPlan() {
    return unitOfWork.plans().findAll().stream()
        .min((p1, p2) -> p1.getPrice().compareTo(p2.getPrice()))
        .orElse(null);
  }

  public List<Plan> getPlansByMinSpeed(int minSpeedMbps) {
    return unitOfWork.plans().findAll().stream()
        .filter(p -> p.getSpeedMbps() >= minSpeedMbps)
        .toList();
  }

  public Plan createPlan(String name, BigDecimal price, int speedMbps, String description) {
    Plan plan = new Plan(UUID.randomUUID(), name, price, speedMbps, description);
    unitOfWork.plans().save(plan);
    return plan;
  }
}
