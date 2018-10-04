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
 * This file is part of uk.co.saiman.data.
 *
 * uk.co.saiman.data is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.data is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.data;

import java.io.IOException;

import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.data.resource.Location;
import uk.co.saiman.data.resource.Resource;

/**
 * A container for a data object which is backed by a {@link Resource resource}
 * according to a {@link DataFormat format}.
 * 
 * @author Elias N Vasylenko
 *
 * @param <T>
 *          the type of the data object
 */
public interface Data<T> {
  static <T> Data<T> locate(Resource resource, DataFormat<T> format) {
    return new SimpleData<>(resource, format);
  }

  static <T> Data<T> locate(Location location, String name, DataFormat<T> format) {
    try {
      return new SimpleData<>(location.getResource(name, format.getExtension()), format);
    } catch (IOException e) {
      throw new DataException("Failed to prepare location", e);
    }
  }

  Resource getResource();

  void relocate(Resource resource) throws DataException;

  void relocate(Location location) throws DataException;

  void relocate(Location location, String name) throws DataException;

  DataFormat<T> getFormat();

  boolean save() throws DataException;

  boolean load() throws DataException;

  /**
   * @return true if the resource has been changed since it was last saved, false
   *         otherwise
   */
  boolean isDirty();

  /**
   * Mark the data as inconsistent with the previously saved state.
   */
  void makeDirty();

  boolean set(T value);

  boolean unset();

  /**
   * Get the data, loading from the input channel if necessary.
   * 
   * @return the data
   */
  T get();
}
