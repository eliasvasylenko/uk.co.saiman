package uk.co.saiman.chemistry.isotope;

import java.util.Comparator;

public class MassAbundance implements Comparable<MassAbundance> {
	private double mass;
	private double abundance;

	// expected error / variance margins
	private double massVariance; // given by approximate variance in merges
	private double abundanceVariance; // given by original data

	public MassAbundance() {
		mass = 0;
		abundance = 0;
		massVariance = 0;
		abundanceVariance = 0;
	}

	public MassAbundance(MassAbundance massAbundance) {
		mass = massAbundance.mass;
		abundance = massAbundance.abundance;
		massVariance = massAbundance.massVariance;
		abundanceVariance = massAbundance.abundanceVariance;
	}

	public MassAbundance(double mass, double abundance) {
		this.mass = mass;
		this.abundance = abundance;
		massVariance = 0;
		abundanceVariance = 0;
	}

	public MassAbundance(double mass, double abundance, double massVariance, double abundanceVariance) {
		this.mass = mass;
		this.abundance = abundance;
		this.massVariance = massVariance;
		this.abundanceVariance = abundanceVariance;
	}

	public double getMass() {
		return mass;
	}

	public MassAbundance withMass(double mass) {
		return new MassAbundance(mass, abundance, massVariance, abundanceVariance);
	}

	public double getAbundance() {
		return abundance;
	}

	public MassAbundance withMassVariance(double massVariance) {
		return new MassAbundance(mass, abundance, massVariance, abundanceVariance);
	}

	public double getMassVariance() {
		return massVariance;
	}

	public MassAbundance withAbundance(double abundance) {
		return new MassAbundance(mass, abundance, massVariance, abundanceVariance);
	}

	public double getAbundanceVariance() {
		return abundanceVariance;
	}

	public MassAbundance withAbundanceVariance(double abundanceVariance) {
		return new MassAbundance(mass, abundance, massVariance, abundanceVariance);
	}

	@Override
	public String toString() {
		String string = "";

		string += abundance + "@" + mass;

		return string;
	}

	@Override
	public int compareTo(MassAbundance that) {
		if (this == that)
			return 0;

		if (this.mass > that.mass)
			return 1;
		if (that.mass > this.mass)
			return -1;
		return 0;
	}

	@Override
	public boolean equals(Object that) {
		if (this == that)
			return true;

		if (!(that instanceof MassAbundance))
			return false;

		MassAbundance thatMassAbundance = (MassAbundance) that;

		if (this.mass != thatMassAbundance.mass)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return ((Double) mass).hashCode();
	}

	public static class AbundanceComparator implements Comparator<MassAbundance> {
		@Override
		public int compare(MassAbundance first, MassAbundance second) {
			if (first.abundance < second.abundance)
				return 1;
			return -1;
		}
	}
}
