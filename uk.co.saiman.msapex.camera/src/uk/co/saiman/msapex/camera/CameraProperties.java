package uk.co.saiman.msapex.camera;

import uk.co.strangeskies.text.properties.Localized;
import uk.co.strangeskies.text.properties.Properties;

public interface CameraProperties extends Properties<CameraProperties> {
	Localized<String> cameraDevice();

	Localized<String> noCameraDevices();
}
