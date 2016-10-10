/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,-========\     ,`===\    /========== \
 *      /== \___/== \  ,`==.== \   \__/== \___\/
 *     /==_/____\__\/,`==__|== |     /==  /
 *     \========`. ,`========= |    /==  /
 *   ___`-___)== ,`== \____|== |   /==  /
 *  /== \__.-==,`==  ,`    |== '__/==  /_
 *  \======== /==  ,`      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.simulation.
 *
 * uk.co.saiman.simulation is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.simulation is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.simulation.msapex;

import java.net.URL;

import org.eclipse.fx.ui.services.theme.Stylesheet;
import org.eclipse.fx.ui.services.theme.Theme;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

/**
 * Default stylesheet for experiment interface components.
 * 
 * @author Elias N Vasylenko
 */
@Component(property = Constants.SERVICE_RANKING + "=" + SimulationStylesheet.RANKING)
public class SimulationStylesheet implements Stylesheet {
	@SuppressWarnings("javadoc")
	public static final int RANKING = 10;

	@Override
	public boolean appliesToTheme(Theme t) {
		return true;
	}

	@Override
	public URL getURL(Theme t) {
		return SimulationStylesheet.class.getClassLoader().getResource("css/simulation.css");
	}
}
