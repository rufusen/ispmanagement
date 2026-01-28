package com.viacheslav.ispmanagement.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.viacheslav.ispmanagement.model.Equipment;
import com.viacheslav.ispmanagement.util.AppPaths;
import com.viacheslav.ispmanagement.util.LocalDateAdapter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
  private final Path filePath = AppPaths.getDataDirectory().resolve("equipment.json");
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
    try {
      Files.createDirectories(filePath.getParent());
      if (!Files.exists(filePath)) {
        Files.createFile(filePath);
      }
    } catch (Exception e) {
      System.err.println("Failed preparing equipment file: " + filePath.toAbsolutePath());
      e.printStackTrace();
      throw new RuntimeException("Error saving equipment to file", e);
    }

    try (OutputStreamWriter writer = new OutputStreamWriter(
        new FileOutputStream(filePath.toFile()), StandardCharsets.UTF_8)) {
      gson.toJson(identityMap.values(), writer);
    } catch (Exception e) {
      System.err.println("Error saving equipment to file: " + filePath.toAbsolutePath());
      e.printStackTrace();
      throw new RuntimeException("Error saving equipment to file", e);
    }
  }

  private void loadFromFile() {
    if (!Files.exists(filePath)) {
      return;
    }

    try (InputStreamReader reader = new InputStreamReader(
        new FileInputStream(filePath.toFile()), StandardCharsets.UTF_8)) {
      Type type = new TypeToken<List<Equipment>>() {
      }.getType();
      List<Equipment> list = gson.fromJson(reader, type);
      if (list != null) {
        list.forEach(e -> identityMap.put(e.getId(), e));
      }
    } catch (Exception e) {
      System.err.println("Error loading equipment from file: " + filePath.toAbsolutePath());
      e.printStackTrace();
    }
  }
}
