package com.viacheslav.ispmanagement.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.viacheslav.ispmanagement.model.Plan;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlanRepository implements CrudRepository<Plan> {

  private final Map<UUID, Plan> identityMap = new HashMap<>();
  private final String filePath = "data/plans.json";
  private final Gson gson;

  public PlanRepository() {
    this.gson = new GsonBuilder().setPrettyPrinting().create();
    loadFromFile();
  }

  // ---------- CRUD ----------

  @Override
  public void save(Plan plan) {
    identityMap.put(plan.getId(), plan);
    saveToFile();
  }

  @Override
  public Optional<Plan> findById(UUID id) {
    return Optional.ofNullable(identityMap.get(id));
  }

  @Override
  public List<Plan> findAll() {
    return new ArrayList<>(identityMap.values());
  }

  @Override
  public void deleteById(UUID id) {
    identityMap.remove(id);
    saveToFile();
  }

  // ---------- SEARCH ----------

  public List<Plan> findByName(String name) {
    return identityMap.values().stream()
        .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
        .collect(Collectors.toList());
  }

  // ---------- FILTER ----------

  public List<Plan> filterByMaxPrice(double maxPrice) {
    return identityMap.values().stream()
        .filter(p -> p.getPrice().doubleValue() <= maxPrice)
        .collect(Collectors.toList());
  }

  // ---------- FILE WORK ----------

  private void saveToFile() {
    try (FileWriter writer = new FileWriter(filePath)) {
      gson.toJson(identityMap.values(), writer);
    } catch (Exception e) {
      throw new RuntimeException("Error saving plans to file", e);
    }
  }

  private void loadFromFile() {
    try (FileReader reader = new FileReader(filePath)) {
      Type listType = new TypeToken<List<Plan>>() {
      }.getType();
      List<Plan> plans = gson.fromJson(reader, listType);

      if (plans != null) {
        for (Plan p : plans) {
          identityMap.put(p.getId(), p);
        }
      }
    } catch (Exception ignored) {
      // файл може не існувати — нормально
    }
  }
}
