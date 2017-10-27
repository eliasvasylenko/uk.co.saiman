package uk.co.saiman.msapex.editor;

public interface EditorDescriptor {
  EditorProvider getProvider();

  String getIconUri();

  String getPartId();

  boolean isApplicable(Object resource);

  EditorPrototype getPrototype(Object resource);
}
