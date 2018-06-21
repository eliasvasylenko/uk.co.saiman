package uk.co.saiman.comms.copley.webmodule;

import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Component;

import uk.co.saiman.webmodule.PackageId;
import uk.co.saiman.webmodule.WebModule;
import uk.co.saiman.webmodule.i18n.ResourceBundleWebModule;

@Component(service = WebModule.class, immediate = true)
public class CopleyResourceWebModule extends ResourceBundleWebModule {
  public CopleyResourceWebModule() {
    super(new PackageId("saiman", "copley-i18n"), new Version(1, 0, 0), "/bundle");
  }
}
