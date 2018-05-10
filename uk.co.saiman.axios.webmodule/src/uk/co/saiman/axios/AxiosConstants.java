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
 * This file is part of uk.co.saiman.axios.webmodule.
 *
 * uk.co.saiman.axios.webmodule is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.axios.webmodule is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.axios;

import uk.co.saiman.webmodules.ProvideWebModule;

@ProvideWebModule(
    name = AxiosConstants.AXIOS_WEB_MODULE_NAME,
    version = AxiosConstants.AXIOS_WEB_MODULE_VERSION,
    root = "/META-INF/resources/webjars/axios/" + AxiosConstants.AXIOS_WEB_MODULE_VERSION,
    main = "index.js")
public interface AxiosConstants {
  final String AXIOS_WEB_MODULE_NAME = "axios";
  final String AXIOS_WEB_MODULE_VERSION = "0.16.1";
}
