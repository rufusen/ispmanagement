package com.viacheslav.ispmanagement.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.viacheslav.ispmanagement.model.Subscriber;
import com.viacheslav.ispmanagement.util.AppPaths;
import com.viacheslav.ispmanagement.util.LocalDateAdapter;
import com.viacheslav.ispmanagement.util.LocalDateTimeAdapter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class SubscriberRepository implements CrudRepository<Subscriber> {

  private final Map<UUID, Subscriber> identityMap = new HashMap<>();
  private final Path filePath = AppPaths.getDataDirectory().resolve("subscribers.json");
  private final Gson gson;

  public SubscriberRepository() {
    this.gson = new GsonBuilder()
        .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .setPrettyPrinting()
        .create();
    loadFromFile();
  }

  @Override
  public void save(Subscriber subscriber) {
    identityMap.put(subscriber.getId(), subscriber);
    saveToFile();
  }

  @Override
  public Optional<Subscriber> findById(UUID id) {
    return Optional.ofNullable(identityMap.get(id));
  }

  @Override
  public List<Subscriber> findAll() {
    return new ArrayList<>(identityMap.values());
  }

  @Override
  public void deleteById(UUID id) {
    identityMap.remove(id);
    saveToFile();
  }

  // SEARCH
  public List<Subscriber> findByName(String name) {
    return identityMap.values().stream()
        .filter(s -> s.getFullName().toLowerCase().contains(name.toLowerCase()))
        .collect(Collectors.toList());
  }

  // FILTER
  public List<Subscriber> filterByPlan(String planName) {
    return identityMap.values().stream()
        .filter(s -> s.getPlan().getName().equalsIgnoreCase(planName))
        .collect(Collectors.toList());
  }

  private void saveToFile() {
    try {
      Files.createDirectories(filePath.getParent());
      if (!Files.exists(filePath)) {
        Files.createFile(filePath);
      }
    } catch (Exception e) {
      System.err.println("Failed preparing subscribers file: " + filePath.toAbsolutePath());
      e.printStackTrace();
      throw new RuntimeException("Error saving subscribers", e);
    }

    try (OutputStreamWriter writer = new OutputStreamWriter(
        new FileOutputStream(filePath.toFile()), StandardCharsets.UTF_8)) {
      gson.toJson(identityMap.values(), writer);
    } catch (Exception e) {
      System.err.println("Error saving subscribers to file: " + filePath.toAbsolutePath());
      e.printStackTrace();
      throw new RuntimeException("Error saving subscribers", e);
    }
  }

  private void loadFromFile() {
    if (!Files.exists(filePath)) {
      return;
    }

    try (InputStreamReader reader = new InputStreamReader(
        new FileInputStream(filePath.toFile()), StandardCharsets.UTF_8)) {
      Type type = new TypeToken<List<Subscriber>>() {
      }.getType();
      List<Subscriber> list = gson.fromJson(reader, type);
      if (list != null) {
        list.forEach(s -> identityMap.put(s.getId(), s));
      }
    } catch (Exception e) {
      System.err.println("Error loading subscribers from file: " + filePath.toAbsolutePath());
      e.printStackTrace();
    }
  }
}
