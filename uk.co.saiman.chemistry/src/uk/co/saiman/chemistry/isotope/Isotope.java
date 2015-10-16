package uk.co.saiman.chemistry.isotope;

import uk.co.saiman.chemistry.Element;

public class Isotope implements Comparable<Isotope> {
  private Element element;
  private int massNumber;
  private double mass;
  private double abundance;

  public Isotope() {
    massNumber = 0;
    mass = 0;
    abundance = 0; // default to not naturally occurring
    element = null;
  }

  public Isotope(Element element) {
    massNumber = 0;
    mass = 0;
    abundance = 0; // default to not naturally occurring
    this.element = element;
  }

  public Isotope(Isotope isotope) {
    massNumber = isotope.massNumber;
    mass = isotope.mass;
    abundance = isotope.abundance;
    this.element = isotope.element;
  }

  public Isotope(int massNumber, double mass, double abundance, Element element) {
    this.massNumber = massNumber;
    this.mass = mass;
    this.abundance = abundance;
    this.element = element;
  }

  public Element getElement() {
    return element;
  }

  public int getMassNumber() {
    return massNumber;
  }

  public double getMass() {
    return mass;
  }

  public double getAbundance() {
    return abundance;
  }

  public void setElement(Element element) {
    this.element = element;
  }

  public void setMassNumber(int massNumber) {
    this.massNumber = massNumber;
  }

  public void setMass(double mass) {
    this.mass = mass;
  }

  public void setAbundance(double abundance) {
    this.abundance = abundance;
  }

  @Override
  public String toString() {
    String string = "";
    String newLine = System.getProperty("line.separator");

    string += "  Isotope: " + massNumber + newLine;
    string += "    mass: " + mass + newLine;
    string += "    abundance: " + abundance + newLine;

    return string;
  }

  @Override
  public int compareTo(Isotope that) {
    if (this == that)
      return 0;

    return this.massNumber - that.massNumber;
  }

  @Override
  public boolean equals(Object that) {
    if (this == that)
      return true;

    if (!(that instanceof Element))
      return false;

    Isotope thatIsotope = (Isotope) that;

    if (this.massNumber != thatIsotope.massNumber)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    return 23 + massNumber * (massNumber + 4);
  }
}
