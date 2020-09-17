/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.osgi.
 *
 * uk.co.saiman.osgi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.osgi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.osgi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.osgi.annotation.bundle.Capability;
import org.osgi.namespace.extender.ExtenderNamespace;

/**
 * Provide an OSGi extender capability of the given name and version.
 * <p>
 * Typically implementations should be derived from the abstract
 * {@link ExtenderManager} helper class.
 * 
 * @author Elias N Vasylenko
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
@Capability(namespace = ExtenderNamespace.EXTENDER_NAMESPACE)
public @interface ProvideExtender {
	/**
	 * @return The name of the extender provision
	 */
	String name();

	/**
	 * Typically the version of an extender may be fetched from a bnd version
	 * macro, as e.g. {@code "$&#123;my-extender-version&#125;"}.
	 * 
	 * @return The version of the extender provision
	 */
	String version();
}
