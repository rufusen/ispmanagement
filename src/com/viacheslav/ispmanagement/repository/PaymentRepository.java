package com.viacheslav.ispmanagement.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.viacheslav.ispmanagement.model.Payment;
import com.viacheslav.ispmanagement.util.LocalDateTimeAdapter;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.math.BigDecimal;
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
  private final String filePath = "data/payments.json";
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
    try (FileWriter writer = new FileWriter(filePath)) {
      gson.toJson(identityMap.values(), writer);
    } catch (Exception e) {
      throw new RuntimeException("Error saving payments", e);
    }
  }

  private void loadFromFile() {
    try (FileReader reader = new FileReader(filePath)) {
      Type type = new TypeToken<List<Payment>>() {
      }.getType();
      List<Payment> list = gson.fromJson(reader, type);
      if (list != null) {
        list.forEach(p -> identityMap.put(p.getId(), p));
      }
    } catch (Exception ignored) {
    }
  }
}
