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
 * This file is part of uk.co.saiman.webmodules.commonjs.
 *
 * uk.co.saiman.webmodules.commonjs is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.webmodules.commonjs is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.webmodules.commonjs.repository.bnd;

import aQute.bnd.build.Workspace;
import aQute.service.reporter.Reporter;
import aQute.service.reporter.Reporter.SetLocation;
import uk.co.saiman.log.Log;

public class ReporterLog implements Log {
  private final Reporter reporter;
  private final Workspace workspace;

  public ReporterLog(Reporter reporter, Workspace workspace) {
    this.reporter = reporter;
    this.workspace = workspace;
  }

  public ReporterLog(Reporter reporter) {
    this.reporter = reporter;
    this.workspace = null;
  }

  void setLocation(SetLocation setLocation) {
    if (workspace != null) {
      setLocation.file(workspace.getFile("build.bnd").getAbsolutePath());
    }
  }

  @Override
  public void log(Level level, String message) {
    switch (level) {
    case ERROR:
      setLocation(reporter.error("%s", message));
      break;
    case WARN:
      setLocation(reporter.warning("%s", message));
      break;
    case DEBUG:
    case INFO:
    case TRACE:
    default:
    }
  }

  @Override
  public void log(Level level, String message, Throwable exception) {
    if (exception != null)
      exception.printStackTrace();

    switch (level) {
    case ERROR:
      setLocation(reporter.exception(exception, "%s", message));
      break;
    case WARN:
      setLocation(reporter.warning("%s", message + ": " + exception.getMessage()));
      break;
    case DEBUG:
    case INFO:
    case TRACE:
    default:
    }
  }

  @Override
  public void log(Level level, Throwable exception) {
    if (exception != null)
      exception.printStackTrace();

    switch (level) {
    case ERROR:
      setLocation(reporter.exception(exception, "%s", exception.getMessage()));
      break;
    case WARN:
      setLocation(reporter.warning("%s", exception.getMessage()));
      break;
    case DEBUG:
    case INFO:
    case TRACE:
    default:
    }
  }
}
