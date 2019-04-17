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
package uk.co.saiman.webmodule.commonjs;

import static java.util.Objects.requireNonNull;
import static uk.co.saiman.webmodule.commonjs.DependencyKind.GIT;
import static uk.co.saiman.webmodule.commonjs.DependencyKind.INVALID;
import static uk.co.saiman.webmodule.commonjs.DependencyKind.URI;
import static uk.co.saiman.webmodule.commonjs.DependencyKind.VERSION_RANGE;

import java.net.URI;
import java.util.Optional;

import uk.co.saiman.webmodule.PackageId;
import uk.co.saiman.webmodule.semver.Range;

public class Dependency {
  public static Dependency empty(PackageId id) {
    return new Dependency(id, INVALID);
  }

  public static Dependency parse(PackageId id, String dependencyString) {
    RuntimeException exception;
    try {
      Range range = Range.parse(dependencyString);
      return new Dependency(id, VERSION_RANGE, range);
    } catch (RuntimeException e) {
      exception = e;
    }

    try {
      if (dependencyString.startsWith("git")) {
        URI uri = new URI(dependencyString);
        return new Dependency(id, GIT, uri);
      }
    } catch (Exception e) {
      exception.addSuppressed(e);
    }

    try {
      if (!dependencyString.startsWith("git")) {
        URI uri = new URI(dependencyString);
        return new Dependency(id, URI, uri);
      }
    } catch (Exception e) {
      exception.addSuppressed(e);
    }

    try {
      // TODO user/repo (github url)...
    } catch (RuntimeException e) {
      exception.addSuppressed(e);
    }

    try {
      // TODO "file:../foo/bar"
    } catch (RuntimeException e) {
      exception.addSuppressed(e);
    }

    throw exception;
  }

  private final PackageId id;
  private final DependencyKind<?> kind;
  private final Object object;

  private <T> Dependency(PackageId id, DependencyKind<T> kind) {
    this.id = id;
    this.kind = requireNonNull(kind);
    this.object = null;
  }

  public <T> Dependency(PackageId id, DependencyKind<T> kind, T object) {
    this.id = id;
    this.kind = requireNonNull(kind);
    this.object = requireNonNull(object);
  }

  public PackageId getPackageId() {
    return id;
  }

  public DependencyKind<?> getKind() {
    return kind;
  }

  @SuppressWarnings("unchecked")
  public <T> Optional<T> getVersion(DependencyKind<T> kind) {
    if (this.kind == kind) {
      return Optional.of((T) object);
    } else {
      return Optional.empty();
    }
  }
}
