/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.webmodules.semver.
 *
 * uk.co.saiman.webmodules.semver is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.webmodules.semver is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.webmodules.semver;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static uk.co.saiman.webmodules.semver.Operator.LESS_THAN;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

public class AdvancedComparator {
  private final List<PrimitiveComparator> comparators;

  public AdvancedComparator(String comparatorString) {
    requireNonNull(comparatorString);

    if (comparatorString.startsWith("~")) {
      comparators = parseTildeRange(comparatorString);

    } else if (comparatorString.startsWith("^")) {
      comparators = parseCaretRange(comparatorString);

    } else {
      String[] hyphenComponents = comparatorString.split("\\s+-\\s+", 2);
      if (hyphenComponents.length > 1) {
        comparators = parseHyphenRange(hyphenComponents[0], hyphenComponents[1]);

      } else if (Character.isDigit(comparatorString.charAt(0))) {
        comparators = parseXRange(comparatorString);

      } else {
        comparators = singletonList(new PrimitiveComparator(comparatorString));
      }
    }
  }

  private List<PrimitiveComparator> parseXRange(String comparatorString) {
    PartialVersion version = new PartialVersion(comparatorString);
    PrimitiveComparator lowerBound = version.getLowerBound();
    Optional<PrimitiveComparator> upperBound = version.getUpperBound();

    return upperBound.map(b -> asList(lowerBound, b)).orElseGet(() -> asList(lowerBound));
  }

  private List<PrimitiveComparator> parseHyphenRange(
      String leftVersionString,
      String rightVersionString) {
    PartialVersion leftVersion = new PartialVersion(leftVersionString);
    PartialVersion rightVersion = new PartialVersion(rightVersionString);
    PrimitiveComparator lowerBound = leftVersion.getLowerBound();
    Optional<PrimitiveComparator> upperBound = rightVersion.getUpperBound();

    return upperBound.map(b -> asList(lowerBound, b)).orElseGet(() -> asList(lowerBound));
  }

  private static List<PrimitiveComparator> parseTildeRange(String comparatorString) {
    comparatorString.substring(1);
    PartialVersion partial = new PartialVersion(comparatorString);
    PrimitiveComparator floor = partial.getLowerBound();
    Version ceiling = (partial.getMinor().isPresent())
        ? floor.getVersion().withMinorBump()
        : floor.getVersion().withMajorBump();

    return asList(floor, new PrimitiveComparator(LESS_THAN, ceiling));
  }

  private static List<PrimitiveComparator> parseCaretRange(String comparatorString) {
    comparatorString.substring(1);
    PartialVersion partial = new PartialVersion(comparatorString);

    PrimitiveComparator floorBound = partial.getLowerBound();
    Version floor = floorBound.getVersion();
    Version ceiling;

    if (!partial.getMinor().isPresent()) {
      ceiling = floor.withMajorBump();

    } else if (!partial.getMicro().isPresent()) {
      ceiling = partial.getMajor().get() == 0 ? floor.withMinorBump() : floor.withMajorBump();

    } else if (partial.getMajor().get() != 0) {
      ceiling = floor.withMajorBump();

    } else if (partial.getMinor().get() != 0) {
      ceiling = floor.withMinorBump();

    } else {
      ceiling = floor.withMicroBump();
    }

    return asList(floorBound, new PrimitiveComparator(LESS_THAN, ceiling));
  }

  public AdvancedComparator(Collection<? extends PrimitiveComparator> comparators) {
    requireNonNull(comparators);

    if (comparators.isEmpty())
      new IllegalArgumentException("invalid comparator \"" + comparators + "\": empty");

    this.comparators = unmodifiableList(new ArrayList<>(comparators));
  }

  public Stream<PrimitiveComparator> getPrimitiveComparators() {
    return comparators.stream();
  }

  public Filter toOsgiFilter() throws InvalidSyntaxException {
    return FrameworkUtil.createFilter(toOsgiFilterString());
  }

  public String toOsgiFilterString() {
    if (comparators.size() == 1) {
      return comparators.get(0).toOsgiFilterString();
    }

    return comparators
        .stream()
        .map(PrimitiveComparator::toOsgiFilterString)
        .collect(joining("", "(&", ")"));
  }
}
