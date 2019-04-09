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

import static uk.co.saiman.experiment.processing.Processing.toProcessing;
import static uk.co.saiman.experiment.processing.ProcessingDeclaration.toProcessingDeclaration;

import java.util.stream.Stream;

import uk.co.saiman.data.function.processing.DataProcessor;

public interface ProcessingService {
  Stream<ProcessingStrategy<?>> strategies();

  DataProcessor loadDeclaration(ProcessorDeclaration declaration);

  ProcessorDeclaration saveDeclaration(DataProcessor processor);

  default Processing loadDeclaration(ProcessingDeclaration declaration) {
    return declaration.processors().map(this::loadDeclaration).collect(toProcessing());
  }

  default ProcessingDeclaration saveDeclaration(Processing processing) {
    return processing.steps().map(this::saveDeclaration).collect(toProcessingDeclaration());
  }
}
