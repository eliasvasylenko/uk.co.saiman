package uk.co.saiman.properties.provider;

import java.util.Locale;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.log.Log;
import uk.co.saiman.observable.ObservableValue;
import uk.co.saiman.properties.LocaleProvider;
import uk.co.saiman.properties.PropertyLoader;

@Component
public class PropertyLoaderService implements PropertyLoader {
  private PropertyLoader propertyLoader;

  @Reference
  private LocaleProvider localeProvider;

  @Reference
  private Log log;

  @Activate // TODO constructor injection R7
  public void initialize() {
    propertyLoader = PropertyLoader.newPropertyLoader(localeProvider, log);
  }

  @Override
  public Locale getLocale() {
    return propertyLoader.getLocale();
  }

  @Override
  public ObservableValue<Locale> locale() {
    return propertyLoader.locale();
  }

  @Override
  public <T> T getProperties(Class<T> accessor) {
    return propertyLoader.getProperties(accessor);
  }
}
