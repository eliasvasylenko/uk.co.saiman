package uk.co.saiman.eclipse.treeview;

public interface TreeDragCandidate<T> {
  default T data() {
    return entry().data();
  }

  TreeEntry<T> entry();
}
