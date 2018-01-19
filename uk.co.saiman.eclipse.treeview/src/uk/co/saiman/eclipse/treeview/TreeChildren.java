package uk.co.saiman.eclipse.treeview;

import static uk.co.saiman.reflection.token.TypeToken.forType;
import static uk.co.saiman.reflection.token.TypedReference.typedObject;

import java.util.stream.Stream;

import uk.co.saiman.reflection.token.TypeToken;
import uk.co.saiman.reflection.token.TypedReference;

public interface TreeChildren {
  void addChild(int index, TypedReference<?> child);

  default <T> void addChild(int index, T child, TypeToken<T> type) {
    addChild(index, typedObject(type, child));
  }

  default <T> void addChild(int index, T child, Class<T> type) {
    addChild(index, child, forType(type));
  }

  @SuppressWarnings("unchecked")
  default void addChild(int index, Object child) {
    addChild(index, child, (Class<Object>) child.getClass());
  }

  default <T> void addChild(TypedReference<T> child) {
    addChild((int) getChildren().count(), child);
  }

  default <T> void addChild(T child, TypeToken<T> type) {
    addChild(typedObject(type, child));
  }

  default <T> void addChild(T child, Class<T> type) {
    addChild(child, forType(type));
  }

  @SuppressWarnings("unchecked")
  default void addChild(Object child) {
    addChild(child, (Class<Object>) child.getClass());
  }

  Stream<TypedReference<?>> getChildren();

  default boolean hasChildren() {
    return getChildren().findAny().isPresent();
  }

  default TypedReference<?> getChild(int index) {
    return getChildren().skip(index).findFirst().get();
  }

  void removeChild(int index);

  boolean removeChild(Object child);
}
