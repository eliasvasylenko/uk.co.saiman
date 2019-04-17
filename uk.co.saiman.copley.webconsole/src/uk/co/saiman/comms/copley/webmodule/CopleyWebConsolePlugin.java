/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.copley.webconsole.
 *
 * uk.co.saiman.copley.webconsole is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.copley.webconsole is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
/*
CommsWebConsolePlugin.java * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.comms.provider.
 *
 * uk.co.saiman.comms.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.copley.webmodule;

import static org.apache.felix.webconsole.WebConsoleConstants.PLUGIN_LABEL;
import static org.apache.felix.webconsole.WebConsoleUtil.getVariableResolver;
import static uk.co.saiman.comms.copley.webmodule.CopleyWebConsoleConstants.COPLEY_WEB_MODULE_NAME;
import static uk.co.saiman.comms.copley.webmodule.CopleyWebConsoleConstants.COPLEY_WEB_MODULE_VERSION;
import static uk.co.saiman.comms.copley.webmodule.CopleyWebConsolePlugin.COPLEY_PLUGIN_ID;
import static uk.co.saiman.instrument.Instrument.INSTRUMENT_CATEGORY;
import static uk.co.saiman.webmodule.WebModuleConstants.DEFAULT_ENTRY_POINT;
import static uk.co.saiman.webmodule.WebModuleConstants.ESM;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.DEFAULT_RESOURCE_ROOT;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.webconsole.DefaultVariableResolver;
import org.apache.felix.webconsole.SimpleWebConsolePlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import uk.co.saiman.comms.webmodule.RequireSaiCommsWebModule;
import uk.co.saiman.webconsole.RequireSaiWebConsoleWebModule;
import uk.co.saiman.webmodule.ProvideWebModule;
import uk.co.saiman.webmodule.axios.RequireAxiosWebModule;
import uk.co.saiman.webmodule.react.RequireReactWebModule;
import uk.co.saiman.webmodule.react.dom.RequireReactDomWebModule;
import uk.co.saiman.webmodule.react.redux.RequireReactReduxWebModule;
import uk.co.saiman.webmodule.redux.RequireReduxWebModule;
import uk.co.saiman.webmodule.redux.thunk.RequireReduxThunkWebModule;

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
@Component(
    immediate = true,
    service = Servlet.class,
    property = PLUGIN_LABEL + "=" + COPLEY_PLUGIN_ID)
public class CopleyWebConsolePlugin extends SimpleWebConsolePlugin {
  public static final String COPLEY_PLUGIN_ID = "uk.co.saiman.copley";

  private static final long serialVersionUID = 1L;

  private static final String REST_ID = "copley.rest.id";
  private static final String REST_NAME = "copley.rest.name";

  private final String template;

  public CopleyWebConsolePlugin() {
    super(
        COPLEY_PLUGIN_ID,
        "${copley.name}",
        INSTRUMENT_CATEGORY,
        new String[] { "${pluginRoot}/static/index.css" });
    this.template = this.readTemplateFile("/static/index.html");
  }

  @Activate
  @Override
  public void activate(BundleContext bundleContext) {
    super.activate(bundleContext);
  }

  @Deactivate
  @Override
  public void deactivate() {
    super.deactivate();
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void renderContent(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    DefaultVariableResolver component = (DefaultVariableResolver) getVariableResolver(request);
    component.put(REST_ID, getLabel());
    component.put(REST_NAME, getTitle());

    response.getWriter().append(template);
  }
}
