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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.webconsole.SimpleWebConsolePlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

import uk.co.saiman.comms.rest.CommsREST;

class CommsWebConsolePlugin extends SimpleWebConsolePlugin {
	private static final long serialVersionUID = 1L;

	private final CommsREST rest;
	private final String template;

	private String bundleName;
	private Version bundleVersion;

	public CommsWebConsolePlugin(CommsREST rest) {
		super(
				rest.getID(),
				rest.getName(),
				INSTRUMENT_CATEGORY,
				new String[] { "${pluginRoot}/res/sai/comms.css" });
		this.rest = rest;
		this.template = this.readTemplateFile("/res/sai/comms.html");
	}

	@Override
	public void activate(BundleContext bundleContext) {
		super.activate(bundleContext);
		this.bundleName = bundleContext.getBundle().getSymbolicName();
		this.bundleVersion = bundleContext.getBundle().getVersion();
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
