package uk.co.saiman.properties.provider;

import static java.util.Arrays.asList;
import static org.osgi.framework.Constants.BUNDLE_LOCALIZATION_DEFAULT_BASENAME;
import static uk.co.saiman.properties.PropertyResourceBundle.getDefaultResource;

import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.annotations.Component;

import uk.co.saiman.properties.PropertyResource;
import uk.co.saiman.properties.PropertyResourceBundle;
import uk.co.saiman.properties.PropertyResourceLoader;

@Component
public class PropertyResourceLoaderService implements PropertyResourceLoader {
  @Override
  public PropertyResource loadResource(Class<?> accessorClass) {
    Bundle bundle = FrameworkUtil.getBundle(accessorClass);

    String localization = bundle.getHeaders().get(Constants.BUNDLE_LOCALIZATION);
    if (localization == null) {
      localization = BUNDLE_LOCALIZATION_DEFAULT_BASENAME;
    }

    ClassLoader classLoader = bundle.adapt(BundleWiring.class).getClassLoader();

    return new PropertyResourceBundle(
        accessorClass,
        classLoader,
        asList(localization, getDefaultResource(accessorClass)));
  }
}
