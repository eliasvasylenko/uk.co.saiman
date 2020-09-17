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
 * This file is part of uk.co.saiman.data.
 *
 * uk.co.saiman.data is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.data is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.data.format;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static uk.co.saiman.data.format.RegistrationTree.STANDARDS;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class MediaType implements Serializable {
  private static final long serialVersionUID = 1L;

  public static final String TEXT_TYPE = "text";
  public static final String APPLICATION_TYPE = "application";

  public static final MediaType PLAIN_TEXT = new MediaType(TEXT_TYPE, "plain");
  public static final MediaType HTML = new MediaType(TEXT_TYPE, "html");
  public static final MediaType RTF = new MediaType(TEXT_TYPE, "rtf");
  public static final MediaType URL = new MediaType(TEXT_TYPE, "uri-list");

  private final String type;
  private final String subtype;
  private final RegistrationTree registrationTree;
  private final String suffix;
  private final List<String> parameters;

  public MediaType(String type, String subtype) {
    this.type = type;
    this.subtype = subtype;
    this.registrationTree = RegistrationTree.STANDARDS;
    this.suffix = null;
    this.parameters = Collections.emptyList();
  }

  public MediaType(String type, String subtype, RegistrationTree registrationTree) {
    this.type = type;
    this.subtype = subtype;
    this.registrationTree = registrationTree;
    this.suffix = null;
    this.parameters = Collections.emptyList();
  }

  private MediaType(
      String type,
      String subtype,
      RegistrationTree registrationTree,
      String suffix,
      List<String> parameters) {
    this.type = type;
    this.subtype = subtype;
    this.registrationTree = registrationTree;
    this.suffix = suffix;
    this.parameters = parameters;
  }

  public static MediaType valueOf(String string) {
    // work in from left

    int slash = string.indexOf('/');
    String type = string.substring(0, slash);
    string = string.substring(slash + 1);

    RegistrationTree registrationTree = STANDARDS;
    for (int i = 1; i < RegistrationTree.values().length; i++) {
      RegistrationTree option = RegistrationTree.values()[i];
      if (string.startsWith(option.prefix().get() + ".")) {
        registrationTree = option;
        string = string.substring(option.prefix().get().length() + 1);
        break;
      }
    }

    // work in from right

    int semicolon;
    ArrayList<String> parameters = new ArrayList<>();
    while ((semicolon = string.lastIndexOf(';')) >= 0) {
      String parameter = string.substring(semicolon + 1);
      string = string.substring(0, semicolon);
      parameters.add(0, parameter);
    }

    int plus = string.indexOf('+');
    String suffix;
    if (plus >= 0) {
      suffix = string.substring(plus + 1);
      string = string.substring(0, plus);
    } else {
      suffix = null;
    }

    String subtype = string;

    // TODO Auto-generated method stub
    return new MediaType(type, subtype, registrationTree, suffix, parameters);
  }

  @Override
  public String toString() {
    return type()
        + "/"
        + registrationTree().prefix().map(p -> p + ".").orElse("")
        + subtype()
        + suffix().map(s -> "+" + s).orElse("")
        + parameters().map(s -> ";" + s).collect(joining());
  }

  public String type() {
    return type;
  }

  public String subtype() {
    return subtype;
  }

  public RegistrationTree registrationTree() {
    return registrationTree;
  }

  public Optional<String> suffix() {
    return Optional.ofNullable(suffix);
  }

  public MediaType withSuffix(String suffix) {
    return new MediaType(type, subtype, registrationTree, suffix, parameters);
  }

  public MediaType withoutSuffix() {
    return new MediaType(type, subtype, registrationTree, null, parameters);
  }

  public Stream<String> parameters() {
    return parameters.stream();
  }

  public MediaType withParameters(String... parameters) {
    return new MediaType(
        type,
        subtype,
        registrationTree,
        suffix,
        new ArrayList<>(asList(parameters)));
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof MediaType)) {
      return false;
    }
    MediaType that = (MediaType) obj;
    return Objects.equals(this.type, that.type)
        && Objects.equals(this.subtype, that.subtype)
        && Objects.equals(this.registrationTree, that.registrationTree)
        && Objects.equals(this.suffix, that.suffix)
        && Objects.equals(this.parameters, that.parameters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, subtype, registrationTree, suffix, parameters);
  }
}
