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

import static uk.co.saiman.data.function.format.RegularSampledContinuousFunctionFormat.overEncodedDomain;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Time;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.data.format.Payload;
import uk.co.saiman.data.function.SampledContinuousFunction;
import uk.co.saiman.measurement.Units;

@Component
public class RegularSampledSpectrumFormat implements DataFormat<Spectrum> {
  private static final String ID = "uk.co.saiman.experiment.spectrum.sampled.regular.format";

  @Reference
  Units units;

  RegularSampledSpectrumFormat() {}

  public RegularSampledSpectrumFormat(Units units) {}

  @Override
  public String getId() {
    return ID;
  }

  @Override
  public String getExtension() {
    return "rss";
  }

  @Override
  public Payload<Spectrum> load(ReadableByteChannel inputChannel) throws IOException {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException();
  }

  @Override
  public void save(WritableByteChannel outputChannel, Payload<? extends Spectrum> payload)
      throws IOException {
    Unit<Time> domainUnits = payload.data.getRawData().domain().getUnit();
    Unit<Dimensionless> rangeUnits = payload.data.getRawData().range().getUnit();

    byte[] bytes;
    bytes = units.formatUnit(domainUnits).getBytes();
    outputChannel.write(ByteBuffer.wrap(bytes));
    outputChannel.write(ByteBuffer.wrap(new byte[] { 0 }));
    bytes = units.formatUnit(rangeUnits).getBytes();
    outputChannel.write(ByteBuffer.wrap(bytes));
    outputChannel.write(ByteBuffer.wrap(new byte[] { 0 }));

    overEncodedDomain(domainUnits, rangeUnits).save(
        outputChannel,
        new Payload<>((SampledContinuousFunction<Time, Dimensionless>) payload.data.getRawData()));
  }
}
