package com.viacheslav.ispmanagement.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.viacheslav.ispmanagement.model.Plan;
import com.viacheslav.ispmanagement.util.AppPaths;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PlanRepository implements CrudRepository<Plan> {

  private final Map<UUID, Plan> identityMap = new HashMap<>();
  private final Path filePath = AppPaths.getDataDirectory().resolve("plans.json");
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
    try {
      Files.createDirectories(filePath.getParent());
      if (!Files.exists(filePath)) {
        Files.createFile(filePath);
      }
    } catch (Exception e) {
      System.err.println("Failed preparing plans file: " + filePath.toAbsolutePath());
      e.printStackTrace();
      throw new RuntimeException("Error saving plans to file", e);
    }

    try (OutputStreamWriter writer = new OutputStreamWriter(
        new FileOutputStream(filePath.toFile()), StandardCharsets.UTF_8)) {
      gson.toJson(identityMap.values(), writer);
    } catch (Exception e) {
      System.err.println("Error saving plans to file: " + filePath.toAbsolutePath());
      e.printStackTrace();
      throw new RuntimeException("Error saving plans to file", e);
    }
  }

  private void loadFromFile() {
    if (!Files.exists(filePath)) {
      return;
    }

    try (InputStreamReader reader = new InputStreamReader(
        new FileInputStream(filePath.toFile()), StandardCharsets.UTF_8)) {
      Type listType = new TypeToken<List<Plan>>() {
      }.getType();
      List<Plan> plans = gson.fromJson(reader, listType);

      if (plans != null) {
        for (Plan p : plans) {
          identityMap.put(p.getId(), p);
        }
      }
    } catch (Exception e) {
      System.err.println("Error loading plans from file: " + filePath.toAbsolutePath());
      e.printStackTrace();
    }
  }
}
