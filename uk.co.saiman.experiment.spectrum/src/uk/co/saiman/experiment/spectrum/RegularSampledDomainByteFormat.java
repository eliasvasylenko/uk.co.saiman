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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

import javax.measure.Quantity;
import javax.measure.Unit;

import uk.co.saiman.data.RegularSampledDomain;

public class RegularSampledDomainByteFormat<U extends Quantity<U>>
    implements ByteFormat<RegularSampledDomain<U>> {
  private static final int SIZE = Double.BYTES * 2 + Integer.BYTES;
  private static final String MASS_SPECTRUM_DOMAIN_EXTENSION = "msd";

  private final Unit<U> domainUnit;

  public RegularSampledDomainByteFormat(Unit<U> domainUnit) {
    this.domainUnit = domainUnit;
  }

  @Override
  public String getPathExtension() {
    return MASS_SPECTRUM_DOMAIN_EXTENSION;
  }

  @Override
  public RegularSampledDomain<U> load(ReadableByteChannel inputChannel) throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(SIZE);
    do {
      inputChannel.read(buffer);
    } while (buffer.hasRemaining());
    buffer.flip();
    double frequency = buffer.getDouble();
    double domainStart = buffer.getDouble();
    int depth = buffer.getInt();

    return new RegularSampledDomain<>(domainUnit, depth, frequency, domainStart);
  }

  @Override
  public void save(WritableByteChannel outputChannel, RegularSampledDomain<U> domain)
      throws IOException {
    ByteBuffer buffer = ByteBuffer.allocate(SIZE);
    buffer.putDouble(domain.getFrequency());
    buffer.putDouble(domain.getInterval().getLeftEndpoint());
    buffer.putInt(domain.getDepth());
    buffer.flip();
    do {
      outputChannel.write(buffer);
    } while (buffer.hasRemaining());
  }
}
