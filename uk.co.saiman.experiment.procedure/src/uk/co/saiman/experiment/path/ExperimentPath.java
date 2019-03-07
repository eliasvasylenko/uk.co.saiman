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
 * This file is part of uk.co.saiman.experiment.
 *
 * uk.co.saiman.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.path;

import static java.lang.Math.max;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.Collections.nCopies;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.concat;
import static uk.co.saiman.collection.StreamUtilities.throwingMerger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.product.Product;
import uk.co.saiman.experiment.product.Production;

public class ExperimentPath implements Comparable<ExperimentPath> {
  public static final String SEPARATOR = "/";
  public static final String SELF = ".";
  public static final String PARENT = "..";

  private final int ancestors;
  private final List<String> ids;

  protected ExperimentPath(int parents, List<String> ids) {
    this.ancestors = parents;
    this.ids = ids;
  }

  public static ExperimentPath defineRelative() {
    return defineRelative(0);
  }

  public static ExperimentPath defineRelative(int ancestor) {
    return new ExperimentPath(ancestor, emptyList());
  }

  public static ExperimentPath defineAbsolute() {
    return new ExperimentPath(-1, emptyList());
  }

  public static ExperimentPath fromString(String string) {
    string = string.strip();

    ExperimentPath path;

    if (string.startsWith(SEPARATOR)) {
      string = string.substring(SEPARATOR.length());
      path = defineAbsolute();

    } else if (string.startsWith(SELF + SEPARATOR)) {
      string = string.substring(SELF.length() + SEPARATOR.length());
      path = defineRelative();

    } else {
      int ancestor = 0;
      while (string.startsWith(PARENT + SEPARATOR)) {
        ancestor++;
        string = string.substring(PARENT.length() + SEPARATOR.length());
      }
      path = defineRelative(ancestor);
    }

    path = stream(string.split(SEPARATOR))
        .filter(s -> !s.isEmpty())
        .reduce(path, (e, s) -> e.resolve(s), throwingMerger());

    return path;
  }

  @Override
  public String toString() {
    return (ancestors == -1 ? SEPARATOR : "")
        + concat(nCopies(max(0, ancestors), PARENT).stream(), getIds())
            .collect(joining(SEPARATOR, "", SEPARATOR));
  }

  public Optional<ExperimentPath> parent() {
    if (ids.isEmpty()) {
      if (isAbsolute())
        return Optional.empty();
      return Optional.of(new ExperimentPath(ancestors + 1, ids));
    } else {
      return Optional.of(new ExperimentPath(ancestors, ids.subList(0, ids.size() - 1)));
    }
  }

  public ExperimentPath resolve(String id) {
    List<String> matchers = new ArrayList<>(this.ids.size() + 1);
    matchers.add(id);
    return new ExperimentPath(ancestors, matchers);
  }

  public ExperimentPath resolve(Collection<? extends String> matchers) {
    List<String> concat = new ArrayList<>(this.ids.size() + matchers.size());
    concat.addAll(this.ids);
    concat.addAll(matchers);
    return new ExperimentPath(ancestors, concat);
  }

  public ExperimentPath resolve(String... matcher) {
    return resolve(asList(matcher));
  }

  public Optional<ExperimentPath> resolve(ExperimentPath path) {
    if (path.isAbsolute()) {
      return Optional.of(path);

    } else if (path.ancestors >= ids.size()) {
      if (isAbsolute()) {
        return Optional.empty();

      } else {
        return Optional
            .of(new ExperimentPath(ancestors + path.ancestors - ids.size(), emptyList()));
      }

    } else {
      List<String> matchers = new ArrayList<>(this.ids.size() - path.ancestors + path.ids.size());
      matchers.addAll(this.ids.subList(0, this.ids.size() - path.ancestors));
      matchers.addAll(path.ids);
      return Optional.of(new ExperimentPath(ancestors, matchers));
    }
  }

  public <T extends Product> Optional<ProductPath> resolve(ProductPath path) {
    return resolve(path.getExperimentPath())
        .map(experimentPath -> ProductPath.define(experimentPath, path.getProductId()));
  }

  public ProductPath resolve(Production<?> production) {
    return ProductPath.define(this, production.id());
  }

  public boolean isAbsolute() {
    return ancestors == -1;
  }

  public int getAncestorDepth() {
    return isAbsolute() ? 0 : ancestors;
  }

  public Stream<String> getIds() {
    return ids.stream();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (obj == null || obj.getClass() != getClass())
      return false;

    var that = (ExperimentPath) obj;

    return this.ancestors == that.ancestors && Objects.equals(this.ids, that.ids);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ancestors, ids);
  }

  @Override
  public int compareTo(ExperimentPath that) {
    int compareAncestors = that.ancestors - this.ancestors;
    if (compareAncestors != 0) {
      return compareAncestors;
    }

    int size = Math.min(this.ids.size(), that.ids.size());
    for (int i = 0; i < size; i++) {
      int compareId = this.ids.get(i).compareTo(that.ids.get(i));
      if (compareId != 0) {
        return compareId;
      }
    }

    return this.ids.size() - that.ids.size();
  }

  public ExperimentPath relativeTo(ExperimentPath path) {
    return path.getIds().reduce(this, ExperimentPath::relativeTo, throwingMerger());
  }

  ExperimentPath relativeTo(String id) {
    int ancestors = this.ancestors;
    List<String> ids = this.ids;

    if (ids.size() > 0 && ids.get(0).equals(id)) {
      ids = ids.subList(1, ids.size());
    } else {
      ancestors++;
    }

    return new ExperimentPath(ancestors, ids);
  }
}
