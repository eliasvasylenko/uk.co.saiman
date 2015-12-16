package uk.co.saiman.msapex.data;

import javafx.scene.Node;
import uk.co.strangeskies.reflection.TypeToken;

public interface AnnotationHandler<T> {
	TypeToken<T> getDataType();

	Node handle(T annotationData);
}
