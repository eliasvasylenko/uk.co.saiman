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
 * This file is part of uk.co.saiman.properties.
 *
 * uk.co.saiman.properties is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.properties is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.properties;

public class ResourceBundleDescriptor {
  private final ClassLoader classLoader;
  private final String location;

  public ResourceBundleDescriptor(ClassLoader classLoader, String location) {
    this.classLoader = classLoader;
    this.location = location;
  }

  public ClassLoader getClassLoader() {
    return classLoader;
  }

  public String getLocation() {
    return location;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!(obj instanceof ResourceBundleDescriptor))
      return false;

    ResourceBundleDescriptor that = (ResourceBundleDescriptor) obj;

    return this.classLoader.equals(that.classLoader) && this.location.equals(that.location);
  }

  @Override
  public int hashCode() {
    return classLoader.hashCode() ^ location.hashCode();
  }

  @Override
  public String toString() {
    return location;
  }
}
