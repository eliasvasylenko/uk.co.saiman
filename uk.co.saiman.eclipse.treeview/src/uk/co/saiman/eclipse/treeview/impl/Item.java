package uk.co.saiman.eclipse.treeview.impl;

public interface Item<T> {
  T object();

  void setObject(T object);

  ItemGroup<T> group();
}
