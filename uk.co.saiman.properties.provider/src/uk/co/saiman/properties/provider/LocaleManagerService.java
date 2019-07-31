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
 * This file is part of uk.co.saiman.properties.provider.
 *
 * uk.co.saiman.properties.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.properties.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.properties.provider;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.observable.Observable;
import uk.co.saiman.properties.LocaleManager;
import uk.co.saiman.properties.LocaleProvider;
import uk.co.saiman.properties.provider.LocaleManagerService.LocaleManagerConfiguration;

/**
 * A locale manager configurable via the config admin service.
 * 
 * @author Elias N Vasylenko
 */
@Designate(ocd = LocaleManagerConfiguration.class)
@Component(name = LocaleManagerService.CONFIGURATION_PID, configurationPid = LocaleManagerService.CONFIGURATION_PID)
public class LocaleManagerService implements LocaleManager, LocaleProvider {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(id = CONFIGURATION_PID, name = "Locale Configuration", description = "The configuration for the user locale for the application")
  public @interface LocaleManagerConfiguration {
    @AttributeDefinition(name = "Locale", description = "The user locale for the application")
    String locale() default "";
  }

  @Reference
  private ConfigurationAdmin configurationAdmin;

  /**
   * Configuration pid for OSGi configuration.
   */
  static final String CONFIGURATION_PID = "uk.co.saiman.properties.locale.manager";
  /**
   * Key for locale setting string, in the format specified by
   * {@link Locale#forLanguageTag(String)}.
   */
  static final String LOCALE_KEY = "locale";

  private final LocaleManager component;

  /**
   * Create empty manager
   */
  public LocaleManagerService() {
    component = LocaleManager.getManager(Locale.getDefault());
  }

  @Override
  public Locale get() {
    return component.getLocale();
  }

  @Override
  public Locale set(Locale locale) {
    Locale previous = setImpl(locale);

    if (!previous.equals(locale)) {
      try {
        Configuration configuration = configurationAdmin.getConfiguration(CONFIGURATION_PID);

        Dictionary<String, Object> properties = configuration.getProperties();
        properties.put(LOCALE_KEY, locale.toLanguageTag());
        configuration.update(properties);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    return previous;
  }

  private Locale setImpl(Locale locale) {
    return component.set(locale);
  }

  @Activate
  void activate(LocaleManagerConfiguration configuration) {
    update(configuration);
  }

  @Modified
  void update(LocaleManagerConfiguration configuration) {
    Locale locale;

    String localeString = configuration.locale();
    if (localeString.equals(""))
      locale = Locale.getDefault();
    else
      locale = Locale.forLanguageTag(localeString);
    setImpl(locale);
  }

  @Override
  public Observable<Change<Locale>> changes() {
    return component.changes();
  }

  @Override
  public void setProblem(Supplier<Throwable> t) {
    component.setProblem(t);
  }

  @Override
  public Optional<Locale> tryGet() {
    return component.tryGet();
  }

  @Override
  public Observable<Locale> value() {
    return component.value();
  }

  @Override
  public Observable<Optional<Locale>> optionalValue() {
    return component.optionalValue();
  }
}
