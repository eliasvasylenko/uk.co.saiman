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
 * This file is part of uk.co.saiman.eclipse.treeview.
 *
 * uk.co.saiman.eclipse.treeview is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.treeview is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.treeview;

import static java.util.Collections.emptyList;
import static java.util.Objects.requireNonNull;
import static uk.co.saiman.reflection.token.TypeToken.forType;
import static uk.co.saiman.reflection.token.TypedReference.typedObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import uk.co.saiman.reflection.token.TypeToken;
import uk.co.saiman.reflection.token.TypedReference;

public class TreeEntryChild<T> {
  private final TypeToken<T> type;
  private final Supplier<? extends T> get;
  private final Consumer<? super T> set;
  private final List<TreeContribution> contributions;

  protected TreeEntryChild(
      TypeToken<T> type,
      Supplier<? extends T> get,
      Consumer<? super T> set,
      List<TreeContribution> contributions) {
    this.type = requireNonNull(type);
    this.get = requireNonNull(get);
    this.set = set;
    this.contributions = contributions == null ? emptyList() : contributions;
  }

  public T getObject() {
    return get.get();
  }

  public void setObject(T object) {
    if (set == null)
      throw new UnsupportedOperationException();
    set.accept(object);
  }

  public boolean isMutable() {
    return set != null;
  }

  public TypeToken<T> getType() {
    return type;
  }

  public TypedReference<T> getTypedObject() {
    return typedObject(getType(), getObject());
  }

  public Stream<TreeContribution> getContributions() {
    return contributions.stream();
  }

  public static <T> Builder<T> withType(TypeToken<T> type) {
    return new Builder<>(type);
  }

  public static <T> Builder<T> withType(Class<T> type) {
    return withType(forType(type));
  }

  public static <T> TreeEntryChild<T> typedChild(TypedReference<T> child) {
    return new TreeEntryChild<>(child.getTypeToken(), child::getObject, null, emptyList());
  }

  public static class Builder<T> {
    private final TypeToken<T> type;
    private final Supplier<? extends T> get;
    private final Consumer<? super T> set;
    private final List<TreeContribution> contributions;

    protected Builder(TypeToken<T> type) {
      this.type = type;
      this.get = null;
      this.set = null;
      this.contributions = null;
    }

    protected Builder(
        TypeToken<T> type,
        Supplier<? extends T> get,
        Consumer<? super T> set,
        List<TreeContribution> contributions) {
      this.type = type;
      this.get = get;
      this.set = set;
      this.contributions = contributions;
    }

    public Builder<T> withValue(T value) {
      if (this.get != null)
        throw new IllegalStateException();
      return new Builder<>(type, () -> value, set, contributions);
    }

    public Builder<T> withGetter(Supplier<? extends T> get) {
      if (this.get != null)
        throw new IllegalStateException();
      return new Builder<>(type, get, set, contributions);
    }

    public Builder<T> withSetter(Consumer<? super T> set) {
      if (this.set != null)
        throw new IllegalStateException();
      return new Builder<>(type, get, set, contributions);
    }

    public TreeEntryChild<T> build() {
      return new TreeEntryChild<>(type, get, set, contributions);
    }

    public Builder<T> withContributions(Collection<? extends TreeContribution> contributions) {
      if (this.contributions != null)
        throw new IllegalStateException();
      return new Builder<>(type, get, set, new ArrayList<>(contributions));
    }

    public Builder<T> withContributions(TreeContribution... contributions) {
      return withContributions(Arrays.asList(contributions));
    }

    public Builder<T> withContribution(TreeContribution contribution) {
      return withContributions(contribution);
    }
  }
}
