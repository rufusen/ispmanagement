package com.viacheslav.ispmanagement.domain;

import com.viacheslav.ispmanagement.model.Equipment;
import com.viacheslav.ispmanagement.repository.UnitOfWork;
import java.util.List;
import java.util.UUID;

/**
 * Service for equipment business logic.
 */
public class EquipmentService {

  private final UnitOfWork unitOfWork;

  public EquipmentService(UnitOfWork unitOfWork) {
    this.unitOfWork = unitOfWork;
  }

  /**
   * Finds equipment by ID.
   *
   * @param id equipment ID
   * @return equipment or null if not found
   */
  public Equipment findById(UUID id) {
    return unitOfWork.equipment().findById(id).orElse(null);
  }

  /**
   * Gets all equipment.
   *
   * @return list of all equipment
   */
  public List<Equipment> getAllEquipment() {
    return unitOfWork.equipment().findAll();
  }

  /**
   * Gets equipment by type.
   *
   * @param type equipment type
   * @return list of equipment of specified type
   */
  public List<Equipment> getEquipmentByType(String type) {
    return unitOfWork.equipment().filterByType(type);
  }
}
