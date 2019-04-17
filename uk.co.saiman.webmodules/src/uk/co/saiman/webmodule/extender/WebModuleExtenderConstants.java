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
package uk.co.saiman.webmodule.extender;

/**
 * 
 */
public class WebModuleExtenderConstants {
  private WebModuleExtenderConstants() {}

  /**
   * The Module Server Extender name
   */
  public static final String EXTENDER_NAME = "uk.co.saiman.webmodule.extender";

  public static final String EXTENDER_VERSION_ATTRIBUTE = EXTENDER_NAME + ".version";
  public static final String RESOURCE_ROOT_ATTRIBUTE = "resource.root";
  public static final String FORMAT_ATTRIBUTE = "format";
  public static final String ENTRY_POINT_ATTRIBUTE = "entry.point";
  public static final String DEPENDENCIES_ATTRIBUTE = "dependencies";

  public static final String DEFAULT_RESOURCE_ROOT = "OSGI-INF/uk.co.saiman.webmodule";

  /**
   * The current version
   */
  public static final String EXTENDER_VERSION = "1.0.0";
}
