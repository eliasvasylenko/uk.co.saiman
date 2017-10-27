package uk.co.saiman.msapex.editor;

import java.util.stream.Stream;

public interface EditorService {
  void registerProvider(EditorProvider provider);

  void unregisterProvider(EditorProvider provider);

  /**
   * Get the available editors for a resource in order of precedence.
   * <p>
   * The strategy for determining the precedence is left to the implementer, but
   * generally it may be in order of the editors most recently
   * {@link EditorPrototype#showPart shown}.
   * 
   * @param resource
   *          the resource data object to edit
   * @return The current editors applicable to the given resource in order of
   *         precedence.
   */
  Stream<EditorPrototype> getApplicableEditors(Object resource);

  Stream<EditorDescriptor> getEditors();
}
