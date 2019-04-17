/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.webmodules.commonjs.
 *
 * uk.co.saiman.webmodules.commonjs is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.webmodules.commonjs is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.webmodule.commonjs.cache;

import static java.nio.file.Files.walk;
import static java.util.Comparator.reverseOrder;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import uk.co.saiman.function.ThrowingConsumer;
import uk.co.saiman.function.ThrowingSupplier;
import uk.co.saiman.webmodule.commonjs.RegistryResolutionException;

/**
 * A shared local cache for resources derived from CommonJS registry packages.
 * 
 * @author Elias N Vasylenko
 *
 */
public class Cache {
  private static final int BUFFER_SIZE = 1024;
  private static final int MAXIMUM_DOWNLOAD_ATTEMPTS = 3;

  private final Path cacheRoot;

  public Cache(Path cacheRoot) {
    this.cacheRoot = cacheRoot;
  }

  public Path getCacheRoot() {
    return cacheRoot;
  }

  public Path fetchResource(String resourceName, ThrowingConsumer<CacheEntry, IOException> prepare)
      throws IOException {
    return fetchResource(resourceName, prepare, Retention.STRONG);
  }

  public <E extends Exception> Path fetchResource(
      String resourceName,
      ThrowingConsumer<CacheEntry, E> prepare,
      Retention retention) throws E {
    Path destination = getCacheRoot().resolve(resourceName);

    if (Files.exists(destination)) {
      if (retention == Retention.STRONG) {
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
    int attempt = MAXIMUM_DOWNLOAD_ATTEMPTS;
    do {
      try {
        prepare.accept(entry);
        attempt = 0;
      } catch (Exception e) {
        if (--attempt == 0) {
          if (Files.exists(destination)) {
            try {
              clearCache(destination);
            } catch (IOException h) {
              e.addSuppressed(h);
            }
          }
          throw e;
        }
      }
    } while (attempt > 0);

    entry.complete();

    return destination;
  }

  private void clearCache(Path destination) throws IOException {
    walk(destination).sorted(reverseOrder()).map(Path::toFile).forEach(File::delete);
  }

  public static <T extends Throwable> byte[] getBytes(ThrowingSupplier<InputStream, T> input)
      throws T, IOException {
    try (InputStream stream = input.get()) {
      return getBytes(stream);
    }
  }

  public static byte[] getBytes(InputStream input) throws IOException {
    input = new BufferedInputStream(input);

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
