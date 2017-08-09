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
 * This file is part of uk.co.saiman.redux.
 *
 * uk.co.saiman.redux is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.redux is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.redux.thunk;

import static uk.co.saiman.redux.thunk.ReduxThunkConstants.REDUX_THUNK_WEB_RESOURCE_NAME;
import static uk.co.saiman.redux.thunk.ReduxThunkConstants.REDUX_THUNK_WEB_RESOURCE_VERSION;

import aQute.bnd.annotation.headers.ProvideCapability;
import osgi.enroute.namespace.WebResourceNamespace;

@ProvideCapability(
		ns = WebResourceNamespace.NS,
		version = REDUX_THUNK_WEB_RESOURCE_VERSION,
		value = ("root=/META-INF/resources/webjars/redux-thunk/"
				+ REDUX_THUNK_WEB_RESOURCE_VERSION) + ";"
				+ (WebResourceNamespace.NS + "=" + REDUX_THUNK_WEB_RESOURCE_NAME))
public interface ReduxThunkConstants {
	final String REDUX_THUNK_WEB_RESOURCE_NAME = "/redux-thunk/redux-thunk";
	final String REDUX_THUNK_WEB_RESOURCE_VERSION = "2.1.0";
}
