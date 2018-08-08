package uk.co.saiman.eclipse.ui.model;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import uk.co.saiman.eclipse.ui.ListItems;

/**
 * Use this annotation to provide {@link ListItems list items} and
 * {@link MCell#ENTRY_DATA entry data} to a {@link MCell cell} which is being
 * redrawn so that it can contribute children and UI.
 *
 * @see org.eclipse.jface.action.IMenuListener
 * @since 1.0
 */
@Documented
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ContentChanged {}
