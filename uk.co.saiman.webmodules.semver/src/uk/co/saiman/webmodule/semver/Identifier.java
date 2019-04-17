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

import java.util.Objects;
import java.util.Optional;

public class Identifier implements Comparable<Identifier> {
  private final int integer;
  private final String string;

  public Identifier(int integer) {
    this.integer = integer;
    this.string = null;
  }

  public Identifier(String string) {
    if (string.matches(".*\\s+.*"))
      throw new IllegalArgumentException(
          "Invalid identifier: \"" + string + "\" contains whitespace");

    int integer;
    try {
      integer = Integer.parseInt(string);
      string = null;
    } catch (Exception e) {
      integer = 0;
    }

    this.integer = integer;
    this.string = string;
  }

  public Optional<Integer> getInteger() {
    return string == null ? Optional.of(integer) : Optional.empty();
  }

  public Optional<String> getString() {
    return Optional.ofNullable(string);
  }

  public boolean isNumeric() {
    return string == null;
  }

  public boolean isString() {
    return string != null;
  }

  @Override
  public String toString() {
    return string == null ? Integer.toString(integer) : string;
  }

  public String toOsgiQualifier() {
    return string == null ? String.format("%010d", integer) : string;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (!(obj instanceof Identifier))
      return false;

    Identifier that = (Identifier) obj;

    return this.integer == that.integer && Objects.equals(this.string, that.string);
  }

  @Override
  public int hashCode() {
    return Objects.hash(integer, string);
  }

  @Override
  public int compareTo(Identifier that) {
    return this.isString()
        ? (that.isString() ? this.string.compareTo(that.string) : 1)
        : (that.isString() ? -1 : Integer.compare(this.integer, that.integer));
  }
}
