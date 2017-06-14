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
 * This file is part of uk.co.saiman.comms.
 *
 * uk.co.saiman.comms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.impl;

import static uk.co.saiman.instrument.Instrument.INSTRUMENT_CATEGORY;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.webconsole.SimpleWebConsolePlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import uk.co.saiman.facebook.react.RequireReactWebResource;

@RequireReactWebResource
@Component(
		name = "osgi.enroute.examples.webconsole",
		service = Servlet.class,
		property = "felix.webconsole.label=" + CommsWebConsolePlugin.PLUGIN)
public class CommsWebConsolePlugin extends SimpleWebConsolePlugin {
	private static final long serialVersionUID = 1L;

	final static String PLUGIN = "comms";
	final static String TITLE = "Comms";

	private final String template;

	public CommsWebConsolePlugin() {
		super(PLUGIN, TITLE, INSTRUMENT_CATEGORY, new String[] { "/comms/static/sai/comms.css" });
		template = this.readTemplateFile("/static/sai/comms.html");
	}

	@Activate
	@Override
	public void activate(BundleContext bundleContext) {
		super.activate(bundleContext);
	}

	@Override
	@Deactivate
	public void deactivate() {
		super.deactivate();
	}

	@Override
	protected void renderContent(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.getWriter().append(template);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println(req.getParts());
	}
}
