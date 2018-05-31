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

import static org.osgi.namespace.service.ServiceNamespace.SERVICE_NAMESPACE;
import static uk.co.saiman.webconsole.SaiWebConsoleConstants.SAI_WEB_CONSOLE_WEB_MODULE_NAME;
import static uk.co.saiman.webconsole.SaiWebConsoleConstants.SAI_WEB_CONSOLE_WEB_MODULE_VERSION;
import static uk.co.saiman.webmodule.WebModuleConstants.ID_ATTRIBUTE;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.EXTENDER_VERSION;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.EXTENDER_VERSION_ATTRIBUTE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.osgi.annotation.bundle.Requirement;

import uk.co.saiman.webmodule.react.RequireReactWebModule;

/**
 * A Web Resource that provides RequireJS javascript files.
 */
@RequireReactWebModule
@Requirement(
    namespace = SERVICE_NAMESPACE,
    filter = "(" + ID_ATTRIBUTE + "=" + SAI_WEB_CONSOLE_WEB_MODULE_NAME + ")",
    version = SAI_WEB_CONSOLE_WEB_MODULE_VERSION,
    attribute = EXTENDER_VERSION_ATTRIBUTE + "=" + EXTENDER_VERSION)
@Retention(RetentionPolicy.CLASS)
public @interface RequireSAIWebConsoleWebModule {}
