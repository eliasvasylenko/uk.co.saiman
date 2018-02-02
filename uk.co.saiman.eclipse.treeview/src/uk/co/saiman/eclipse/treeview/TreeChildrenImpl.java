package uk.co.saiman.eclipse.treeview;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import uk.co.saiman.reflection.token.TypedReference;

final class TreeChildrenImpl implements TreeChildren {
  private final List<TypedReference<?>> children = new ArrayList<>();

  @Override
  public void addChild(int index, TypedReference<?> child) {
    children.add(index, child);
  }

  @Override
  public Stream<TypedReference<?>> getChildren() {
    return children.stream();
  }

  @Override
  public void removeChild(int index) {
    children.remove(index);
  }

  @Override
  public boolean removeChild(Object child) {
    return children.remove(child);
  }
}