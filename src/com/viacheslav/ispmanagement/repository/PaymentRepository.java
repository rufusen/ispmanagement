package com.viacheslav.ispmanagement.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.viacheslav.ispmanagement.model.Payment;
import com.viacheslav.ispmanagement.util.AppPaths;
import com.viacheslav.ispmanagement.util.LocalDateTimeAdapter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class PaymentRepository implements CrudRepository<Payment> {

  private final Map<UUID, Payment> identityMap = new HashMap<>();
  private final Path filePath = AppPaths.getDataDirectory().resolve("payments.json");
  private final Gson gson;

  public PaymentRepository() {
    this.gson = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .setPrettyPrinting()
        .create();
    loadFromFile();
  }

  @Override
  public void save(Payment payment) {
    identityMap.put(payment.getId(), payment);
    saveToFile();
  }

  @Override
  public Optional<Payment> findById(UUID id) {
    return Optional.ofNullable(identityMap.get(id));
  }

  @Override
  public List<Payment> findAll() {
    return new ArrayList<>(identityMap.values());
  }

  @Override
  public void deleteById(UUID id) {
    identityMap.remove(id);
    saveToFile();
  }

  // FILTER
  public List<Payment> filterByMinAmount(BigDecimal min) {
    return identityMap.values().stream()
        .filter(p -> p.getAmount().compareTo(min) >= 0)
        .collect(Collectors.toList());
  }

  private void saveToFile() {
    try {
      Files.createDirectories(filePath.getParent());
      if (!Files.exists(filePath)) {
        Files.createFile(filePath);
      }
    } catch (Exception e) {
      System.err.println("Failed preparing payments file: " + filePath.toAbsolutePath());
      e.printStackTrace();
      throw new RuntimeException("Error saving payments", e);
    }

    try (OutputStreamWriter writer = new OutputStreamWriter(
        new FileOutputStream(filePath.toFile()), StandardCharsets.UTF_8)) {
      gson.toJson(identityMap.values(), writer);
    } catch (Exception e) {
      System.err.println("Error saving payments to file: " + filePath.toAbsolutePath());
      e.printStackTrace();
      throw new RuntimeException("Error saving payments", e);
    }
  }

  private void loadFromFile() {
    if (!Files.exists(filePath)) {
      return;
    }

    try (InputStreamReader reader = new InputStreamReader(
        new FileInputStream(filePath.toFile()), StandardCharsets.UTF_8)) {
      Type type = new TypeToken<List<Payment>>() {
      }.getType();
      List<Payment> list = gson.fromJson(reader, type);
      if (list != null) {
        list.forEach(p -> identityMap.put(p.getId(), p));
      }
    } catch (Exception e) {
      System.err.println("Error loading payments from file: " + filePath.toAbsolutePath());
      e.printStackTrace();
    }
  }
}
