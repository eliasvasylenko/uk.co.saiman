package uk.co.saiman.eclipse;

import java.net.URL;

/**
 * A collection of general utility methods for working with JavaFX and
 * e(fx)clipse.
 * 
 * @author Elias N Vasylenko
 */
public class FXUtilities {
	private static final String CONTROLLER_STRING = "Controller";

	private FXUtilities() {}

	/**
	 * Find the {@code .fxml} resource associated with a given controller class by
	 * location and naming conventions. The location of the file is assumed to be
	 * the same package as the controller class. The name of the file is assumed
	 * to be {@code [classname].fxml}, or if {@code [classname]} takes the form
	 * {@code [classnameprefix]Controller}, the name of the file is assumed to be
	 * {@code [classnameprefix].fxml}.
	 * 
	 * @param controllerClass
	 *          The controller class whose resource we wish to locate
	 * @return The URL for the resource associated with the given controller
	 *         class.
	 */
	public static URL getResource(Class<?> controllerClass) {
		String resourceName = controllerClass.getSimpleName();

		if (resourceName.endsWith(CONTROLLER_STRING)) {
			resourceName = resourceName.substring(0, resourceName.length() - CONTROLLER_STRING.length());
		}

		return getResource(controllerClass, resourceName);
	}

	/**
	 * Find the {@code .fxml} resource for a given controller class by location
	 * conventions. The location of the file is assumed to be the same package as
	 * the controller class.
	 * 
	 * @param controllerClass
	 *          The controller class whose resource we wish to locate
	 * @param resourceName
	 *          The name of the resource file
	 * @return The URL for the resource associated with the given controller
	 *         class.
	 */
	public static URL getResource(Class<?> controllerClass, String resourceName) {
		String resourceLocation = "/" + controllerClass.getPackage().getName().replace('.', '/') + "/" + resourceName
				+ ".fxml";

		return controllerClass.getResource(resourceLocation);
	}
}
