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
package uk.co.saiman.comms.impl;

import static org.apache.felix.webconsole.WebConsoleUtil.getVariableResolver;
import static uk.co.saiman.instrument.Instrument.INSTRUMENT_CATEGORY;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.webconsole.DefaultVariableResolver;
import org.apache.felix.webconsole.SimpleWebConsolePlugin;

class CommsWebConsolePlugin extends SimpleWebConsolePlugin {
	private static final long serialVersionUID = 1L;

	private static final String REST_ID = "comms.rest.id";
	private static final String REST_NAME = "comms.rest.name";

	private final String template;

	public CommsWebConsolePlugin(String id, String name) {
		super(id, name, INSTRUMENT_CATEGORY, new String[] { "${pluginRoot}/res/sai/comms.css" });
		this.template = this.readTemplateFile("/res/sai/comms.html");
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
