package com.viacheslav.ispmanagement.domain;

import com.viacheslav.ispmanagement.model.Equipment;
import com.viacheslav.ispmanagement.repository.UnitOfWork;
import java.util.List;
import java.util.UUID;

public class EquipmentService {

  private final UnitOfWork unitOfWork;

  public EquipmentService(UnitOfWork unitOfWork) {
    this.unitOfWork = unitOfWork;
  }

  public Equipment findById(UUID id) {
    return unitOfWork.equipment().findById(id).orElse(null);
  }

  public List<Equipment> getAllEquipment() {
    return unitOfWork.equipment().findAll();
  }

  public List<Equipment> getEquipmentByType(String type) {
    return unitOfWork.equipment().filterByType(type);
  }
}
