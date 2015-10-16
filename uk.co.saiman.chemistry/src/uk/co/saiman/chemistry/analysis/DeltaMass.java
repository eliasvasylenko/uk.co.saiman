package uk.co.saiman.chemistry.analysis;

import uk.co.saiman.chemistry.ChemicalMeasurement;

public class DeltaMass {
	private double dm;
	private ChemicalMeasurement massSpectralChemicalCompoundInterface;

	public double getDeltaMass() {
		return dm;
	}

	public void setDeltaMass(double dm) {
		this.dm = dm;
	}

	public DeltaMass() {
	}

	public DeltaMass(DeltaMass other) {
		dm = other.dm;
		massSpectralChemicalCompoundInterface = other.massSpectralChemicalCompoundInterface;
	}

	public ChemicalMeasurement getChemicalInterface() {
		return massSpectralChemicalCompoundInterface;
	}

	public void setChemicalInterface(
			ChemicalMeasurement massSpectralChemicalCompoundInterface) {
		this.massSpectralChemicalCompoundInterface = massSpectralChemicalCompoundInterface;
	}

	public DeltaMass(double dm, ChemicalMeasurement massSpectralChemicalCompoundInterface) {
		this.dm = dm;
		this.massSpectralChemicalCompoundInterface = massSpectralChemicalCompoundInterface;
	}
}