/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.babel.
 *
 * uk.co.saiman.babel is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.babel is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.babel.standalone;

import static uk.co.saiman.babel.standalone.BabelStandaloneConstants.BABEL_STANDALONE_WEB_RESOURCE_NAME;
import static uk.co.saiman.babel.standalone.BabelStandaloneConstants.BABEL_STANDALONE_WEB_RESOURCE_VERSION;

import aQute.bnd.annotation.headers.ProvideCapability;
import osgi.enroute.namespace.WebResourceNamespace;

@ProvideCapability(
    ns = WebResourceNamespace.NS,
    version = BABEL_STANDALONE_WEB_RESOURCE_VERSION,
    value = ("root=/META-INF/resources/webjars/babel-standalone/"
        + BABEL_STANDALONE_WEB_RESOURCE_VERSION) + ";"
        + (WebResourceNamespace.NS + "=" + BABEL_STANDALONE_WEB_RESOURCE_NAME))
public interface BabelStandaloneConstants {
  final String BABEL_STANDALONE_WEB_RESOURCE_NAME = "/babel/standalone";
  final String BABEL_STANDALONE_WEB_RESOURCE_VERSION = "6.24.0";
}
