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
 * This file is part of uk.co.saiman.experiment.spectrum.msapex.
 *
 * uk.co.saiman.experiment.spectrum.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.spectrum.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.spectrum.msapex;

import static uk.co.saiman.experiment.processing.Processing.PROCESSING_VARIABLE;
import static uk.co.saiman.experiment.processing.Processing.PROCESSING_VARIABLE_ID;

import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.core.di.extensions.Service;

import uk.co.saiman.data.function.processing.DataProcessor;
import uk.co.saiman.eclipse.ui.Children;
import uk.co.saiman.eclipse.ui.ToBeRendered;
import uk.co.saiman.eclipse.utilities.ContextBuffer;
import uk.co.saiman.experiment.Step;
import uk.co.saiman.experiment.event.ChangeVariableEvent;
import uk.co.saiman.experiment.processing.Processing;
import uk.co.saiman.experiment.processing.msapex.ProcessorCell;
import uk.co.saiman.experiment.spectrum.msapex.i18n.SpectrumProperties;

public class SpectrumProcessingCell {
  @Inject
  @Service
  private SpectrumProperties properties;
  @Inject
  private Step step;

  @ToBeRendered
  public static boolean render(@Optional @Named(PROCESSING_VARIABLE_ID) Object processing) {
    return processing != null;
  }

  @Children(snippetId = ProcessorCell.ID)
  public Stream<ContextBuffer> updateChildren(@Optional ChangeVariableEvent event) {
    if (event == null || event.step() != step || event.variable() != PROCESSING_VARIABLE) {
      return null;
    }
    return step
        .getVariables()
        .get(PROCESSING_VARIABLE)
        .orElseGet(Processing::new)
        .steps()
        .map(step -> ContextBuffer.empty().set(DataProcessor.class, step));
  }
}
