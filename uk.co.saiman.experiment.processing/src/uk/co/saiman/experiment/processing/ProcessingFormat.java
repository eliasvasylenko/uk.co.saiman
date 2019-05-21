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
 * This file is part of uk.co.saiman.experiment.processing.
 *
 * uk.co.saiman.experiment.processing is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.processing is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.processing;

import static uk.co.saiman.data.format.MediaType.APPLICATION_TYPE;
import static uk.co.saiman.data.format.RegistrationTree.VENDOR;

import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.stream.Stream;

import uk.co.saiman.data.format.DataFormat;
import uk.co.saiman.data.format.MediaType;
import uk.co.saiman.data.format.Payload;
import uk.co.saiman.state.StateList;

public class ProcessingFormat implements DataFormat<Processing> {
  public static final int VERSION = 1;

  public static final MediaType MEDIA_TYPE = new MediaType(
      APPLICATION_TYPE,
      "saiman.processing.v" + VERSION,
      VENDOR).withSuffix("json");

  private final DataFormat<StateList> stateListFormat;
  private final ProcessingService processorService;

  public ProcessingFormat(
      DataFormat<StateList> stateListFormat,
      ProcessingService processorService) {
    this.stateListFormat = stateListFormat;
    this.processorService = processorService;
  }

  @Override
  public String getExtension() {
    return "processor";
  }

  @Override
  public Stream<MediaType> getMediaTypes() {
    return Stream.of(MEDIA_TYPE);
  }

  @Override
  public Payload<? extends Processing> load(ReadableByteChannel inputChannel) throws IOException {
    return new Payload<>(
        ProcessingDeclaration
            .fromState(stateListFormat.load(inputChannel).data)
            .load(processorService));
  }

  @Override
  public void save(WritableByteChannel outputChannel, Payload<? extends Processing> payload)
      throws IOException {
    stateListFormat
        .save(
            outputChannel,
            new Payload<>(ProcessingDeclaration.save(processorService, payload.data).toState()));
  }
}
