/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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

import uk.co.saiman.chemistry.ChemicalComposition;
import uk.co.saiman.simulation.instrument.SimulatedSampleImage;

public class SimulatedSampleImageConfiguration {
	private SimulatedSampleImage image;
	private ChemicalComposition red;
	private ChemicalComposition green;
	private ChemicalComposition blue;

	public SimulatedSampleImageConfiguration() {
		image = new SimulatedSampleImage() {
			@Override
			public int getWidth() {
				return 0;
			}

			@Override
			public double getRed(int x, int y) {
				return 0;
			}

			@Override
			public int getHeight() {
				return 0;
			}

			@Override
			public double getGreen(int x, int y) {
				return 0;
			}

			@Override
			public double getBlue(int x, int y) {
				return 0;
			}

			@Override
			public String toString() {
				return "empty image...";
			}
		};
	}

	public SimulatedSampleImage getSampleImage() {
		return image;
	}

	public void setSampleImage(SimulatedSampleImage image) {
		this.image = image;
	}

	public ChemicalComposition getRedChemical() {
		return red;
	}

	public void setRedChemical(ChemicalComposition red) {
		this.red = red;
	}

	public ChemicalComposition getGreenChemical() {
		return green;
	}

	public void setGreenChemical(ChemicalComposition green) {
		this.green = green;
	}

	public ChemicalComposition getBlueChemical() {
		return blue;
	}

	public void setBlueChemical(ChemicalComposition blue) {
		this.blue = blue;
	}
}