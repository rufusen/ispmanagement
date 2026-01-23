package com.viacheslav.ispmanagement.domain;

import com.viacheslav.ispmanagement.model.Plan;
import com.viacheslav.ispmanagement.repository.UnitOfWork;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Service for plan business logic.
 */
public class PlanService {

  private final UnitOfWork unitOfWork;

  public PlanService(UnitOfWork unitOfWork) {
    this.unitOfWork = unitOfWork;
  }

  /**
   * Finds plan by ID.
   *
   * @param id plan ID
   * @return plan or null if not found
   */
  public Plan findById(UUID id) {
    return unitOfWork.plans().findById(id).orElse(null);
  }

  /**
   * Gets all plans.
   *
   * @return list of all plans
   */
  public List<Plan> getAllPlans() {
    return unitOfWork.plans().findAll();
  }

  /**
   * Gets plans within a price range.
   *
   * @param maxPrice maximum price
   * @return list of plans with price <= maxPrice
   */
  public List<Plan> getPlansByMaxPrice(double maxPrice) {
    return unitOfWork.plans().filterByMaxPrice(maxPrice);
  }

  /**
   * Searches plans by name.
   *
   * @param name plan name (partial match)
   * @return list of matching plans
   */
  public List<Plan> searchPlansByName(String name) {
    return unitOfWork.plans().findByName(name);
  }

  /**
   * Gets the cheapest plan.
   *
   * @return cheapest plan or null if no plans exist
   */
  public Plan getCheapestPlan() {
    return unitOfWork.plans().findAll().stream()
        .min((p1, p2) -> p1.getPrice().compareTo(p2.getPrice()))
        .orElse(null);
  }

  /**
   * Gets plans with speed >= specified speed.
   *
   * @param minSpeedMbps minimum speed in Mbps
   * @return list of plans meeting the speed requirement
   */
  public List<Plan> getPlansByMinSpeed(int minSpeedMbps) {
    return unitOfWork.plans().findAll().stream()
        .filter(p -> p.getSpeedMbps() >= minSpeedMbps)
        .toList();
  }
}
