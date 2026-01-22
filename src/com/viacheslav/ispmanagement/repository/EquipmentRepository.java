package com.viacheslav.ispmanagement.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.viacheslav.ispmanagement.model.Equipment;
import com.viacheslav.ispmanagement.util.LocalDateAdapter;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class EquipmentRepository implements CrudRepository<Equipment> {

  private final Map<UUID, Equipment> identityMap = new HashMap<>();
  private final String filePath = "data/equipment.json";
  private final Gson gson;

  public EquipmentRepository() {
    this.gson = new GsonBuilder()
        .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
        .setPrettyPrinting()
        .create();
    loadFromFile();
  }

  // ---------- CRUD ----------

  @Override
  public void save(Equipment equipment) {
    identityMap.put(equipment.getId(), equipment);
    saveToFile();
  }

  @Override
  public Optional<Equipment> findById(UUID id) {
    return Optional.ofNullable(identityMap.get(id));
  }

  @Override
  public List<Equipment> findAll() {
    return new ArrayList<>(identityMap.values());
  }

  @Override
  public void deleteById(UUID id) {
    identityMap.remove(id);
    saveToFile();
  }

  // ---------- FILTER ----------

  public List<Equipment> filterByType(String type) {
    return identityMap.values().stream()
        .filter(e -> e.getType().equalsIgnoreCase(type))
        .collect(Collectors.toList());
  }

  // ---------- FILE ----------

  private void saveToFile() {
    try (FileWriter writer = new FileWriter(filePath)) {
      gson.toJson(identityMap.values(), writer);
    } catch (Exception e) {
      throw new RuntimeException("Error saving equipment to file", e);
    }
  }

  private void loadFromFile() {
    try (FileReader reader = new FileReader(filePath)) {
      Type type = new TypeToken<List<Equipment>>() {
      }.getType();
      List<Equipment> list = gson.fromJson(reader, type);
      if (list != null) {
        list.forEach(e -> identityMap.put(e.getId(), e));
      }
    } catch (Exception ignored) {
    }
  }
}
