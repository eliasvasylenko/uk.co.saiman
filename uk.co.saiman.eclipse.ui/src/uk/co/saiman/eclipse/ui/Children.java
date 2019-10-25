package uk.co.saiman.eclipse.ui;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.stream.Stream;

import uk.co.saiman.eclipse.model.ui.MCell;
import uk.co.saiman.eclipse.model.ui.MTree;
import uk.co.saiman.eclipse.utilities.ContextBuffer;

/**
 * Annotate methods in contribution objects for {@link MCell cells} and
 * {@link MTree trees} in order to generate lists of children.
 * <p>
 * The children are created by cloning the snippet of the given id.
 * <p>
 * The contexts of the children are filled according to the data returned by the
 * method. The method should return a {@link Stream stream} or {@link Collection
 * collection} of {@link ContextBuffer context buffers}. Each element of the
 * stream corresponds to a child to be added to the model.
 * 
 * @author Elias N Vasylenko
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Children {
  String CHILD_INDEX = "uk.co.saiman.eclipse.ui.children.index";

  String snippetId();
}
