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
 * This file is part of uk.co.saiman.experiment.spectrum.
 *
 * uk.co.saiman.experiment.spectrum is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.spectrum is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.spectrum;

import static java.nio.file.Files.newByteChannel;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Path;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Mass;
import javax.measure.quantity.Time;

import uk.co.saiman.data.ContinuousFunction;
import uk.co.saiman.experiment.CachingObservableResource;
import uk.co.saiman.experiment.CachingResource;
import uk.co.strangeskies.function.ThrowingSupplier;
import uk.co.strangeskies.mathematics.expression.Expression;

public class ByteFormatSpectrum<C extends ContinuousFunction<Time, Dimensionless>>
    implements Spectrum {
  private final CachingResource<C> data;

  private final ThrowingSupplier<ReadableByteChannel, IOException> readChannel;
  private final ThrowingSupplier<WritableByteChannel, IOException> writeChannel;
  private final ByteFormat<C> format;

  protected ByteFormatSpectrum(Path location, String name, C data, ByteFormat<C> format) {
    this(location.resolve(name + "." + format.getPathExtension()), data, format);
  }

  protected ByteFormatSpectrum(Path location, C data, ByteFormat<C> format) {
    this(
        () -> newByteChannel(location, READ),
        () -> newByteChannel(location, WRITE, CREATE, TRUNCATE_EXISTING),
        data,
        format);
  }

  protected ByteFormatSpectrum(
      ThrowingSupplier<ReadableByteChannel, IOException> readChannel,
      ThrowingSupplier<WritableByteChannel, IOException> writeChannel,
      C data,
      ByteFormat<C> format) {
    this.readChannel = readChannel;
    this.writeChannel = writeChannel;

    this.data = new CachingObservableResource<>(this::load, this::save, Expression::invalidations);
    this.data.setData(data);

    this.format = format;
  }

  @Override
  public void complete() {
    data.save();
  }

  protected C load() {
    try (ReadableByteChannel readChannel = this.readChannel.get()) {
      return format.load(readChannel);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  protected void save(C data) {
    try (WritableByteChannel writeChannel = this.writeChannel.get()) {
      format.save(writeChannel, data);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public ContinuousFunction<Time, Dimensionless> getRawData() {
    return data.getData();
  }

  @Override
  public ContinuousFunction<Mass, Dimensionless> getCalibratedData() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public SpectrumCalibration getCalibration() {
    // TODO Auto-generated method stub
    return null;
  }
}
