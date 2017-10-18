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

import java.util.function.Function;

import uk.co.saiman.log.provider.ConsoleLog.ConsoleLogConfiguration;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * {@link LogListener} implementation dumping all logs to console
 * 
 * @author Elias N Vasylenko
 */
@Designate(ocd = ConsoleLogConfiguration.class)
@Component(
    configurationPid = ConsoleLog.CONFIGURATION_PID,
    configurationPolicy = ConfigurationPolicy.REQUIRE,
    immediate = true)
public class ConsoleLog implements LogListener {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      name = "Console Log Configuration",
      description = "The console log provides a listener over the OSGi log service which directs output to stdout")
  public @interface ConsoleLogConfiguration {
    @AttributeDefinition(
        name = "Enable Console Log",
        description = "Enable console output for the OSGi log service")
    boolean enabled();
  }

  static final String CONFIGURATION_PID = "uk.co.saiman.console.log";

  private boolean enabled = false;

  @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
  @SuppressWarnings("javadoc")
  public void addLogReader(LogReaderService service) {
    service.addLogListener(this);
  }

  @SuppressWarnings("javadoc")
  public void removeLogReader(LogReaderService service) {
    service.removeLogListener(this);
  }

  @Activate
  @Modified
  void updated(ConsoleLogConfiguration configuration) {
    enabled = configuration.enabled();
  }

  @Override
  public void logged(LogEntry entry) {
    if (enabled) {
      String level = formatLevel(entry.getLevel());
      String bundle = formatIfPresent(entry.getBundle());
      String service = formatIfPresent(entry.getServiceReference());

      System.out.println("[" + level + bundle + service + "] " + entry.getMessage());

      if (entry.getException() != null) {
        entry.getException().printStackTrace();
      }
    }
  }

  private String formatLevel(int level) {
    String levelString;

    switch (level) {
    case LogService.LOG_ERROR:
      levelString = "ERROR  ";
      break;
    case LogService.LOG_WARNING:
      levelString = "WARNING";
      break;
    case LogService.LOG_INFO:
      levelString = "INFO   ";
      break;
    case LogService.LOG_DEBUG:
      levelString = "DEBUG  ";
      break;
    default:
      throw new IllegalArgumentException("Unexpected log level");
    }

    return levelString;
  }

  private String formatIfPresent(Object object) {
    return formatIfPresent(object, Object::toString);
  }

  private String formatIfPresent(Object object, Function<Object, String> format) {
    return object != null ? "; " + format.apply(object) : "";
  }
}
