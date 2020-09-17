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
package uk.co.saiman.chemistry.isotope;

import static uk.co.saiman.mathematics.Interval.bounded;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import uk.co.saiman.chemistry.ChemicalComposition;
import uk.co.saiman.mathematics.Interval;

public class IdealMassSpectrum {
  // data
  private TreeMap<Double, Double> data;

  // source molecule
  private ChemicalComposition molecule;

  // effective resolution
  private double effectiveResolution;
  private static final double fwhmCoefficient = 0.5 / Math.sqrt(2 * Math.log(2));

  // relative abundance of whole spectrum
  private double relativeAbundance = 1;

  public IdealMassSpectrum() {
    // mass spectrum data
    data = new TreeMap<>();
    this.molecule = null;
  }

  public IdealMassSpectrum(IdealMassSpectrum massSpectrum) {
    data = new TreeMap<>();
    if (massSpectrum.molecule != null) {
      molecule = massSpectrum.molecule;
    } else {
      molecule = null;
    }
    if (massSpectrum.data != null) {
      data = new TreeMap<>();

      Iterator<Double> dataIterator = massSpectrum.data.keySet().iterator();
      Double dataItem;
      while (dataIterator.hasNext()) {
        dataItem = dataIterator.next();
        data.put(dataItem, massSpectrum.data.get(dataItem));
      }
    } else {
      data = null;
    }
    setEffectiveResolution(massSpectrum.getEffectiveResolution());
    relativeAbundance = massSpectrum.relativeAbundance;
  }

  public void calculateForIsotopeDistribution(IsotopeDistribution isotopeDistribution) {
    calculateForIsotopeDistribution(isotopeDistribution, null, null, 0, true);
  }

  public void calculateForIsotopeDistribution(
      IsotopeDistribution isotopeDistribution,
      IdealMassSpectrum extremaSpectra,
      Interval<Double> massRange,
      double sampleResolution,
      boolean normalise) {
    molecule = isotopeDistribution.getMolecule();

    double normal = isotopeDistribution.getTotalAbundance();

    // clear data
    data.clear();

    if (isotopeDistribution.isEmpty()) {
      return;
    }

    relativeAbundance = isotopeDistribution.getRelativeAbundance();

    // distribution data
    SortedSet<MassAbundance> distributionData = isotopeDistribution.getData();

    // initial pass to find largest variance to get range of spectrum and
    // calculate effective standard deviations and peak heights
    double largestVariance = 0;
    Map<MassAbundance, Double> effectiveStandardDeviations = new HashMap<>();
    Map<MassAbundance, Double> peakHeightReciprocals = new HashMap<>();
    Iterator<MassAbundance> distributionIterator = distributionData.iterator();
    MassAbundance massAbundance;

    while (distributionIterator.hasNext()) {
      massAbundance = distributionIterator.next();

      double localPeakWidth = massAbundance.getMass() / getEffectiveResolution() * fwhmCoefficient;

      // effectively convolve our gaussian filter with the variance of any
      // merged mass points
      effectiveStandardDeviations.put(
          massAbundance,
          Math.sqrt(massAbundance.getMassVariance() + localPeakWidth * localPeakWidth));
      peakHeightReciprocals.put(
          massAbundance,
          effectiveStandardDeviations.get(massAbundance) * Math.sqrt(Math.PI * 2));

      // find largest variance
      if (massAbundance.getMassVariance() > largestVariance) {
        largestVariance = massAbundance.getMassVariance();
      }
    }

    double minimumPeakWidth = distributionData.first().getMass() / getEffectiveResolution();

    // padding of total sample space range on either side of distribution
    double padding = Math.sqrt(largestVariance) + minimumPeakWidth;
    padding *= 4;

    // sample step size
    if (sampleResolution == 0) {
      sampleResolution = minimumPeakWidth * 0.2;
    }

    // current effective sample set
    SortedSet<MassAbundance> sampleSet = new TreeSet<>();
    Iterator<MassAbundance> sampleSetIterator;
    MassAbundance sampleItem = new MassAbundance(0, 0);

    // initial sample mass
    double startMass;
    double endMass;
    if (massRange != null) {
      startMass = massRange.getLeftEndpoint();
      endMass = massRange.getRightEndpoint();
    } else {
      startMass = distributionData.first().getMass() - padding;
      endMass = distributionData.last().getMass() + padding;
    }

    MassAbundance sampleScanPosition = new MassAbundance(startMass - padding, 1);

    double sampleMass = startMass;
    boolean skippedPreviousSample = true;
    double previousSampleValue = 0;
    // sample at each step
    while (sampleMass <= endMass) {
      // find all masses no longer close enough to affect current sample
      sampleSetIterator = sampleSet.iterator();
      while (sampleSetIterator.hasNext()) {
        sampleItem = sampleSetIterator.next();
        if (sampleMass > sampleItem.getMass() + effectiveStandardDeviations.get(sampleItem) * 4) {
          sampleSetIterator.remove();
        }
      }

      // find all masses now close enough to affect current sample
      sampleSetIterator = distributionData.tailSet(sampleScanPosition).iterator();
      // sampleSetIterator.next();
      boolean done = false;
      while (sampleSetIterator.hasNext() && !done) {
        sampleItem = sampleSetIterator.next();
        if (sampleMass >= sampleItem.getMass() - effectiveStandardDeviations.get(sampleItem) * 4) {
          sampleSet.add(sampleItem);
        } else {
          sampleScanPosition = sampleItem;
          done = true;
        }
      }

      // working variables
      double sampleValue = 0;
      double powerNumerator;
      double powerDenominator;
      double sampleFromItem;
      double difference;
      // go through each item affecting this sample
      sampleSetIterator = sampleSet.iterator();
      while (sampleSetIterator.hasNext()) {
        sampleItem = sampleSetIterator.next();

        powerNumerator = sampleMass - sampleItem.getMass();
        powerNumerator *= -powerNumerator;

        powerDenominator = effectiveStandardDeviations.get(sampleItem);
        powerDenominator *= powerDenominator * 2;

        sampleFromItem = Math.pow(Math.E, powerNumerator / powerDenominator)
            / peakHeightReciprocals.get(sampleItem) * sampleItem.getAbundance();

        // if near edges then interpolate to 0 for smoother fall-off.
        difference = Math.abs(sampleMass - sampleItem.getMass());
        if (difference > effectiveStandardDeviations.get(sampleItem) * 3) {
          sampleFromItem *= 1 - (difference - effectiveStandardDeviations.get(sampleItem) * 3)
              / effectiveStandardDeviations.get(sampleItem);
        }

        sampleValue += sampleFromItem;
      }

      sampleValue /= normal;

      Interval<Double> extrema = null;
      if (extremaSpectra != null) {
        extrema = extremaSpectra.getAbundanceExtremaInRange(
            Interval.bounded(sampleMass - sampleResolution, sampleMass));
      }

      if ((extrema != null || sampleValue > 0) && skippedPreviousSample) {
        skippedPreviousSample = false;
        data.put(sampleMass - sampleResolution, 0d);
      }

      if (extrema != null) {
        if (extrema.getLeftEndpoint() < sampleValue
            && extrema.getLeftEndpoint() < previousSampleValue) {
          data.put(sampleMass - sampleResolution * 0.75, extrema.getLeftEndpoint());
        }

        if (extrema.getRightEndpoint() > sampleValue
            && extrema.getRightEndpoint() > previousSampleValue) {
          data.put(sampleMass - sampleResolution * 0.25, extrema.getRightEndpoint());
        }
      }

      if (sampleValue > 0) {
        data.put(sampleMass, sampleValue);
      } else if (!skippedPreviousSample) {
        data.put(sampleMass, 0d);
        skippedPreviousSample = true;
      }

      previousSampleValue = sampleValue;

      sampleMass += sampleResolution;
    }
    if (!skippedPreviousSample) {
      data.put(sampleMass, 0d);
    }

    if (normalise && !data.isEmpty()) {
      /*
       * here we merge the distribution by half the peak width and then find the
       * [n] most abundant masses, which should translate to about the location
       * of the [n] tallest peaks in the mass spectrum. We then sample these
       * points and take the largest to normalise by, by creating a mass
       * spectrum with a single sample at those points. Not perfect, but pretty
       * close in practice.
       */
      IsotopeDistribution significantMassDistribution = new IsotopeDistribution(
          isotopeDistribution);
      significantMassDistribution
          .mergeMassesWithinRange(getMass() / getEffectiveResolution() * 0.25, false);

      IsotopeDistribution workingDistribution = new IsotopeDistribution(
          significantMassDistribution);

      double largestAbundance = 0;
      for (int i = 0; i < 5; i++) {
        MassAbundance mostAbundantMass = workingDistribution.getLargestAbundance();
        workingDistribution.removeMassAbundance(mostAbundantMass);

        IdealMassSpectrum significantMassSpectrum = new IdealMassSpectrum();
        significantMassSpectrum.setEffectiveResolution(getEffectiveResolution());
        significantMassSpectrum.calculateForIsotopeDistribution(
            significantMassDistribution,
            null,
            bounded(mostAbundantMass.getMass(), mostAbundantMass.getMass()).withOpenEndpoints(),
            1,
            false);

        double abundance = significantMassSpectrum.getLargestAbundance();
        if (abundance > largestAbundance) {
          largestAbundance = abundance;
        }
      }

      normalise(largestAbundance);
    }
  }

  /**
   * Find the range of abundances present within this range for this spectra.
   *
   * @param massRange
   *          the range between which to check for extrema.
   * @return the range between the maxima and minima as approximated by any
   *         samples within the range given, null if there are no such samples.
   */
  protected Interval<Double> getAbundanceExtremaInRange(Interval<Double> massRange) {
    Map<Double, Double> samplesWithinRange = data.tailMap(massRange.getLeftEndpoint()).headMap(
        massRange.getRightEndpoint());

    if (samplesWithinRange.isEmpty()) {
      return null;
    }

    Iterator<Double> abundanceIterator = samplesWithinRange.values().iterator();
    Double abundance = abundanceIterator.next();
    Interval<Double> abundanceRange = Interval.bounded(abundance, abundance);
    while (abundanceIterator.hasNext()) {
      abundanceRange.getExtendedThrough(abundanceIterator.next(), false);
    }

    return abundanceRange;
  }

  public void normalise(double normalAbundance) {
    // normalise results
    Iterator<Double> dataIterator;
    Double dataSample;

    dataIterator = data.keySet().iterator();
    while (dataIterator.hasNext()) {
      dataSample = dataIterator.next();
      data.put(dataSample, data.get(dataSample) / normalAbundance);
    }
  }

  public void normalise() {
    // normalise results
    Iterator<Double> dataIterator;
    Double dataSample;

    dataIterator = data.keySet().iterator();
    while (dataIterator.hasNext()) {
      dataSample = dataIterator.next();
      data.put(dataSample, data.get(dataSample) / getLargestAbundance());
    }
  }

  public double getLargestAbundance() {
    Iterator<Double> dataIterator;
    Double dataSample;
    Double largestAbundance = 0d;

    dataIterator = data.keySet().iterator();
    while (dataIterator.hasNext()) {
      dataSample = dataIterator.next();
      if (data.get(dataSample) > largestAbundance) {
        largestAbundance = data.get(dataSample);
      }
    }

    return largestAbundance;
  }

  public TreeMap<Double, Double> getData() {
    return data;
  }

  public void setRelativeAbundance(double relativeAbundance) {
    this.relativeAbundance = relativeAbundance;
  }

  public double getRelativeAbundance() {
    return relativeAbundance;
  }

  public void setEffectiveResolution(double effectiveResolution) {
    this.effectiveResolution = effectiveResolution;
  }

  public double getEffectiveResolution() {
    return effectiveResolution;
  }

  public ChemicalComposition getMolecule() {
    return molecule;
  }

  public Interval<Double> getRange() {
    return Interval.bounded(data.firstEntry().getKey(), data.lastEntry().getKey());
  }

  public double getInterpolatedAbundance(double mass) {
    if (!getRange().contains(mass)) {
      return 0;
    }

    Map.Entry<Double, Double> below = data.floorEntry(mass);
    Map.Entry<Double, Double> above = data.ceilingEntry(mass);

    if (below == above) {
      return below.getValue();
    }

    double interpolated = ((mass - below.getKey()) * below.getValue()
        + (above.getKey() - mass) * above.getValue()) / getMassStepSize();

    return interpolated;
  }

  public double getMassStepSize() {
    return (getRange().getRightEndpoint() - getRange().getLeftEndpoint()) / data.size();
  }

  public NavigableMap<Double, Double> dataFromRange(Interval<Double> range) {
    return data.tailMap(range.getLeftEndpoint(), false).headMap(range.getRightEndpoint(), false);
  }

  public IdealMassSpectrum extractFromRange(Interval<Double> range) {
    IdealMassSpectrum massSpectrum = new IdealMassSpectrum(this);
    massSpectrum.clipToRange(range);

    return massSpectrum;
  }

  public void clipToRange(Interval<Double> range) {
    TreeMap<Double, Double> newData = new TreeMap<>(dataFromRange(range));

    newData.put(range.getLeftEndpoint(), getInterpolatedAbundance(range.getLeftEndpoint()));
    newData.put(range.getRightEndpoint(), getInterpolatedAbundance(range.getRightEndpoint()));

    data = newData;

    double highestAbundance = 0;
    Iterator<Map.Entry<Double, Double>> massAbundanceIterator = data.entrySet().iterator();
    while (massAbundanceIterator.hasNext()) {
      double abundance = massAbundanceIterator.next().getValue();
      if (abundance > highestAbundance) {
        highestAbundance = abundance;
      }
    }
    setRelativeAbundance(highestAbundance * relativeAbundance);
  }

  public double getNormalisingConstant() {
    double total = 0;

    Iterator<Double> sampleIterator = data.values().iterator();
    while (sampleIterator.hasNext()) {
      total += sampleIterator.next();
    }

    return 1 / total;
  }

  public double getMass() {
    if (molecule != null) {
      return molecule.getAverageMass();
    }

    double totalMass = 0;
    double totalAbundance = 0;
    Iterator<Map.Entry<Double, Double>> massAbundanceIterator = data.entrySet().iterator();
    Map.Entry<Double, Double> massAbundance;
    while (massAbundanceIterator.hasNext()) {
      massAbundance = massAbundanceIterator.next();
      totalMass += massAbundance.getKey() * massAbundance.getValue();
      totalAbundance += massAbundance.getValue();
    }
    return totalMass / totalAbundance;
  }

  public double getBestFitScaleAgainst(IdealMassSpectrum massSpectrum) {
    Interval<Double> intersectionRange = getRange().getIntersectionWith(massSpectrum.getRange());

    double weightedAverageScale = 0;
    if (!intersectionRange.isEmpty()) {
      double weight;
      double weightSum = 0;
      double mass;
      double abundance;
      double otherAbundance;
      if (getMassStepSize() < massSpectrum.getMassStepSize()) {
        Iterator<Double> intersectionIterator = dataFromRange(intersectionRange)
            .keySet()
            .iterator();

        while (intersectionIterator.hasNext()) {
          mass = intersectionIterator.next();
          abundance = data.get(mass);
          otherAbundance = massSpectrum.getInterpolatedAbundance(mass);
          weightSum += weight = Math.sqrt(otherAbundance * abundance);
          weightedAverageScale += (otherAbundance / abundance) * weight;
        }
      } else {
        Iterator<Double> intersectionIterator = massSpectrum
            .dataFromRange(intersectionRange)
            .keySet()
            .iterator();

        while (intersectionIterator.hasNext()) {
          mass = intersectionIterator.next();
          abundance = getInterpolatedAbundance(mass);
          otherAbundance = massSpectrum.getData().get(mass);
          weightSum += weight = Math.sqrt(otherAbundance * abundance);
          weightedAverageScale += (otherAbundance / abundance) * weight;
        }
      }
      if (weightedAverageScale == 0) {
        weightedAverageScale = 1;
      } else {
        weightedAverageScale /= weightSum;
      }
    } else {
      weightedAverageScale = 1;
    }

    return 1 / weightedAverageScale;
  }
}
