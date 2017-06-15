/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.facebook.react.webresource.
 *
 * uk.co.saiman.facebook.react.webresource is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.facebook.react.webresource is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.babel.core;

import static uk.co.saiman.babel.core.BabelCoreConstants.BABEL_CORE_WEB_RESOURCE_NAME;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import aQute.bnd.annotation.headers.RequireCapability;
import osgi.enroute.namespace.WebResourceNamespace;

/**
 * A Web Resource that provides Babel Core javascript files.
 */
@RequireCapability(
		ns = WebResourceNamespace.NS,
		filter = "(&(" + WebResourceNamespace.NS + "=" + BABEL_CORE_WEB_RESOURCE_NAME + ")${frange;"
				+ BabelCoreConstants.BABEL_CORE_WEB_RESOURCE_VERSION + "})")
@Retention(RetentionPolicy.CLASS)
public @interface RequireBabelCoreWebResource {
	String[] resource() default {};

	int priority() default 1000;
}
