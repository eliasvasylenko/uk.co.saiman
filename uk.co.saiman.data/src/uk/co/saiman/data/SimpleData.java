/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Objects;

import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.data.format.Payload;
import uk.co.saiman.data.resource.Location;
import uk.co.saiman.data.resource.Resource;

public class SimpleData<T> implements Data<T> {
  private final Resource resource;
  private final DataFormat<T> format;
  private boolean dirty;
  private T data;

  public SimpleData(Resource resource, DataFormat<T> format) {
    this.resource = requireNonNull(resource);
    this.format = requireNonNull(format);
  }

  public SimpleData(Location location, String name, DataFormat<T> format) {
    this(location.getResource(name, format.getExtension()), format);
  }

  @Override
  public Resource getResource() {
    return resource;
  }

  @Override
  public DataFormat<T> getFormat() {
    return format;
  }

  @Override
  public boolean save() {
    if (isDirty()) {
      this.dirty = false;

      T value = getImpl();
      if (value == null) {
        try {
          resource.delete();
        } catch (IOException e) {
          throw new DataException("Unable to clear data", e);
        }
      } else {
        try (WritableByteChannel writeChannel = resource.write()) {
          format.save(writeChannel, new Payload<>(value));
        } catch (IOException e) {
          throw new DataException("Unable to write data", e);
        }
      }

      return true;
    }
    return false;
  }

  @Override
  public boolean load() {
    if (isDirty()) {
      this.dirty = false;

      try (ReadableByteChannel readChannel = resource.read()) {
        setImpl(format.load(readChannel).data);
      } catch (IOException e) {
        throw new DataException("Unable to read data", e);
      }

      return true;
    }
    return false;
  }

  protected void setImpl(T data) {
    this.data = data;
  }

  protected T getImpl() {
    return data;
  }

  @Override
  public boolean isDirty() {
    return dirty;
  }

  @Override
  public void makeDirty() {
    dirty = true;
  }

  @Override
  public boolean set(T data) {
    requireNonNull(data);
    if (!Objects.equals(getImpl(), data)) {
      setImpl(data);
      makeDirty();
      return true;
    }
    return false;
  }

  @Override
  public boolean unset() {
    if (data != null) {
      data = null;
      makeDirty();
      return true;
    }
    return false;
  }

  @Override
  public T get() {
    return getImpl();
  }
}
