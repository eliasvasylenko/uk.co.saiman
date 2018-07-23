package uk.co.saiman.msapex.experiment.locations;

import java.util.Optional;

import org.eclipse.e4.core.contexts.IEclipseContext;

import uk.co.saiman.experiment.ResultStorage;

public interface ResultStorageProvider {
  /**
   * @return the persistent id of the storage provider
   */
  String getId();

  /**
   * @return the human-readable name of the type of location
   */
  String getName();

  /**
   * Request a result locator from the provider. Invocation of this method may
   * present UI to gather user input to inform creation of the result locator, or
   * to allow cancellation.
   * 
   * @param experimentName
   *          the initial name of the experiment to provide for
   * @return an optional containing the result locator, or an empty optional if
   *         the operation was cancelled
   */
  Optional<ResultStorage> requestLocator(IEclipseContext context, String experimentName);
}
