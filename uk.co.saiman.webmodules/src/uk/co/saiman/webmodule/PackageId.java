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
 * This file is part of uk.co.saiman.webmodules.
 *
 * uk.co.saiman.webmodules is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.webmodules is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.webmodule;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.Optional;

public class PackageId {
  private final Optional<String> scope;
  private final String name;

  public PackageId(String name) {
    this(Optional.empty(), name);
  }

  public PackageId(String scope, String name) {
    this(Optional.of(scope), name);
  }

  private PackageId(Optional<String> scope, String name) {
    this.scope = scope;
    this.name = name;
  }

  public Optional<String> getScope() {
    return scope;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (!(obj instanceof PackageId))
      return false;

    PackageId that = (PackageId) obj;
    return Objects.equals(this.scope, that.scope) && Objects.equals(this.name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(scope, name);
  }

  public static PackageId parseId(String idString) {
    if (!idString.startsWith("@"))
      return new PackageId(idString);

    idString = idString.substring(1);
    String[] idStrings = idString.split("/", 2);
    return new PackageId(idStrings[0], idStrings[1]);
  }

  @Override
  public String toString() {
    return scope.map(s -> "@" + s + "/" + name).orElse(name);
  }

  public static String urlEncodeName(PackageId name) throws UnsupportedEncodingException {
    if (name.scope.isPresent()) {
      return "@" + URLEncoder.encode(name.scope.get() + "/" + name.name, "UTF-8");
    } else {
      return URLEncoder.encode(name.name, "UTF-8");
    }
  }

  public static PackageId urlDecodeName(String name) throws UnsupportedEncodingException {
    return parseId(URLDecoder.decode(name, "UTF-8"));
  }
}
