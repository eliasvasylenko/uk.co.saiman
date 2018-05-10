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

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

public class ComparatorSet {
  private final List<AdvancedComparator> comparators;

  public ComparatorSet(String comparatorSetString) {
    comparatorSetString = comparatorSetString.trim();

    ArrayList<AdvancedComparator> comparators = new ArrayList<>();

    for (String comparator : comparatorSetString.split("(?!\\s+-\\s+)\\s+")) {
      comparators.add(new AdvancedComparator(comparator));
    }
    comparators.trimToSize();

    this.comparators = unmodifiableList(comparators);
  }

  public ComparatorSet(Collection<? extends AdvancedComparator> comparators) {
    this.comparators = unmodifiableList(new ArrayList<>(comparators));
  }

  public Stream<AdvancedComparator> getAdvancedComparators() {
    return comparators.stream();
  }

  public Filter toOsgiFilter() throws InvalidSyntaxException {
    return FrameworkUtil.createFilter(toOsgiFilterString());
  }

  public String toOsgiFilterString() {
    if (comparators.isEmpty()) {
      return "(version=*)";
    }

    if (comparators.size() == 1) {
      return comparators.get(0).toOsgiFilterString();
    }

    return comparators
        .stream()
        .map(AdvancedComparator::toOsgiFilterString)
        .collect(joining("", "(&", ")"));
  }
}
