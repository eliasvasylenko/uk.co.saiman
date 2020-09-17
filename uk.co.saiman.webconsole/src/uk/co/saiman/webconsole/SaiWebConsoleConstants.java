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

import static uk.co.saiman.webmodule.WebModuleConstants.DEFAULT_ENTRY_POINT;
import static uk.co.saiman.webmodule.WebModuleConstants.ESM;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.DEFAULT_RESOURCE_ROOT;

import uk.co.saiman.webmodule.ProvideWebModule;
import uk.co.saiman.webmodule.lighterhtml.RequireLighterhtmlWebModule;

/**
 * Annotation to generate requirement to SAI web console utilities.
 * 
 * @author Elias N Vasylenko
 */
@RequireLighterhtmlWebModule
@SuppressWarnings("javadoc")
@ProvideWebModule(id = SaiWebConsoleConstants.SAI_WEB_CONSOLE_WEB_MODULE_NAME, version = SaiWebConsoleConstants.SAI_WEB_CONSOLE_WEB_MODULE_VERSION, resourceRoot = DEFAULT_RESOURCE_ROOT, entryPoint = DEFAULT_ENTRY_POINT, format = ESM)
public interface SaiWebConsoleConstants {
  final String SAI_WEB_CONSOLE_WEB_MODULE_NAME = "@saiman/webconsole";
  final String SAI_WEB_CONSOLE_WEB_MODULE_VERSION = "1.0.0";
}
