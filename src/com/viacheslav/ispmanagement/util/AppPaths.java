package com.viacheslav.ispmanagement.util;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Centralized filesystem path resolver.
 *
 * <p>Rules:
 * <ul>
 *   <li>Running from IDE (classes directory): store files in project root</li>
 *   <li>Running from JAR/EXE: store files next to the JAR/EXE</li>
 * </ul>
 */
public final class AppPaths {

  private static final String DATA_DIR_NAME = "data";
  private static volatile Path dataDirectory;

  private AppPaths() {
  }

  public static Path getDataDirectory() {
    Path cached = dataDirectory;
    if (cached != null) {
      return cached;
    }

    synchronized (AppPaths.class) {
      if (dataDirectory == null) {
        Path baseDir = resolveBaseDirectory();
        Path dataDir = baseDir.resolve(DATA_DIR_NAME).normalize().toAbsolutePath();
        try {
          Files.createDirectories(dataDir);
        } catch (IOException e) {
          System.err.println("Failed to create data directory: " + dataDir);
          e.printStackTrace();
        }
        dataDirectory = dataDir;
      }
      return dataDirectory;
    }
  }

  private static Path resolveBaseDirectory() {
    try {
      URL locationUrl = AppPaths.class.getProtectionDomain().getCodeSource().getLocation();
      Path location = Paths.get(locationUrl.toURI());

      String lower = location.toString().toLowerCase();
      boolean looksLikeJarOrExe = lower.endsWith(".jar") || lower.endsWith(".exe");

      if (Files.isRegularFile(location) || looksLikeJarOrExe) {
        Path parent = location.getParent();
        return parent != null ? parent.toAbsolutePath() : Paths.get(".").toAbsolutePath();
      }

      // IDE / classes directory case: walk up to project root
      Path projectRoot = findProjectRoot(location);
      return (projectRoot != null ? projectRoot : location).toAbsolutePath();
    } catch (Exception e) {
      System.err.println("Failed to resolve application base directory.");
      e.printStackTrace();
      return Paths.get(".").toAbsolutePath();
    }
  }

  private static Path findProjectRoot(Path from) {
    Path current = from.toAbsolutePath();
    while (current != null) {
      if (Files.isDirectory(current.resolve("src"))
          || Files.isDirectory(current.resolve(".idea"))
          || Files.isRegularFile(current.resolve("com.viacheslav.ispmanagement.iml"))) {
        return current;
      }
      current = current.getParent();
    }
    return null;
  }
}

