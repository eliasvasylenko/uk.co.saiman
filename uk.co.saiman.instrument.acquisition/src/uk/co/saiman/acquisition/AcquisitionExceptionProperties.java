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
 * This file is part of uk.co.saiman.instrument.acquisition.
 *
 * uk.co.saiman.instrument.acquisition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.instrument.acquisition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.acquisition;

import uk.co.saiman.text.properties.Nested;
import uk.co.saiman.text.properties.PropertyConfiguration;
import uk.co.saiman.text.properties.SaiProperties;
import uk.co.saiman.text.properties.PropertyConfiguration.KeyCase;

/**
 * Localized text resource accessor for acquisition engine items.
 * 
 * @author Elias N Vasylenko
 */
@SuppressWarnings("javadoc")
@PropertyConfiguration(keyCase = KeyCase.LOWER, keySplitString = ".")
public interface AcquisitionExceptionProperties {
	@Nested
	SaiProperties sai();

	String countMustBePositive();

	String noSignal();

	String alreadyAcquiring();

	String experimentInterrupted();

	String unexpectedException();
}
