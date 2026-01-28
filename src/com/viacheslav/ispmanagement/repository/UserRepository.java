package com.viacheslav.ispmanagement.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.viacheslav.ispmanagement.model.User;
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

public class UserRepository implements CrudRepository<User> {

  private final Map<UUID, User> identityMap = new HashMap<>();
  private final Path filePath = AppPaths.getDataDirectory().resolve("users.json");
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
    try {
      Files.createDirectories(filePath.getParent());
      if (!Files.exists(filePath)) {
        Files.createFile(filePath);
      }
    } catch (Exception e) {
      System.err.println("Failed preparing users file: " + filePath.toAbsolutePath());
      e.printStackTrace();
      throw new RuntimeException("Error saving users to file", e);
    }

    try (OutputStreamWriter writer = new OutputStreamWriter(
        new FileOutputStream(filePath.toFile()), StandardCharsets.UTF_8)) {
      gson.toJson(identityMap.values(), writer);
    } catch (Exception e) {
      System.err.println("Error saving users to file: " + filePath.toAbsolutePath());
      e.printStackTrace();
      throw new RuntimeException("Error saving users to file", e);
    }
  }

  private void loadFromFile() {
    if (!Files.exists(filePath)) {
      return;
    }

    try (InputStreamReader reader = new InputStreamReader(
        new FileInputStream(filePath.toFile()), StandardCharsets.UTF_8)) {
      Type type = new TypeToken<List<User>>() {
      }.getType();
      List<User> list = gson.fromJson(reader, type);
      if (list != null) {
        list.forEach(u -> identityMap.put(u.getId(), u));
      }
    } catch (Exception e) {
      System.err.println("Error loading users from file: " + filePath.toAbsolutePath());
      e.printStackTrace();
    }
  }
}
