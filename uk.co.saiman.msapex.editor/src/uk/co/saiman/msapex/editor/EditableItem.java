package uk.co.saiman.msapex.editor;

import java.lang.reflect.Type;

public interface EditableItem {
  Object getData();

  Type getType();
}
