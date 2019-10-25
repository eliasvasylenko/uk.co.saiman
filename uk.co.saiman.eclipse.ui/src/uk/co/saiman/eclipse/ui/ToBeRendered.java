package uk.co.saiman.eclipse.ui;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.eclipse.e4.ui.model.application.MContribution;
import org.eclipse.e4.ui.model.application.ui.MContext;
import org.eclipse.e4.ui.model.application.ui.MUIElement;

/**
 * This annotation efficiently facilitates conditionally-present model elements.
 * <p>
 * It is intended for the annotation of static methods of {@link MContribution
 * contribution objects} of {@link MUIElement UI elements} with {@link MContext
 * contexts}. Annotated methods are evaluated at context creation, but before
 * contribution injection. If the annotated method returns false, the
 * contribution is set to not render, which short-circuits further processing of
 * the model element before removing it from the application model.
 * 
 * @author Elias N Vasylenko
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ToBeRendered {}
