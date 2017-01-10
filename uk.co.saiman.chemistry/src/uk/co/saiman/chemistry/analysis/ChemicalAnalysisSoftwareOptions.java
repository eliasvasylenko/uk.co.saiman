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
 * This file is part of uk.co.saiman.chemistry.
 *
 * uk.co.saiman.chemistry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.chemistry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.chemistry.analysis;

/*
 * @author Alexis Polley
 */

public class ChemicalAnalysisSoftwareOptions {
	double errorPPM;
	double errorDa;
	int minSeriesPeaks;
	double knownDeltaMass;
	byte constrainDeltaMass;
	byte deltaMassErrorAdjacentOnly;
	byte determineDeltaMassFromSelection;
	byte useRelativeError;
	int maximumMasses;
	byte lowPrecisionMode;
	double mergeDistance;
	double resolution;
	byte getResolutionFromMergeDistance;
	double minimumAbundance;

	public ChemicalAnalysisSoftwareOptions() {
		errorPPM = 500;
		errorDa = 0.5;
		minSeriesPeaks = 5;
		knownDeltaMass = 0;
		constrainDeltaMass = 0;
		deltaMassErrorAdjacentOnly = 1;
		determineDeltaMassFromSelection = 0;
		useRelativeError = 0;
		maximumMasses = 10000;
		lowPrecisionMode = 0;
		mergeDistance = 0.0005;
		resolution = 1000;
		getResolutionFromMergeDistance = 0;
		minimumAbundance = 0.5;
	}

	public double getErrorPPM() {
		return errorPPM;
	}

	public void setErrorPPM(double errorPPM) {
		this.errorPPM = errorPPM;
	}

	public double getErrorDa() {
		return errorDa;
	}

	public void setErrorDa(double errorDa) {
		this.errorDa = errorDa;
	}

	public int getMinSeriesPeaks() {
		return minSeriesPeaks;
	}

	public void setMinSeriesPeaks(int minSeriesPeaks) {
		this.minSeriesPeaks = minSeriesPeaks;
	}

	public double getKnownDeltaMass() {
		return knownDeltaMass;
	}

	public void setKnownDeltaMass(double knownDeltaMass) {
		this.knownDeltaMass = knownDeltaMass;
	}

	public boolean getConstrainDeltaMass() {
		boolean ret = false;
		ret = (constrainDeltaMass != 0);
		return ret;
	}

	public void setConstrainDeltaMass(boolean toThis) {
		byte v = 0;
		if (toThis) {
			v = (byte) 255;
		}
		this.constrainDeltaMass = v;
	}

	public boolean getDeltaMassErrorAdjacentOnly() {
		boolean ret = false;
		ret = (deltaMassErrorAdjacentOnly != 0);
		return ret;
	}

	public void setDeltaMassErrorAdjacentOnly(boolean toThis) {
		byte v = 0;
		if (toThis) {
			v = (byte) 255;
		}
		this.deltaMassErrorAdjacentOnly = v;
	}

	public boolean getDetermineDeltaMassFromSelection() {
		boolean ret = false;
		ret = (determineDeltaMassFromSelection != 0);
		return ret;
	}

	public void setDetermineDeltaMassFromSelection(boolean toThis) {
		byte v = 0;
		if (toThis) {
			v = (byte) 255;
		}
		this.determineDeltaMassFromSelection = v;
	}

	public boolean getUseRelativeError() {
		boolean ret = false;
		ret = (useRelativeError != 0);
		return ret;
	}

	public void setUseRelativeError(boolean toThis) {
		byte v = 0;
		if (toThis) {
			v = (byte) 255;
		}
		this.useRelativeError = v;
	}

	public int getMaximumMasses() {
		return maximumMasses;
	}

	public void setMaximumMasses(int maximumMasses) {
		this.maximumMasses = maximumMasses;
	}

	public boolean getLowPrecisionMode() {
		boolean ret = false;
		ret = (lowPrecisionMode != 0);
		return ret;
	}

	public void setLowPrecisionMode(boolean toThis) {
		byte v = 0;
		if (toThis) {
			v = (byte) 255;
		}
		this.lowPrecisionMode = v;
	}

	public double getMergeDistance() {
		return mergeDistance;
	}

	public void setMergeDistance(double mergeDistance) {
		this.mergeDistance = mergeDistance;
	}

	public double getResolution() {
		return resolution;
	}

	public void setResolution(double resolution) {
		this.resolution = resolution;
	}

	public boolean getGetResolutionFromMergeDistance() {
		boolean ret = false;
		ret = (getResolutionFromMergeDistance != 0);
		return ret;
	}

	public void setGetResolutionFromMergeDistance(boolean toThis) {
		byte v = 0;
		if (toThis) {
			v = (byte) 255;
		}
		this.getResolutionFromMergeDistance = v;
	}

	public double getMinimumAbundance() {
		return minimumAbundance;
	}

	public void setMinimumAbundance(double minimumAbundance) {
		this.minimumAbundance = minimumAbundance;
	}

	public double getError() {
		return getUseRelativeError() ? getErrorPPM() : getErrorDa();
	}
}
