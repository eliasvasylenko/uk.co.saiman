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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import aQute.bnd.annotation.headers.RequireCapability;
import osgi.enroute.namespace.WebResourceNamespace;

/**
 * A Web Resource that provides RequireJS javascript files.
 */
@RequireCapability(
    ns = WebResourceNamespace.NS,
    filter = "(&(" + WebResourceNamespace.NS + "=" + SAI_WEB_CONSOLE_WEB_RESOURCE_NAME + ")${frange;"
        + SAI_WEB_CONSOLE_WEB_RESOURCE_VERSION + "})")
@Retention(RetentionPolicy.CLASS)
public @interface RequireSAIWebConsoleWebResource {
  String[] resource() default "dist/sai-webconsole.js";

  int priority() default 300;
}
