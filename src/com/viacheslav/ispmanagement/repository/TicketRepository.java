package com.viacheslav.ispmanagement.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.viacheslav.ispmanagement.model.Ticket;
import com.viacheslav.ispmanagement.util.LocalDateTimeAdapter;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class TicketRepository implements CrudRepository<Ticket> {

  private final Map<UUID, Ticket> identityMap = new HashMap<>();
  private final String filePath = "data/tickets.json";
  private final Gson gson;

  public TicketRepository() {
    this.gson = new GsonBuilder()
        .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
        .setPrettyPrinting()
        .create();
    loadFromFile();
  }

  // ---------- CRUD ----------

  @Override
  public void save(Ticket ticket) {
    identityMap.put(ticket.getId(), ticket);
    saveToFile();
  }

  @Override
  public Optional<Ticket> findById(UUID id) {
    return Optional.ofNullable(identityMap.get(id));
  }

  @Override
  public List<Ticket> findAll() {
    return new ArrayList<>(identityMap.values());
  }

  @Override
  public void deleteById(UUID id) {
    identityMap.remove(id);
    saveToFile();
  }

  // ---------- FILTER ----------

  public List<Ticket> filterByStatus(Ticket.Status status) {
    return identityMap.values().stream()
        .filter(t -> t.getStatus() == status)
        .collect(Collectors.toList());
  }

  // ---------- FILE ----------

  private void saveToFile() {
    try (FileWriter writer = new FileWriter(filePath)) {
      gson.toJson(identityMap.values(), writer);
    } catch (Exception e) {
      throw new RuntimeException("Error saving tickets to file", e);
    }
  }

  private void loadFromFile() {
    try (FileReader reader = new FileReader(filePath)) {
      Type type = new TypeToken<List<Ticket>>() {
      }.getType();
      List<Ticket> list = gson.fromJson(reader, type);
      if (list != null) {
        list.forEach(t -> identityMap.put(t.getId(), t));
      }
    } catch (Exception ignored) {
    }
  }
}
