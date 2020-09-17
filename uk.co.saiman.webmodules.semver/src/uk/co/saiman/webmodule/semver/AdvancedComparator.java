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
package uk.co.saiman.webmodule.semver;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static uk.co.saiman.webmodule.semver.Operator.LESS_THAN;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

public interface AdvancedComparator {
  public static AdvancedComparator parse(String comparatorString) {
    requireNonNull(comparatorString);

    if (comparatorString.startsWith("~")) {
      return new TildeRange(comparatorString);

    } else if (comparatorString.startsWith("^")) {
      return new CaretRange(comparatorString);

    } else {
      String[] hyphenComponents = comparatorString.split("\\s+-\\s+", 2);
      if (hyphenComponents.length > 1) {
        return new HyphenRange(hyphenComponents[0], hyphenComponents[1]);

      } else if (comparatorString.isEmpty()
          || Character.isDigit(comparatorString.charAt(0))
          || comparatorString.charAt(0) == 'x'
          || comparatorString.charAt(0) == 'X'
          || comparatorString.charAt(0) == '*') {
        return new XRange(comparatorString);

      } else {
        return new PrimitiveRange(comparatorString);
      }
    }
  }

  public class TildeRange implements AdvancedComparator {
    private final PartialVersion partialVersion;
    private final List<PrimitiveComparator> comparators;

    private TildeRange(String comparatorString) {
      this(new PartialVersion(comparatorString.substring(1)));
    }

    public TildeRange(PartialVersion partialVersion) {
      this.partialVersion = partialVersion;

      PrimitiveComparator floor = partialVersion.getLowerBound();
      Version ceiling = (partialVersion.getMinor().isPresent())
          ? floor.getVersion().withMinorBump()
          : floor.getVersion().withMajorBump();

      this.comparators = asList(floor, new PrimitiveComparator(LESS_THAN, ceiling));
    }

    @Override
    public String toString() {
      return "~" + partialVersion.toString();
    }

    @Override
    public Stream<PrimitiveComparator> getPrimitiveComparators() {
      return comparators.stream();
    }
  }

  public class CaretRange implements AdvancedComparator {
    private final PartialVersion partialVersion;
    private final List<PrimitiveComparator> comparators;

    private CaretRange(String comparatorString) {
      this(new PartialVersion(comparatorString.substring(1)));
    }

    public CaretRange(PartialVersion partialVersion) {
      this.partialVersion = partialVersion;

      PrimitiveComparator floorBound = partialVersion.getLowerBound();
      Version floor = floorBound.getVersion();
      Version ceiling;

      if (!partialVersion.getMinor().isPresent()) {
        ceiling = floor.withMajorBump();

      } else if (!partialVersion.getMicro().isPresent()) {
        ceiling = partialVersion.getMajor().get() == 0
            ? floor.withMinorBump()
            : floor.withMajorBump();

      } else if (partialVersion.getMajor().get() != 0) {
        ceiling = floor.withMajorBump();

      } else if (partialVersion.getMinor().get() != 0) {
        ceiling = floor.withMinorBump();

      } else {
        ceiling = floor.withMicroBump();
      }

      this.comparators = asList(floorBound, new PrimitiveComparator(LESS_THAN, ceiling));
    }

    @Override
    public String toString() {
      return "^" + partialVersion.toString();
    }

    @Override
    public Stream<PrimitiveComparator> getPrimitiveComparators() {
      return comparators.stream();
    }
  }

  public class XRange implements AdvancedComparator {
    private final PartialVersion partialVersion;
    private final List<PrimitiveComparator> comparators;

    private XRange(String comparatorString) {
      this(new PartialVersion(comparatorString));
    }

    public XRange(PartialVersion partialVersion) {
      this.partialVersion = partialVersion;

      PrimitiveComparator lowerBound = partialVersion.getLowerBound();
      Optional<PrimitiveComparator> upperBound = partialVersion.getUpperBound();

      this.comparators = upperBound
          .map(b -> asList(lowerBound, b))
          .orElseGet(() -> asList(lowerBound)); // TODO
    }

    @Override
    public String toString() {
      return partialVersion.toString();
    }

    @Override
    public Stream<PrimitiveComparator> getPrimitiveComparators() {
      return comparators.stream();
    }
  }

  public class HyphenRange implements AdvancedComparator {
    private final PartialVersion lowerPartialVersion;
    private final PartialVersion upperPartialVersion;
    private final List<PrimitiveComparator> comparators;

    private HyphenRange(String lowerComparatorString, String upperComparatorString) {
      this(new PartialVersion(lowerComparatorString), new PartialVersion(upperComparatorString));
    }

    public HyphenRange(PartialVersion lowerPartialVersion, PartialVersion upperPartialVersion) {
      this.lowerPartialVersion = lowerPartialVersion;
      this.upperPartialVersion = upperPartialVersion;

      PrimitiveComparator lowerBound = lowerPartialVersion.getLowerBound();
      Optional<PrimitiveComparator> upperBound = upperPartialVersion.getUpperBound();

      this.comparators = upperBound
          .map(b -> asList(lowerBound, b))
          .orElseGet(() -> asList(lowerBound));
    }

    @Override
    public String toString() {
      return lowerPartialVersion.toString() + " - " + upperPartialVersion.toString();
    }

    @Override
    public Stream<PrimitiveComparator> getPrimitiveComparators() {
      return comparators.stream();
    }
  }

  public class PrimitiveRange implements AdvancedComparator {
    private final PrimitiveComparator comparator;

    private PrimitiveRange(String comparatorString) {
      this(PrimitiveComparator.parse(comparatorString));
    }

    public PrimitiveRange(PrimitiveComparator comparator) {
      this.comparator = comparator;
    }

    @Override
    public String toString() {
      return comparator.toString();
    }

    @Override
    public Stream<PrimitiveComparator> getPrimitiveComparators() {
      return Stream.of(comparator);
    }
  }

  Stream<PrimitiveComparator> getPrimitiveComparators();

  default boolean matches(Version version) {
    return getPrimitiveComparators().allMatch(p -> p.matches(version));
  }

  default Filter toOsgiFilter() throws InvalidSyntaxException {
    return FrameworkUtil.createFilter(toOsgiFilterString());
  }

  default String toOsgiFilterString() {
    if (getPrimitiveComparators().count() == 1) {
      return getPrimitiveComparators().findFirst().get().toOsgiFilterString();
    }

    return getPrimitiveComparators()
        .map(PrimitiveComparator::toOsgiFilterString)
        .collect(joining("", "(&", ")"));
  }
}
