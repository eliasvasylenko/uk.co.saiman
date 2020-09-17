/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.webmodules.commonjs.registry.
 *
 * uk.co.saiman.webmodules.commonjs.registry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.webmodules.commonjs.registry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.webmodule.commonjs.registry;

import static java.util.Objects.requireNonNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import uk.co.saiman.webmodule.commonjs.RegistryResolutionException;
import uk.co.saiman.webmodule.commonjs.Resource;
import uk.co.saiman.webmodule.commonjs.ResourceType;

public class RegistryResource implements Resource {
  private final URL url;
  private final ResourceType type;
  private final Optional<String> sha1;

  public RegistryResource(String archiveUrl, ResourceType type) {
    this(archiveUrl, type, Optional.empty());
  }

  public RegistryResource(String archiveUrl, ResourceType type, String sha1) {
    this(archiveUrl, type, Optional.of(sha1.toUpperCase()));
  }

  private RegistryResource(String archiveUrl, ResourceType type, Optional<String> sha1) {
    try {
      this.url = new URL(requireNonNull(archiveUrl));
    } catch (MalformedURLException e) {
      throw new RegistryResolutionException(
          "Failed to resolve " + type + " archive from URL " + archiveUrl,
          e);
    }

    this.type = requireNonNull(type);
    this.sha1 = sha1;
  }

  @Override
  public URL getUrl() {
    return url;
  }

  @Override
  public ResourceType getType() {
    return type;
  }

  @Override
  public Optional<String> getSha1() {
    return sha1;
  }
}
