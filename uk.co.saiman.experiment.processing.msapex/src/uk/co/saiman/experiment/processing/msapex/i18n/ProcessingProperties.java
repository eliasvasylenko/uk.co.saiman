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
 * This file is part of uk.co.saiman.experiment.processing.msapex.
 *
 * uk.co.saiman.experiment.processing.msapex is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.processing.msapex is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.processing.msapex.i18n;

import uk.co.saiman.properties.Localized;
import uk.co.saiman.properties.service.PropertiesService;

@PropertiesService
public interface ProcessingProperties {
  Localized<String> convolutionProcessor();

  Localized<String> convolutionProcessorDescription();

  Localized<String> gaussianSmoothProcessor();

  Localized<String> boxFilterProcessor();

  Localized<String> missingProcessor();

  Localized<String> standardDeviationLabel();

  Localized<String> widthLabel();

  Localized<String> vectorLabel();

  Localized<String> centreLabel();

  Localized<String> processing();
}
