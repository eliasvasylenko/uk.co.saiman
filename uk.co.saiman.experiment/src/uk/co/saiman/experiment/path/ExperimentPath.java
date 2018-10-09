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
import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.concat;
import static uk.co.saiman.collection.StreamUtilities.reverse;
import static uk.co.saiman.collection.StreamUtilities.throwingMerger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.experiment.ExperimentException;
import uk.co.saiman.experiment.ExperimentNode;
import uk.co.saiman.experiment.Workspace;

public class ExperimentPath {
  private static final String SEPARATOR = "/";
  private static final String PARENT = "..";

  private static final String PARENT_OF_ROOT = "Cannot resolve parent of root path";
  private static final String CANNOT_RESOLVE = "Cannot resolve path";
  private static final String ROOT_NOT_EXPERIMENT = "Root path does not resolve an experiment";
  private static final String PARENT_OF_EXPERIMENT = "Cannot resolve parent of experiment";

  private final int ancestors;
  private final List<ExperimentMatcher> matchers;

  protected ExperimentPath(int parents, List<ExperimentMatcher> ids) {
    this.ancestors = parents;
    this.matchers = ids;
  }

  public static ExperimentPath relative() {
    return new ExperimentPath(0, emptyList());
  }

  public static ExperimentPath absolute() {
    return new ExperimentPath(-1, emptyList());
  }

  public static ExperimentPath of(ExperimentNode<?, ?> node) {
    return new ExperimentPath(
        -1,
        reverse(node.getAncestors()).map(ExperimentMatcher::matching).collect(toList()));
  }

  public static ExperimentPath fromString(String string) {
    ExperimentPath path;

    if (string.startsWith(SEPARATOR)) {
      string = string.substring(SEPARATOR.length());
      path = absolute();
    } else {
      path = relative();
    }

    while (string.startsWith(PARENT + SEPARATOR)) {
      path = path.parent();
      string = string.substring(PARENT.length() + SEPARATOR.length());
    }

    return stream(string.split(SEPARATOR))
        .reduce(path, (e, s) -> e.resolve(ExperimentMatcher.fromString(s)), throwingMerger());
  }

  @Override
  public String toString() {
    return (ancestors == -1 ? "/" : "")
        + concat(nCopies(max(0, ancestors), "..").stream(), getMatchers().map(Objects::toString))
            .collect(joining(SEPARATOR));
  }

  public ExperimentPath parent() {
    if (matchers.isEmpty()) {
      if (isAbsolute())
        throw new ExperimentException("Cannot resolve parent of root path");
      return new ExperimentPath(ancestors + 1, matchers);
    } else {
      return new ExperimentPath(ancestors, matchers.subList(0, matchers.size() - 1));
    }
  }

  public ExperimentPath resolve(String id) {
    List<ExperimentMatcher> matchers = new ArrayList<>(this.matchers.size() + 1);
    matchers.add(new ExperimentMatcher(id));
    return new ExperimentPath(ancestors, matchers);
  }

  public ExperimentPath resolve(Collection<? extends ExperimentMatcher> matchers) {
    List<ExperimentMatcher> concat = new ArrayList<>(this.matchers.size() + matchers.size());
    concat.addAll(this.matchers);
    concat.addAll(matchers);
    return new ExperimentPath(ancestors, concat);
  }

  public ExperimentPath resolve(ExperimentMatcher... matcher) {
    return resolve(asList(matcher));
  }

  public ExperimentPath resolve(ExperimentPath path) {
    if (path.isAbsolute()) {
      return path;

    } else if (path.ancestors >= matchers.size()) {
      if (isAbsolute())
        throw new ExperimentException(PARENT_OF_ROOT);
      return new ExperimentPath(ancestors + path.ancestors - matchers.size(), emptyList());

    } else {
      List<ExperimentMatcher> matchers = new ArrayList<>(
          this.matchers.size() - path.ancestors + path.matchers.size());
      matchers.addAll(this.matchers.subList(0, this.matchers.size() - path.ancestors));
      matchers.addAll(path.matchers);
      return new ExperimentPath(ancestors, matchers);
    }
  }

  public ExperimentNode<?, ?> resolve(ExperimentNode<?, ?> node) {
    if (isAbsolute())
      return resolve(
          node
              .getWorkspace()
              .orElseThrow(
                  () -> new ExperimentException("Cannot resolve absolute path for detached node")));

    Optional<ExperimentNode<?, ?>> result = Optional.of(node);

    for (int i = 0; i < ancestors; i++) {
      result = result.flatMap(ExperimentNode::getParent);
    }
    if (!result.isPresent()) {
      throw new ExperimentException(PARENT_OF_EXPERIMENT);
    }

    for (ExperimentMatcher matcher : matchers) {
      result = result.flatMap(r -> r.getChildren().filter(matcher::match).findFirst());
    }

    return result.orElseThrow(() -> new ExperimentException(CANNOT_RESOLVE + " " + this));
  }

  public ExperimentNode<?, ?> resolve(Workspace workspace) {
    if (ancestors > 0)
      throw new ExperimentException(PARENT_OF_ROOT);
    if (matchers.isEmpty())
      throw new ExperimentException(ROOT_NOT_EXPERIMENT);

    Optional<? extends ExperimentNode<?, ?>> result = workspace
        .getExperiments()
        .filter(matchers.get(0)::match)
        .findFirst();

    for (int i = 1; i < matchers.size(); i++) {
      ExperimentMatcher matcher = matchers.get(i);
      result = result.flatMap(r -> r.getChildren().filter(matcher::match).findFirst());
    }

    return result.orElseThrow(() -> new ExperimentException(CANNOT_RESOLVE + " " + this));
  }

  public boolean isAbsolute() {
    return ancestors == -1;
  }

  public int getAncestorDepth() {
    return isAbsolute() ? 0 : ancestors;
  }

  public Stream<ExperimentMatcher> getMatchers() {
    return matchers.stream();
  }
}
