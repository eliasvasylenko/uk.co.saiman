package uk.co.saiman.msapex.editor;

import java.util.stream.Stream;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import uk.co.saiman.msapex.editor.impl.EditorAddon;

/**
 * An editor provider for certain types of resource. Implementations may manage
 * abstract resource descriptors or operate directly on business objects.
 * <p>
 * The provider can provide a number of different editors for different types of
 * resource. Each provided editor has to a unique {@link #getEditorPartIds ID},
 * where each ID corresponds to the {@link MPart#getElementId element id} of the
 * editor part which can be added to the application model.
 * 
 * @author Elias N Vasylenko
 */
public interface EditorProvider {
  String getId();

  /**
   * Get a list of the available editor part IDs.
   * 
   * @return a list of ID strings corresponding to the {@link MPart#getElementId
   *         element IDs} of the MPart instances we can instantiate
   */
  Stream<String> getEditorPartIds();

  /**
   * Test whether an editor is applicable to a resource.
   * 
   * @param editorId
   *          the ID of the editor
   * @param resource
   *          the object to edit
   * @return true if the editor with the given part ID is applicable to the given
   *         resource
   */
  boolean isEditorApplicable(String editorId, Object resource);

  /**
   * Create a {@link MPart part} instance of the given element ID.
   * <p>
   * The part should not be created or added to the model before it is returned.
   * The {@link EditorAddon} is responsible for preparing the
   * {@link IEclipseContext context} for injection of the resource object to the
   * {@link MPart#getContributionURI part contribution}.
   * 
   * @param ID
   *          the ID of the editor part to create
   * @return a new editor part, not yet added to the application model
   */
  MPart createEditorPart(String ID);

  /**
   * Initialize an editor part for the given resource.
   * 
   * @param part
   *          the editor part which is being entered into the application model
   * @param resource
   *          the object to edit, or null if the part has been persisted and
   *          re-loaded
   * @return A reference to the editing resource. This may be adapted from the
   *         given resource or loaded from a persisted state if no resource was
   *         given.
   */
  Object initializeEditorPart(MPart part, Object resource);
}
