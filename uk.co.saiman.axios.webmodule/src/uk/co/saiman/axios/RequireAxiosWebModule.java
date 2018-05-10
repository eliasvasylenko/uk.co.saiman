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

import static uk.co.saiman.axios.AxiosConstants.AXIOS_WEB_MODULE_NAME;
import static uk.co.saiman.axios.AxiosConstants.AXIOS_WEB_MODULE_VERSION;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import uk.co.saiman.webmodules.RequireWebModule;

@RequireWebModule(name = AXIOS_WEB_MODULE_NAME, version = AXIOS_WEB_MODULE_VERSION)
@Retention(RetentionPolicy.CLASS)
public @interface RequireAxiosWebModule {}
