/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
package uk.co.saiman.webmodule.commonjs.repository;

import static uk.co.saiman.bytes.ByteBuffers.toHexString;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import uk.co.saiman.webmodule.commonjs.registry.RegistryResolutionException;

class TarGzInputStream extends TarArchiveInputStream {
  private static final int BUFFER_SIZE = 1024;

  private final InputStream inputStream;
  private final String expectedSha1;
  private final MessageDigest digest;

  public TarGzInputStream(InputStream inputStream, String expectedSha1) throws IOException {
    this(inputStream, expectedSha1, expectedSha1 == null ? null : createSha1MessageDigest());
  }

  private TarGzInputStream(InputStream inputStream, String expectedSha1, MessageDigest digest)
      throws IOException {
    this(
        expectedSha1,
        digest,
        digest != null ? new DigestInputStream(inputStream, digest) : inputStream);
  }

  private TarGzInputStream(String expectedSha1, MessageDigest digest, InputStream inputStream)
      throws IOException {
    super(new GzipCompressorInputStream(inputStream));

    this.inputStream = inputStream;
    this.expectedSha1 = expectedSha1;
    this.digest = digest;
  }

  private static MessageDigest createSha1MessageDigest() {
    try {
      return MessageDigest.getInstance("SHA-1");
    } catch (NoSuchAlgorithmException e) {
      throw new RegistryResolutionException("Unable to validate shasum for resource", e);
    }
  }

  @Override
  public void close() throws IOException {
    try {
      if (expectedSha1 != null) {
        byte[] readBuffer = new byte[BUFFER_SIZE];
        while (inputStream.read(readBuffer) != -1) {}

        String sha1 = toHexString(ByteBuffer.wrap(digest.digest()));

        if (!sha1.equals(expectedSha1)) {
          throw new RegistryResolutionException(
              "Failed to validate shasum " + sha1 + " for resource, expecting " + expectedSha1);
        }
      }
    } finally {
      super.close();
    }
  }

  public TarArchiveEntry findEntry(String name) {
    name = name.toLowerCase();

    TarArchiveEntry tarEntry;

    try {
      do {
        tarEntry = getNextTarEntry();
      } while (tarEntry != null && !tarEntry.getName().toLowerCase().equals(name));
    } catch (IOException e) {
      throw new RegistryResolutionException("Failed to open " + name);
    }

    if (tarEntry == null) {
      throw new RegistryResolutionException("Failed to locate " + name);
    }

    return tarEntry;
  }
}
