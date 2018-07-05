package uk.co.saiman.properties.impl;

import static java.lang.String.format;

import java.util.List;

import uk.co.saiman.properties.LocaleProvider;
import uk.co.saiman.properties.PropertyResource;
import uk.co.saiman.properties.PropertyValueConversion;

public class StringFormatConversion implements PropertyValueConversion<String> {
  private final LocaleProvider localeProvider;
  private final PropertyResource propertyResource;
  private final String key;

  public StringFormatConversion(
      LocaleProvider localeProvider,
      PropertyResource propertyResource,
      String key) {
    this.localeProvider = localeProvider;
    this.propertyResource = propertyResource;
    this.key = key;
  }

  @Override
  public String applyConversion(List<?> arguments) {
    String value;
    try {
      value = format(
          propertyResource.getValue(key, localeProvider.getLocale()),
          arguments.toArray());
    } catch (Exception e) {
      value = format("<?%s?>%s", key, arguments);
    }
    return value;
  }
}
