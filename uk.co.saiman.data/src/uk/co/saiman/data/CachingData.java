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
import java.lang.ref.SoftReference;

import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.data.resource.Location;
import uk.co.saiman.data.resource.Resource;

public class CachingData<T> extends SimpleData<T> {
  private SoftReference<T> dataReference;

  public CachingData(Resource resource, DataFormat<T> format) {
    super(resource, format);
    this.dataReference = new SoftReference<>(null);
  }

  public CachingData(Location location, String name, DataFormat<T> format) throws IOException {
    this(location.getResource(name, format.getExtension()), format);
  }

  @Override
  public boolean save() {
    if (super.save()) {
      dataReference = new SoftReference<>(getImpl());
      setImpl(null);
      return true;
    }
    return false;
  }

  /**
   * Mark the data as inconsistent with the previously saved state.
   */
  @Override
  public void makeDirty() {
    T data = dataReference.get();

    if (data != null) {
      super.makeDirty();
      setImpl(data);
    }
  }

  @Override
  public boolean set(T data) {
    dataReference = new SoftReference<>(null);
    return super.set(data);
  }

  /**
   * Get the data, loading from the input channel if necessary.
   * 
   * @return the data
   */
  @Override
  public T get() {
    T data = super.get();

    if (data == null) {
      data = dataReference.get();

      if (data == null) {
        load();
        data = super.get();
      }
    }

    return data;
  }
}
