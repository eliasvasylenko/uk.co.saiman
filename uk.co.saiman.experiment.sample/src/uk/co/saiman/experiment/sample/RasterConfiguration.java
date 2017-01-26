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
 * This file is part of uk.co.saiman.experiment.sample.
 *
 * uk.co.saiman.experiment.sample is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.sample is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.sample;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import uk.co.saiman.instrument.raster.RasterDevice;

/**
 * A base interface for raster configuration.
 * 
 * @author Elias N Vasylenko
 */
public interface RasterConfiguration {
	Quantity<Length> getMaximumWidth();

	Quantity<Length> getMaximumHeight();

	Quantity<Length> getWidth();

	void setWidth(Quantity<Length> distance);

	Quantity<Length> getHeight();

	void setHeight(Quantity<Length> distance);

	int getXSteps();

	void setXSteps(int steps);

	int getYSteps();

	void setYSteps(int steps);

	Quantity<Length> getXResolution();

	Quantity<Length> getYResolution();

	RasterDevice rasterDevice();
}
