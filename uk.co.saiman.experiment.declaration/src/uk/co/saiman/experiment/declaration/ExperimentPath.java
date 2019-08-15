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
 * This file is part of uk.co.saiman.experiment.graph.
 *
 * uk.co.saiman.experiment.graph is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.graph is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.declaration;

import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static uk.co.saiman.collection.StreamUtilities.throwingMerger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A path within an experiment. The root path represents the complete procedure,
 * and descendant paths point to instructions.
 * 
 * @author Elias N Vasylenko
 *
 */
public abstract class ExperimentPath<T extends ExperimentPath<T>>
    implements Comparable<ExperimentPath<?>> {
  public static final String SEPARATOR = "/";
  public static final String SELF = ".";
  public static final String PARENT = "..";

  private final List<ExperimentId> ids;

  protected ExperimentPath(List<ExperimentId> ids) {
    this.ids = ids;
  }

  public static ExperimentPath<Relative> toSelf() {
    return toAncestor(0);
  }

  public static ExperimentPath<Relative> toAncestor(int ancestor) {
    return new Relative(ancestor, emptyList());
  }

  public static ExperimentPath<Absolute> toRoot() {
    return new Absolute(emptyList());
  }

  public static ExperimentPath<?> fromString(String string) {
    string = string.strip();

    ExperimentPath<?> path;

    if (string.startsWith(SEPARATOR)) {
      string = string.substring(SEPARATOR.length());
      path = toRoot();

    } else if (string.startsWith(SELF + SEPARATOR)) {
      string = string.substring(SELF.length() + SEPARATOR.length());
      path = toSelf();

    } else {
      int ancestor = 0;
      while (string.startsWith(PARENT + SEPARATOR)) {
        ancestor++;
        string = string.substring(PARENT.length() + SEPARATOR.length());
      }
      path = toAncestor(ancestor);
    }

    path = stream(string.split(SEPARATOR))
        .filter(s -> !s.isEmpty())
        .map(ExperimentId::fromName)
        .reduce(path, (e, s) -> e.resolve(s), throwingMerger());

    return path;
  }

  public static ExperimentPath<Relative> relativeFromString(String string) {
    var path = ExperimentPath.fromString(string);
    if (path instanceof Relative) {
      return (Relative) path;
    } else {
      throw new IllegalArgumentException();
    }
  }

  public static ExperimentPath<Absolute> absoluteFromString(String string) {
    var path = fromString(string);
    if (path instanceof Absolute) {
      return (Absolute) path;
    } else {
      throw new IllegalArgumentException();
    }
  }

  @Override
  public String toString() {
    return ids().map(id -> id + SEPARATOR).collect(joining());
  }

  public abstract Optional<ExperimentPath<T>> parent();

  public abstract boolean isAbsolute();

  public abstract int ancestorDepth();

  List<ExperimentId> getIds() {
    return ids;
  }

  public Stream<ExperimentId> ids() {
    return ids.stream();
  }

  public ExperimentId id(int index) {
    return ids.get(index);
  }

  public int depth() {
    return ids.size() - ancestorDepth();
  }

  public boolean isEmpty() {
    return ids.isEmpty() && ancestorDepth() == 0;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (obj == null || obj.getClass() != getClass())
      return false;

    var that = (ExperimentPath<?>) obj;

    return this.ancestorDepth() == that.ancestorDepth() && Objects.equals(this.ids, that.ids);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ancestorDepth(), ids);
  }

  @Override
  public int compareTo(ExperimentPath<?> that) {
    if (this.isAbsolute() != that.isAbsolute()) {
      return this.isAbsolute() ? 1 : -1;
    }

    int compareAncestors = that.ancestorDepth() - this.ancestorDepth();
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

  abstract ExperimentPath<T> withIds(List<ExperimentId> ids);

  public ExperimentPath<T> resolve(Collection<? extends ExperimentId> ids) {
    List<ExperimentId> concat = new ArrayList<>(this.ids.size() + ids.size());
    concat.addAll(this.ids);
    concat.addAll(ids);
    return withIds(concat);
  }

  public ExperimentPath<T> resolve(String... idStrings) {
    return resolve(Stream.of(idStrings).map(ExperimentId::fromName).collect(toList()));
  }

  public ExperimentPath<T> resolve(ExperimentId... ids) {
    return resolve(Arrays.asList(ids));
  }

  public abstract Optional<ExperimentPath<T>> resolve(ExperimentPath<Relative> path);

  public abstract Optional<ExperimentPath<Absolute>> resolveAgainst(ExperimentPath<Absolute> path);

  public ExperimentPath<Absolute> toAbsolute() {
    return resolveAgainst(toRoot()).get();
  }

  public ExperimentPath<Relative> relativeTo(ExperimentPath<?> path) {
    return path
        .ids()
        .reduce(toAncestor(0).resolve(ids), ExperimentPath::relativeTo, throwingMerger());
  }

  public ExperimentPath<Relative> relativeTo(ExperimentId id) {
    int ancestors = ancestorDepth();
    List<ExperimentId> ids = this.ids;

    if (ids.size() > 0 && ids.get(0).equals(id)) {
      ids = ids.subList(1, ids.size());
    } else {
      ancestors++;
    }

    return new Relative(ancestors, ids);
  }

  public abstract Iterator<? extends ExperimentRelation> iterator();

  /**
   * An absolute path.
   * 
   * @author Elias N Vasylenko
   */
  public static class Absolute extends ExperimentPath<Absolute> {
    Absolute(List<ExperimentId> ids) {
      super(ids);
    }

    @Override
    public String toString() {
      return SEPARATOR + super.toString();
    }

    @Override
    public Optional<ExperimentPath<Absolute>> parent() {
      if (isEmpty()) {
        return Optional.empty();
      } else {
        return Optional.of(new Absolute(getIds().subList(0, getIds().size() - 1)));
      }
    }

    @Override
    public boolean isAbsolute() {
      return true;
    }

    @Override
    public int ancestorDepth() {
      return 0;
    }

    @Override
    public ExperimentPath<Absolute> withIds(List<ExperimentId> ids) {
      return new Absolute(ids);
    }

    @Override
    public Optional<ExperimentPath<Absolute>> resolve(ExperimentPath<Relative> path) {
      if (path.ancestorDepth() == getIds().size()) {
        return Optional.of(withIds(path.getIds()));

      } else if (path.ancestorDepth() > getIds().size()) {
        return Optional.empty();

      } else {
        return Optional
            .of(
                new Absolute(getIds().subList(0, getIds().size() - path.ancestorDepth()))
                    .resolve(path.getIds()));
      }
    }

    @Override
    public Optional<ExperimentPath<Absolute>> resolveAgainst(ExperimentPath<Absolute> path) {
      return Optional.of(this);
    }

    @Override
    public Iterator<? extends ExperimentRelation> iterator() {
      return ids().map(ExperimentRelation.Dependent::new).collect(toList()).iterator();
    }
  }

  /**
   * A relative path.
   * 
   * @author Elias N Vasylenko
   */
  public static class Relative extends ExperimentPath<Relative> {
    private final int ancestors;

    Relative(int ancestors, List<ExperimentId> ids) {
      super(ids);
      this.ancestors = ancestors;
    }

    @Override
    public String toString() {
      return isEmpty()
          ? SELF + SEPARATOR
          : Stream.generate(() -> PARENT + SEPARATOR).limit(ancestors).collect(joining())
              + super.toString();
    }

    @Override
    public Optional<ExperimentPath<Relative>> parent() {
      if (depth() == 0) {
        return Optional.of(new Relative(ancestors + 1, getIds()));
      } else {
        return Optional.of(new Relative(ancestors, getIds().subList(0, getIds().size() - 1)));
      }
    }

    @Override
    public boolean isAbsolute() {
      return false;
    }

    @Override
    public int ancestorDepth() {
      return ancestors;
    }

    @Override
    public ExperimentPath<Relative> withIds(List<ExperimentId> ids) {
      return new Relative(ancestors, ids);
    }

    @Override
    public Optional<ExperimentPath<Relative>> resolve(ExperimentPath<Relative> path) {
      if (path.ancestorDepth() == getIds().size()) {
        return Optional.of(withIds(path.getIds()));

      } else if (path.ancestorDepth() > getIds().size()) {
        return Optional
            .of(
                new Relative(
                    path.ancestorDepth() + ancestorDepth() - getIds().size(),
                    emptyList()));

      } else {
        return Optional
            .of(
                new Relative(
                    ancestorDepth(),
                    getIds().subList(0, getIds().size() - path.ancestorDepth()))
                        .resolve(path.getIds()));
      }
    }

    @Override
    public Optional<ExperimentPath<Absolute>> resolveAgainst(ExperimentPath<Absolute> path) {
      return path.resolve(this);
    }

    @Override
    public Iterator<ExperimentRelation> iterator() {
      return Stream
          .concat(
              IntStream.range(0, ancestors).mapToObj(i -> new ExperimentRelation.Dependency()),
              ids().map(ExperimentRelation.Dependent::new))
          .collect(toList())
          .iterator();
    }
  }
}
