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

import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Component;

import uk.co.saiman.webmodule.PackageId;
import uk.co.saiman.webmodule.WebModule;
import uk.co.saiman.webmodule.i18n.ResourceBundleWebModule;

@Component(service = WebModule.class, immediate = true)
public class CopleyResourceWebModule extends ResourceBundleWebModule {
  public CopleyResourceWebModule() {
    super(new PackageId("saiman", "copley-i18n"), new Version(1, 0, 0), "/bundle");
  }
}