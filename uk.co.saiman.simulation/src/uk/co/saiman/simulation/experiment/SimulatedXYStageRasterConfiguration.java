/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,-========\     ,`===\    /========== \
 *      /== \___/== \  ,`==.== \   \__/== \___\/
 *     /==_/____\__\/,`==__|== |     /==  /
 *     \========`. ,`========= |    /==  /
 *   ___`-___)== ,`== \____|== |   /==  /
 *  /== \__.-==,`==  ,`    |== '__/==  /_
 *  \======== /==  ,`      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.simulation.
 *
 * uk.co.saiman.simulation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.simulation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.simulation.experiment;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

import uk.co.saiman.experiment.sample.XYStageConfiguration;
import uk.co.saiman.measurement.Units;

public class SimulatedXYStageRasterConfiguration extends SimulatedSampleImageConfiguration
		implements XYStageConfiguration {
	private final Units units;

	private String name;
	private Quantity<Length> x;
	private Quantity<Length> y;

	public SimulatedXYStageRasterConfiguration(Units units) {
		this.units = units;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Quantity<Length> getMinimumX() {
		return units.metre().milli().getQuantity(0);
	}

	@Override
	public Quantity<Length> getMaximumX() {
		if (getSampleImage() == null) {
			return getMinimumX();
		} else {
			return units.metre().milli().getQuantity(getSampleImage().getWidth());
		}
	}

	@Override
	public Quantity<Length> getMinimumY() {
		return units.metre().milli().getQuantity(0);
	}

	@Override
	public Quantity<Length> getMaximumY() {
		if (getSampleImage() == null) {
			return getMinimumY();
		} else {
			return units.metre().milli().getQuantity(getSampleImage().getHeight());
		}
	}

	@Override
	public Quantity<Length> getX() {
		return x;
	}

	@Override
	public void setX(Quantity<Length> offset) {
		x = offset;
	}

	@Override
	public Quantity<Length> getY() {
		return y;
	}

	@Override
	public void setY(Quantity<Length> offset) {
		y = offset;
	}

	@Override
	public String toString() {
		return "[" + x + ", " + y + "]";
	}
}
