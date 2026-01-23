package com.viacheslav.ispmanagement.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.viacheslav.ispmanagement.model.User;
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

public class UserRepository implements CrudRepository<User> {

  private final Map<UUID, User> identityMap = new HashMap<>();
  private final String filePath = "data/users.json";
  private final Gson gson;

  public UserRepository() {
    this.gson = new GsonBuilder().setPrettyPrinting().create();
    loadFromFile();
  }

  @Override
  public void save(User user) {
    identityMap.put(user.getId(), user);
    saveToFile();
  }

  @Override
  public Optional<User> findById(UUID id) {
    return Optional.ofNullable(identityMap.get(id));
  }

  @Override
  public List<User> findAll() {
    return new ArrayList<>(identityMap.values());
  }

  @Override
  public void deleteById(UUID id) {
    identityMap.remove(id);
    saveToFile();
  }

  // SEARCH
  public Optional<User> findByEmail(String email) {
    return identityMap.values().stream()
        .filter(u -> u.getEmail().equalsIgnoreCase(email))
        .findFirst();
  }

  // FILTER
  public List<User> filterByRole(User.Role role) {
    return identityMap.values().stream()
        .filter(u -> u.getRole() == role)
        .collect(Collectors.toList());
  }

  private void saveToFile() {
    try (FileWriter writer = new FileWriter(filePath)) {
      gson.toJson(identityMap.values(), writer);
    } catch (Exception e) {
      throw new RuntimeException("Error saving users to file", e);
    }
  }

  private void loadFromFile() {
    try (FileReader reader = new FileReader(filePath)) {
      Type type = new TypeToken<List<User>>() {
      }.getType();
      List<User> list = gson.fromJson(reader, type);
      if (list != null) {
        list.forEach(u -> identityMap.put(u.getId(), u));
      }
    } catch (Exception ignored) {
      // File may not exist - that's OK
    }
  }
}
