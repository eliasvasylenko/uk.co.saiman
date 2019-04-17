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

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PreReleaseVersion implements Comparable<PreReleaseVersion> {
  private final List<Identifier> identifiers;

  public PreReleaseVersion(String versionString) {
    String[] identifierStrings = versionString.split("\\.");
    identifiers = stream(identifierStrings).map(Identifier::new).collect(toList());
  }

  public PreReleaseVersion(List<? extends Identifier> identifiers) {
    this.identifiers = new ArrayList<>(identifiers);
  }

  public Stream<Identifier> getIdentifiers() {
    return identifiers.stream();
  }

  @Override
  public String toString() {
    return identifiers.stream().map(Identifier::toString).collect(joining("."));
  }

  public String toOsgiQualifier() {
    return identifiers.stream().map(Identifier::toOsgiQualifier).collect(joining("-"));
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (!(obj instanceof PreReleaseVersion))
      return false;

    PreReleaseVersion that = (PreReleaseVersion) obj;

    return this.identifiers.equals(that.identifiers);
  }

  @Override
  public int hashCode() {
    return identifiers.hashCode();
  }

  @Override
  public int compareTo(PreReleaseVersion that) {
    int thisSize = this.identifiers.size();
    int thatSize = that.identifiers.size();

    for (int i = 0; i < Math.min(thisSize, thatSize); i++) {
      int comparison = this.identifiers.get(i).compareTo(that.identifiers.get(i));
      if (comparison != 0)
        return comparison;
    }

    return this.identifiers.size() - that.identifiers.size();
  }
}
