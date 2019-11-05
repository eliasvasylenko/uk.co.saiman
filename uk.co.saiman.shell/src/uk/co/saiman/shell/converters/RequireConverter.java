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
 * This file is part of uk.co.saiman.shell.
 *
 * uk.co.saiman.shell is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.shell is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.shell.converters;

import static java.lang.annotation.RetentionPolicy.CLASS;
import static org.osgi.namespace.service.ServiceNamespace.CAPABILITY_OBJECTCLASS_ATTRIBUTE;
import static org.osgi.namespace.service.ServiceNamespace.SERVICE_NAMESPACE;
import static org.osgi.resource.Namespace.EFFECTIVE_ACTIVE;

import java.lang.annotation.Retention;

import org.osgi.annotation.bundle.Requirement;

@Requirement(
    namespace = SERVICE_NAMESPACE,
    filter = "(&("
        + CAPABILITY_OBJECTCLASS_ATTRIBUTE
        + "=org.apache.felix.service.command.Converter)("
        + RequireConverter.TYPE
        + "=${#converterType}))",
    effective = EFFECTIVE_ACTIVE)
@Retention(CLASS)
public @interface RequireConverter {
  String TYPE = "type";

  Class<?> converterType();
}
