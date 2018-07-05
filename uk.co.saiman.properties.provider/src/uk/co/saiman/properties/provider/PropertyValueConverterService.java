package uk.co.saiman.properties.provider;

import java.lang.reflect.AnnotatedType;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.properties.LocaleProvider;
import uk.co.saiman.properties.PropertyResource;
import uk.co.saiman.properties.PropertyValueConversion;
import uk.co.saiman.properties.PropertyValueConverter;

@Component
public class PropertyValueConverterService implements PropertyValueConverter {
  private final PropertyValueConverter converter = PropertyValueConverter
      .getDefaultValueConverter();

  @Override
  public PropertyValueConversion<?> getConversion(
      LocaleProvider localeProvider,
      PropertyResource propertyResource,
      AnnotatedType type,
      String key) {
    return converter.getConversion(localeProvider, propertyResource, type, key);
  }

}
