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
 * This file is part of uk.co.saiman.log.provider.
 *
 * uk.co.saiman.log.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.log.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.log.provider;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import uk.co.saiman.log.Log;

/**
 * {@link LogListener} implementation dumping all logs to console
 * 
 * @author Elias N Vasylenko
 */
@Component(scope = ServiceScope.PROTOTYPE)
@SuppressWarnings("javadoc")
public class LogProvider implements Log {
  private Bundle usingBundle;

  private ServiceReference<LoggerFactory> logServiceReference;
  @Reference(service = LoggerFactory.class)
  Logger logService;

  @Activate
  public void activate(ComponentContext context) {
    usingBundle = context.getUsingBundle();
    logServiceReference = usingBundle.getBundleContext().getServiceReference(LoggerFactory.class);
    if (logServiceReference != null)
      logService = usingBundle
          .getBundleContext()
          .getService(logServiceReference)
          .getLogger(usingBundle, usingBundle.getSymbolicName(), Logger.class);
  }

  @Deactivate
  public void deactivate() {
    if (usingBundle.getState() == Bundle.ACTIVE) {
      usingBundle.getBundleContext().ungetService(logServiceReference);
    }
  }

  @Override
  public void log(Level level, String message) {
    switch (level) {
    case TRACE:
      logService.trace(message);
      break;
    case DEBUG:
      logService.debug(message);
      break;
    case INFO:
      logService.info(message);
      break;
    case WARN:
      logService.warn(message);
      break;
    case ERROR:
      logService.error(message);
      break;
    default:
      throw new AssertionError();
    }
  }

  @Override
  public void log(Level level, Throwable exception) {
    log(level, exception.getMessage());
  }

  @Override
  public void log(Level level, String message, Throwable exception) {
    log(level, message + " (" + exception.getMessage() + ")");
  }
}
