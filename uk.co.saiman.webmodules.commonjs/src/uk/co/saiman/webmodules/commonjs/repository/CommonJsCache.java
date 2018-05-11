package uk.co.saiman.webmodules.commonjs.repository;

import static java.nio.file.Files.walk;
import static java.util.Comparator.reverseOrder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import uk.co.saiman.webmodules.commonjs.registry.RegistryResolutionException;

/**
 * A shared local cache for resources derived from CommonJS registry packages.
 * 
 * @author Elias N Vasylenko
 *
 */
public class CommonJsCache {
  private static final int BUFFER_SIZE = 1024;
  private static final String SHASUM_CACHE = "shasum";

  private final CommonJsBundleVersion bundleVersion;

  public CommonJsCache(CommonJsBundleVersion bundleVersion) {
    this.bundleVersion = bundleVersion;
  }

  CommonJsBundleVersion getBundleVersion() {
    return bundleVersion;
  }

  CommonJsBundle getBundle() {
    return bundleVersion.getBundle();
  }

  public Path getCacheRoot() {
    Path cacheRoot = getBundle().getRepository().getCache();
    return bundleVersion
        .getSha1()
        .map(sha1 -> cacheRoot.resolve(SHASUM_CACHE).resolve(sha1))
        .orElse(
            cacheRoot
                .resolve(getBundle().getModuleName())
                .resolve(getBundleVersion().getSemver().toString()));
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
      if (getBundleVersion().getSha1().isPresent() && stable) {
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
