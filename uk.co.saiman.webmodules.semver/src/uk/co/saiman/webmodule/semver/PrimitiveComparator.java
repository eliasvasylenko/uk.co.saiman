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

import static java.util.Objects.requireNonNull;
import static org.osgi.framework.Constants.VERSION_ATTRIBUTE;
import static uk.co.saiman.webmodule.semver.Operator.EQUAL;
import static uk.co.saiman.webmodule.semver.Operator.GREATER_THAN;
import static uk.co.saiman.webmodule.semver.Operator.GREATER_THAN_OR_EQUAL;
import static uk.co.saiman.webmodule.semver.Operator.LESS_THAN;
import static uk.co.saiman.webmodule.semver.Operator.LESS_THAN_OR_EQUAL;

import java.util.stream.Stream;

import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;

public class PrimitiveComparator {
  private final Operator operator;
  private final Version version;

  public static PrimitiveComparator parse(String comparatorString) {
    requireNonNull(comparatorString);

    // we do them in this order so we don't match e.g. ">" when we see a ">=".
    Operator operator = Stream
        .of(GREATER_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL, GREATER_THAN, LESS_THAN, EQUAL)
        .filter(o -> comparatorString.startsWith(o.getSymbol()))
        .findFirst()
        .orElseThrow(
            () -> new IllegalArgumentException(
                "invalid primitive comparator \"" + comparatorString + "\": invalid format"));

    String versionString = comparatorString.substring(operator.getSymbol().length()).trim();

    PartialVersion partialVersion = new PartialVersion(versionString);

    Version version;
    if (partialVersion.getMicro().isPresent()) {
      version = Version.parse(versionString);
      return new PrimitiveComparator(operator, version);
    }

    switch (operator) {
    case GREATER_THAN_OR_EQUAL:
      return partialVersion.getLowerBound();
    case GREATER_THAN:
      return partialVersion.getLowerBoundExclusive().get();
    case LESS_THAN_OR_EQUAL:
      return partialVersion.getUpperBound().get();
    case LESS_THAN:
      return partialVersion.getUpperBoundExclusive();
    default:
      throw new IllegalArgumentException(
          "invalid version \"" + versionString + "\": invalid format");
    }
  }

  public PrimitiveComparator(Operator operator, Version version) {
    this.operator = requireNonNull(operator);
    this.version = requireNonNull(version);
  }

  public Operator getOperator() {
    return operator;
  }

  public Version getVersion() {
    return version;
  }

  public boolean matches(Version version) {
    switch (operator) {
    case EQUAL:
      return version.equals(this.version);
    case GREATER_THAN:
      return version.compareTo(this.version) > 0;
    case GREATER_THAN_OR_EQUAL:
      return version.compareTo(this.version) >= 0;
    case LESS_THAN:
      return version.compareTo(this.version) < 0;
    case LESS_THAN_OR_EQUAL:
      return version.compareTo(this.version) <= 0;
    default:
      return false;
    }
  }

  public Filter toOsgiFilter() throws InvalidSyntaxException {
    return FrameworkUtil.createFilter(toOsgiFilterString());
  }

  public String toOsgiFilterString() {
    org.osgi.framework.Version version = this.version.toOsgiVersion();

    switch (operator) {
    case EQUAL:
      return "(" + VERSION_ATTRIBUTE + "=" + version + ")";
    case GREATER_THAN:
      return "(!(" + VERSION_ATTRIBUTE + "<=" + version + "))";
    case GREATER_THAN_OR_EQUAL:
      return "(" + VERSION_ATTRIBUTE + ">=" + version + ")";
    case LESS_THAN:
      return "(!(" + VERSION_ATTRIBUTE + ">=" + version + "))";
    case LESS_THAN_OR_EQUAL:
      return "(" + VERSION_ATTRIBUTE + "<=" + version + ")";
    default:
      return "(!(" + VERSION_ATTRIBUTE + "=*))";
    }
  }
}
