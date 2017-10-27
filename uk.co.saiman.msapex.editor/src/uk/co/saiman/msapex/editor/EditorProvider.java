package uk.co.saiman.msapex.editor;

import java.util.Map;
import java.util.stream.Stream;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;

import uk.co.saiman.msapex.editor.impl.EditorAddon;

/**
 * An editor provider for certain types of resource. Implementations may manage
 * abstract resource descriptors or operate directly on business objects.
 * 
 * @author Elias N Vasylenko
 */
public interface EditorProvider {
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
   * @return true if the editor with the given part ID is applicable to the
   *         given resource
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
  MPart getEditorPart(String ID);

  /**
   * Retrieve metadata describing a resource which can be used to obtain a new
   * reference to the resource if it is closed.
   * 
   * @param resource
   *          the object to persist
   * @return a key-value metadata store
   */
  Map<String, String> persistResource(Object resource);

  /**
   * Obtain a new reference to the resource from its previously
   * {@link #persistResource retrieved metadata}.
   * 
   * @param persistentData
   *          A key-value store containing metadata describing a resource. The
   *          map may contain unrelated entries.
   * @return a reference to the data of the described resource
   */
  Object resolveResource(Map<String, String> persistentData);
}
