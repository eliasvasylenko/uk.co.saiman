package uk.co.saiman.properties.impl;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;

import uk.co.saiman.properties.LocaleProvider;
import uk.co.saiman.properties.Localized;
import uk.co.saiman.properties.PropertyResource;
import uk.co.saiman.properties.PropertyValueConversion;
import uk.co.saiman.properties.PropertyValueConverter;

public class PropertyValueConverterImpl implements PropertyValueConverter {
  @Override
  public PropertyValueConversion<?> getConversion(
      LocaleProvider localeProvider,
      PropertyResource propertyResource,
      AnnotatedType type,
      String key) {
    requireNonNull(localeProvider);
    requireNonNull(propertyResource);
    requireNonNull(type);
    requireNonNull(key);

    if (type.getType() == String.class) {
      return new StringFormatConversion(localeProvider, propertyResource, key);
    }

    if (type instanceof AnnotatedParameterizedType) {
      AnnotatedParameterizedType parameterizedType = (AnnotatedParameterizedType) type;
      Class<?> rawType = (Class<?>) ((ParameterizedType) type.getType()).getRawType();

      if (rawType == Localized.class) {
        return new LocalizedConversion(
            this,
            localeProvider,
            propertyResource,
            parameterizedType.getAnnotatedActualTypeArguments()[0],
            key);
      }
    }

    return null;
  }
}
