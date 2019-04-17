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
 * This file is part of uk.co.saiman.webmodules.
 *
 * uk.co.saiman.webmodules is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.webmodules is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.webmodule;

import static org.osgi.namespace.service.ServiceNamespace.CAPABILITY_OBJECTCLASS_ATTRIBUTE;
import static org.osgi.namespace.service.ServiceNamespace.SERVICE_NAMESPACE;
import static uk.co.saiman.webmodule.WebModuleConstants.DEFAULT_ENTRY_POINT;
import static uk.co.saiman.webmodule.WebModuleConstants.ESM;
import static uk.co.saiman.webmodule.WebModuleConstants.VERSION_ATTRIBUTE;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.DEFAULT_RESOURCE_ROOT;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.ENTRY_POINT_ATTRIBUTE;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.EXTENDER_VERSION;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.EXTENDER_VERSION_ATTRIBUTE;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.FORMAT_ATTRIBUTE;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.RESOURCE_ROOT_ATTRIBUTE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.osgi.annotation.bundle.Attribute;
import org.osgi.annotation.bundle.Capability;

import uk.co.saiman.webmodule.extender.RequireWebModuleExtender;

@Capability(
    namespace = SERVICE_NAMESPACE,
    attribute = {
        CAPABILITY_OBJECTCLASS_ATTRIBUTE + "=uk.co.saiman.webmodule.WebModule",
        EXTENDER_VERSION_ATTRIBUTE + "=" + EXTENDER_VERSION })
@RequireWebModuleExtender
@Retention(RetentionPolicy.CLASS)
public @interface ProvideWebModule {
  @Attribute
  String id();

  @Attribute(VERSION_ATTRIBUTE + ":Version")
  String version();

  @Attribute(RESOURCE_ROOT_ATTRIBUTE)
  String resourceRoot() default DEFAULT_RESOURCE_ROOT;

  @Attribute(ENTRY_POINT_ATTRIBUTE)
  String entryPoint() default DEFAULT_ENTRY_POINT;

  @Attribute(FORMAT_ATTRIBUTE)
  String format() default ESM;
}
