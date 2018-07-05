package uk.co.saiman.properties;

import uk.co.saiman.properties.impl.PropertyResourceLoaderImpl;

public interface PropertyResourceLoader {
  PropertyResource loadResource(Class<?> accessorClass);

  static PropertyResourceLoader getDefaultResourceLoader() {
    return new PropertyResourceLoaderImpl();
  }
}
