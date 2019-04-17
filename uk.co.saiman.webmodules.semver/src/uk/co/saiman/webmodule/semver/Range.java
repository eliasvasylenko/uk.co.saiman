/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
import static java.util.Arrays.stream;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

public class Range {
  public static final Range UNBOUNDED = new Range(
      new ComparatorSet(
          new AdvancedComparator.PrimitiveRange(
              new PrimitiveComparator(
                  Operator.GREATER_THAN_OR_EQUAL,
                  new Version(0, 0, 0).withPreRelease("0")))));

  public static final Range EMPTY = new Range(
      new ComparatorSet(
          new AdvancedComparator.PrimitiveRange(
              new PrimitiveComparator(
                  Operator.LESS_THAN,
                  new Version(0, 0, 0).withPreRelease("0")))));

  private final List<ComparatorSet> comparatorSets;

  public static Range parse(String versionRangeString) {
    return new Range(
        stream(versionRangeString.trim().split("\\s+\\|\\|\\s+"))
            .map(ComparatorSet::parse)
            .collect(toList()));
  }

  public Range(Collection<? extends ComparatorSet> comparatorSets) {
    this.comparatorSets = unmodifiableList(new ArrayList<>(comparatorSets));
  }

  public Range(ComparatorSet... comparatorSets) {
    this(asList(comparatorSets));
  }

  public Stream<ComparatorSet> getComparatorSets() {
    return comparatorSets.stream();
  }

  public boolean matches(Version version) {
    return comparatorSets.stream().anyMatch(s -> s.matches(version));
  }

  public Filter toOsgiFilter() {
    try {
      return FrameworkUtil.createFilter(toOsgiFilterString());
    } catch (InvalidSyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  public String toOsgiFilterString() {
    if (comparatorSets.isEmpty()) {
      return "(!(version=*))";
    }

    if (comparatorSets.size() == 1) {
      return comparatorSets.get(0).toOsgiFilterString();
    }

    return comparatorSets
        .stream()
        .map(ComparatorSet::toOsgiFilterString)
        .collect(joining("", "(|", ")"));
  }

  @Override
  public String toString() {
    return comparatorSets.stream().map(Object::toString).collect(joining(" || "));
  }
}
