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
package uk.co.saiman.webmodules.commonjs.registry.impl;

import java.net.MalformedURLException;
import java.net.URL;

import uk.co.saiman.webmodules.commonjs.registry.Archive;
import uk.co.saiman.webmodules.commonjs.registry.ArchiveType;
import uk.co.saiman.webmodules.commonjs.registry.RegistryResolutionException;

public class ArchiveImpl implements Archive {
  private final URL url;
  private final ArchiveType type;

  public ArchiveImpl(String archiveUrl, ArchiveType type) {
    try {
      this.url = new URL(archiveUrl);
    } catch (MalformedURLException e) {
      throw new RegistryResolutionException(
          "Failed to resolve " + type + " archive from URL " + archiveUrl,
          e);
    }

    this.type = type;
  }

  @Override
  public URL getURL() {
    return url;
  }

  @Override
  public ArchiveType getType() {
    return type;
  }
}
