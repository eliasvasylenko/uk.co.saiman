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
 * This file is part of uk.co.saiman.comms.copley.webconsole.
 *
 * uk.co.saiman.comms.copley.webconsole is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms.copley.webconsole is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.copley.webmodule;

import static uk.co.saiman.comms.copley.webmodule.CopleyWebConsoleConstants.COPLEY_WEB_MODULE_NAME;
import static uk.co.saiman.comms.copley.webmodule.CopleyWebConsoleConstants.COPLEY_WEB_MODULE_VERSION;
import static uk.co.saiman.webmodule.WebModuleConstants.DEFAULT_ENTRY_POINT;
import static uk.co.saiman.webmodule.WebModuleConstants.ESM;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.DEFAULT_RESOURCE_ROOT;

import uk.co.saiman.comms.webmodule.RequireSaiCommsWebModule;
import uk.co.saiman.webconsole.RequireSaiWebConsoleWebModule;
import uk.co.saiman.webmodule.ProvideWebModule;
import uk.co.saiman.webmodule.axios.RequireAxiosWebModule;
import uk.co.saiman.webmodule.react.RequireReactWebModule;
import uk.co.saiman.webmodule.react.dom.RequireReactDomWebModule;
import uk.co.saiman.webmodule.react.redux.RequireReactReduxWebModule;
import uk.co.saiman.webmodule.redux.RequireReduxWebModule;
import uk.co.saiman.webmodule.redux.thunk.RequireReduxThunkWebModule;

/**
 * Annotation to generate requirement to SAI web console utilities.
 * 
 * @author Elias N Vasylenko
 */
@RequireReactWebModule
@RequireReactDomWebModule
@RequireReactReduxWebModule
@RequireReduxThunkWebModule
@RequireReduxWebModule
@RequireAxiosWebModule
@RequireSaiWebConsoleWebModule
@RequireSaiCommsWebModule
@ProvideWebModule(
    id = COPLEY_WEB_MODULE_NAME,
    version = COPLEY_WEB_MODULE_VERSION,
    entryPoint = DEFAULT_ENTRY_POINT,
    resourceRoot = DEFAULT_RESOURCE_ROOT,
    format = ESM)
public interface CopleyWebConsoleConstants {
  final String COPLEY_WEB_MODULE_NAME = "@saiman/copley";
  final String COPLEY_WEB_MODULE_VERSION = "1.0.0";
}
