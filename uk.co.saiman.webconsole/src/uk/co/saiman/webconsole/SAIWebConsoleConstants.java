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
 * This file is part of uk.co.saiman.webconsole.
 *
 * uk.co.saiman.webconsole is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.webconsole is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.webconsole;

import static uk.co.saiman.webconsole.SAIWebConsoleConstants.SAI_WEB_CONSOLE_WEB_RESOURCE_NAME;
import static uk.co.saiman.webconsole.SAIWebConsoleConstants.SAI_WEB_CONSOLE_WEB_RESOURCE_VERSION;

import aQute.bnd.annotation.headers.BundleCategory;
import aQute.bnd.annotation.headers.Category;
import aQute.bnd.annotation.headers.ProvideCapability;
import osgi.enroute.namespace.WebResourceNamespace;

/**
 * Annotation to generate requirement to SAI web console utilities.
 * 
 * @author Elias N Vasylenko
 */
@SuppressWarnings("javadoc")
@BundleCategory(value = Category.bus, custom = SAI_WEB_CONSOLE_WEB_RESOURCE_VERSION)
@ProvideCapability(
		ns = WebResourceNamespace.NS,
		version = SAI_WEB_CONSOLE_WEB_RESOURCE_VERSION,
		value = ("root=static" + SAI_WEB_CONSOLE_WEB_RESOURCE_NAME) + ";"
				+ (WebResourceNamespace.NS + "=" + SAI_WEB_CONSOLE_WEB_RESOURCE_NAME))
public interface SAIWebConsoleConstants {
	final String SAI_WEB_CONSOLE_WEB_RESOURCE_NAME = "/sai/webconsole";
	final String SAI_WEB_CONSOLE_WEB_RESOURCE_VERSION = "1.0.0";
}
