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

/**
 * 
 */
public class WebModuleConstants {
  private WebModuleConstants() {}

  public static final String ID_ATTRIBUTE = "id";
  public static final String VERSION_ATTRIBUTE = "version";

  public static final String ESM = "esm";
  public static final ModuleFormat ESM_FORMAT = new ModuleFormat(ESM);
  public static final String CJS = "cjs";
  public static final ModuleFormat CJS_FORMAT = new ModuleFormat(CJS);
  public static final String AMD = "amd";
  public static final ModuleFormat AMD_FORMAT = new ModuleFormat(AMD);
}
