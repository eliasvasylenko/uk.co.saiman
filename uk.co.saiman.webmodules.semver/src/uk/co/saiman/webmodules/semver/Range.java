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

public class Range {
  private final List<ComparatorSet> comparatorSets;

  public Range(String versionRangeString) {
    versionRangeString = versionRangeString.trim();

    ArrayList<ComparatorSet> comparatorSets = new ArrayList<>();

    for (String comparatorSet : versionRangeString.split("\\s+||\\s+")) {
      comparatorSets.add(new ComparatorSet(comparatorSet));
    }
    comparatorSets.trimToSize();

    this.comparatorSets = unmodifiableList(comparatorSets);
  }

  public Range(Collection<? extends ComparatorSet> comparatorSets) {
    this.comparatorSets = unmodifiableList(new ArrayList<>(comparatorSets));
  }

  public Stream<ComparatorSet> getComparatorSets() {
    return comparatorSets.stream();
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
}
