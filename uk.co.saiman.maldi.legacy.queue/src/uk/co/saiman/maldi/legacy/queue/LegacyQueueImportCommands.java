/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.maldi.legacy.queue.
 *
 * uk.co.saiman.maldi.legacy.queue is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.maldi.legacy.queue is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.maldi.legacy.queue;

import static uk.co.saiman.shell.converters.ShellProperties.COMMAND_FUNCTION_KEY;
import static uk.co.saiman.shell.converters.ShellProperties.COMMAND_SCOPE_PROPERTY;

import java.nio.file.Path;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.experiment.declaration.ExperimentId;
import uk.co.saiman.experiment.definition.ExperimentDefinition;
import uk.co.saiman.maldi.sample.MaldiSampleAreaExecutor;
import uk.co.saiman.maldi.sample.MaldiSamplePlateExecutor;
import uk.co.saiman.maldi.spectrum.MaldiSpectrumExecutor;

/**
 * Provide commands to the GoGo shell for importing from the legacy Maldi
 * experiment queue format.
 * 
 * @author Elias N Vasylenko
 */
@Component(immediate = true, service = LegacyQueueImportCommands.class, property = { COMMAND_SCOPE_PROPERTY,
    COMMAND_FUNCTION_KEY + "=importLegacyQueue" })
public class LegacyQueueImportCommands {

  private LegacyQueueImporter importer;

  @Activate
  public LegacyQueueImportCommands(
      @Reference MaldiSamplePlateExecutor samplePlate,
      @Reference MaldiSampleAreaExecutor sampleArea,
      @Reference MaldiSpectrumExecutor spectrum) {
    this.importer = new LegacyQueueImporter(samplePlate, sampleArea, spectrum);
  }

  public ExperimentDefinition importLegacyQueue(ExperimentId name, Path queueFile) {
    return importer.importLegacyQueue(name, queueFile);
  }
}
