package uk.co.saiman.comms.copley.impl;

import static org.osgi.framework.Constants.SERVICE_PID;
import static org.osgi.service.component.annotations.ReferenceCardinality.MULTIPLE;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import uk.co.saiman.comms.copley.CopleyController;

@Component(service = CopleyService.class)
public class CopleyService {
  private final Map<CopleyController, ServiceReference<CopleyController>> controllers = new HashMap<>();;

  public CopleyService() {}

  @Reference(cardinality = MULTIPLE, policy = DYNAMIC, unbind = "removeController")
  synchronized void addController(
      CopleyController controller,
      ServiceReference<CopleyController> serviceReference,
      Map<String, ?> properties) {
    if (controllers.containsKey(controller)) {
      throw new IllegalArgumentException("Already contains controller " + controller);
    }
    String servicePid = (String) serviceReference.getProperty(SERVICE_PID);
    if (servicePid != null) {
      controllers.put(controller, serviceReference);
    }
  }

  synchronized void removeController(
      CopleyController controller,
      ServiceReference<CopleyController> serviceReference) {
    controllers.remove(controller, serviceReference);
  }

  public String getId(CopleyController controller) {
    return (String) controllers.get(controller).getProperty(SERVICE_PID);
  }

  public CopleyController getController(String id) {
    return controllers
        .keySet()
        .stream()
        .filter(c -> getId(c).equals(id))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Cannot find controller " + id));
  }

  public Stream<CopleyController> getControllers() {
    return controllers.keySet().stream();
  }

  public Bundle getBundle(CopleyController controller) {
    return controllers.get(controller).getBundle();
  }
}
