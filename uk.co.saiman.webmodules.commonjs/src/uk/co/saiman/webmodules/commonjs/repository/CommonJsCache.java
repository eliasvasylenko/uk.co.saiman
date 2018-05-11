package uk.co.saiman.webmodules.commonjs.repository;

import static java.nio.file.Files.walk;
import static java.util.Comparator.reverseOrder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;

import uk.co.saiman.webmodules.commonjs.registry.RegistryResolutionException;
import uk.co.saiman.webmodules.semver.Version;

/**
 * A shared local cache for resources derived from CommonJS registry packages.
 * 
 * @author Elias N Vasylenko
 *
 */
public class CommonJsCache {
  private static final int BUFFER_SIZE = 1024;
  private static final String SHASUM_CACHE = "shasum";

  private final Path cacheRoot;
  private final String moduleName;
  private final Version moduleVersion;

  private final Optional<String> sha1;

  public CommonJsCache(Path cacheRoot, String moduleName, Version moduleVersion) {
    this(cacheRoot, moduleName, moduleVersion, Optional.empty());
  }

  public CommonJsCache(Path cacheRoot, String moduleName, Version moduleVersion, String sha1) {
    this(cacheRoot, moduleName, moduleVersion, Optional.of(sha1));
  }

  private CommonJsCache(
      Path cacheRoot,
      String moduleName,
      Version moduleVersion,
      Optional<String> sha1) {
    this.cacheRoot = cacheRoot;
    this.moduleName = moduleName;
    this.moduleVersion = moduleVersion;
    this.sha1 = sha1;
  }

  public String getModuleName() {
    return moduleName;
  }

  public Optional<String> getSha1() {
    return sha1;
  }

  public Path getCacheRoot() {
    return sha1
        .map(sha1 -> cacheRoot.resolve(SHASUM_CACHE).resolve(sha1))
        .orElse(cacheRoot.resolve(moduleName).resolve(moduleVersion.toString()));
  }

  public Path fetchResource(String resourceName, Consumer<CacheEntry> prepare) {
    return fetchResource(resourceName, prepare, true);
  }

  public Path fetchUnstableResource(String resourceName, Consumer<CacheEntry> prepare) {
    return fetchResource(resourceName, prepare, false);
  }

  private Path fetchResource(String resourceName, Consumer<CacheEntry> prepare, boolean stable) {
    Path destination = getCacheRoot().resolve(resourceName);

    if (Files.exists(destination)) {
      if (sha1.isPresent() && stable) {
        return destination;
      } else {
        try {
          clearCache(destination);
        } catch (IOException e) {
          throw new RegistryResolutionException("Unable to clear destination " + destination, e);
        }
      }
    }

    CacheEntry entry = new CacheEntry(destination);
    try {
      prepare.accept(entry);
    } catch (Exception e) {
      if (Files.exists(destination)) {
        try {
          clearCache(destination);
        } catch (IOException h) {
          e.addSuppressed(h);
        }
      }
      throw e;
    }
    entry.complete();

    return destination;
  }

  private void clearCache(Path destination) throws IOException {
    walk(destination).sorted(reverseOrder()).map(Path::toFile).forEach(File::delete);
  }

  public static byte[] getBytes(InputStream input) throws IOException {
    try (ByteArrayOutputStream buffered = new ByteArrayOutputStream()) {
      byte[] readBuffer = new byte[BUFFER_SIZE];
      int len = 0;
      while ((len = input.read(readBuffer)) != -1) {
        buffered.write(readBuffer, 0, len);
      }

      return buffered.toByteArray();
    }
  }
}
