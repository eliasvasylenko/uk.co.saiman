package uk.co.saiman.experiment;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static uk.co.saiman.collection.StreamUtilities.reverse;
import static uk.co.saiman.collection.StreamUtilities.throwingMerger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class ExperimentPath {
  private static final String SEPARATOR = "/";
  private static final String PARENT = "..";

  private static final String PARENT_OF_ROOT = "Cannot resolve parent of root path";

  private final int ancestors;
  private final List<ExperimentMatcher> matchers;

  protected ExperimentPath(int parents, List<ExperimentMatcher> ids) {
    this.ancestors = parents;
    this.matchers = ids;
  }

  public static ExperimentPath relative() {
    return new ExperimentPath(0, Collections.emptyList());
  }

  public static ExperimentPath absolute() {
    return new ExperimentPath(-1, Collections.emptyList());
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
        .reduce(path, (e, s) -> e.match(ExperimentMatcher.fromString(s)), throwingMerger());
  }

  @Override
  public String toString() {
    return getMatchers().map(Objects::toString).collect(joining(SEPARATOR));
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

  public ExperimentPath id(String id) {
    List<ExperimentMatcher> matchers = new ArrayList<>(this.matchers.size() + 1);
    matchers.add(new ExperimentMatcher(id));
    return new ExperimentPath(ancestors, matchers);
  }

  public ExperimentPath match(ExperimentMatcher matcher) {
    List<ExperimentMatcher> matchers = new ArrayList<>(this.matchers.size() + 1);
    matchers.add(matcher);
    return new ExperimentPath(ancestors, matchers);
  }

  public ExperimentPath path(ExperimentPath path) {
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
      return resolve(node.getWorkspace());

    for (int i = 0; i < ancestors; i++) {
      node = node.getParent().orElse(null);
    }
  }

  public ExperimentNode<?, ?> resolve(Workspace workspace) {
    if (ancestors > 0)
      throw new ExperimentException(PARENT_OF_ROOT);

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
