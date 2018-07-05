package uk.co.saiman.properties.impl;

import uk.co.saiman.properties.PropertyResource;
import uk.co.saiman.properties.PropertyResourceBundle;
import uk.co.saiman.properties.PropertyResourceLoader;

public class PropertyResourceLoaderImpl implements PropertyResourceLoader {
  @Override
  public PropertyResource loadResource(Class<?> accessorClass) {
    return new PropertyResourceBundle(accessorClass);
  }
}
