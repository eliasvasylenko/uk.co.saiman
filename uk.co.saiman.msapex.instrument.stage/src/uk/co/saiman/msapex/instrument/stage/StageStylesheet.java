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
 * This file is part of uk.co.saiman.msapex.instrument.stage.
 *
 * uk.co.saiman.msapex.instrument.stage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.instrument.stage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.instrument.stage;

import java.net.URL;

import org.eclipse.fx.ui.services.theme.Stylesheet;
import org.eclipse.fx.ui.services.theme.Theme;
import org.osgi.service.component.annotations.Component;

/**
 * Default stylesheet for periodic tables.
 * 
 * @author Elias N Vasylenko
 */
@Component
public class StageStylesheet implements Stylesheet {
	@Override
	public boolean appliesToTheme(Theme t) {
		return true;
	}

	@Override
	public URL getURL(Theme t) {
		return StageStylesheet.class.getClassLoader().getResource("css/stage.css");
	}
}
