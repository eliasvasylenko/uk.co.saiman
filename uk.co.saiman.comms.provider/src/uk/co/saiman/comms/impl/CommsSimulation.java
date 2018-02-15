package uk.co.saiman.comms.impl;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static uk.co.saiman.log.Log.Level.ERROR;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.comms.CommsPort;
import uk.co.saiman.comms.InvalidCommsPort;
import uk.co.saiman.comms.impl.CommsSimulation.SerialPortSimulationConfiguration;
import uk.co.saiman.log.Log;

@Designate(ocd = SerialPortSimulationConfiguration.class, factory = true)
@Component(
    immediate = true,
    configurationPid = CommsSimulation.CONFIGURATION_PID,
    configurationPolicy = REQUIRE)
public class CommsSimulation {
  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      name = "Simulated Serial Comms Configuration",
      description = "The simulated serial comms component provides serial port interfaces of various simulation strategies")
  public @interface SerialPortSimulationConfiguration {
    @AttributeDefinition(
        name = "Dump Ports",
        description = "A list of port names to provide as dump ports which discard data")
    String[] dumpPorts() default {};

    @AttributeDefinition(
        name = "Invalid Ports",
        description = "A list of port names to provide as broken ports which reject connections, this may be useful for integration testing")
    String[] invalidPorts() default {};

    @AttributeDefinition(
        name = "Pair Ports",
        description = "A list of port names to provide as pair ports. For each port named \"<name>\" a corresponding port named \"<name>.pair\" will be created and the two will be linked")
    String[] pairedPorts() default {};
  }

  static final String CONFIGURATION_PID = "uk.co.saiman.comms.simulation";
  public static final String PARTNER_POSTFIX = "partner";
  public static final String NAME = "name";

  @Reference
  private Log log;

  private Map<String, ServiceRegistration<CommsPort>> dumpPorts;
  private Map<String, ServiceRegistration<CommsPort>> invalidPorts;
  private Map<String, ServiceRegistration<CommsPort>> pairedPorts;

  public CommsSimulation() {
    dumpPorts = new HashMap<>();
    invalidPorts = new HashMap<>();
    pairedPorts = new HashMap<>();
  }

  @Activate
  public void activate(BundleContext context, SerialPortSimulationConfiguration configuration) {
    modified(context, configuration);
  }

  @Modified
  public void modified(BundleContext context, SerialPortSimulationConfiguration configuration) {
    try {
      updateServices(context, dumpPorts, configuration.dumpPorts(), DumpCommsPort::new);

      updateServices(context, invalidPorts, configuration.invalidPorts(), InvalidCommsPort::new);

      removeServices(pairedPorts, configuration.pairedPorts());
      for (String portName : configuration.pairedPorts()) {
        if (!pairedPorts.containsKey(portName)) {
          PairedCommsPort newPort = new PairedCommsPort(portName, portName + "." + PARTNER_POSTFIX);
          ServiceRegistration<CommsPort> service;
          service = context.registerService(CommsPort.class, newPort, getProperties(newPort));
          pairedPorts.put(newPort.getName(), service);

          newPort = newPort.getPartner();
          service = context.registerService(CommsPort.class, newPort, getProperties(newPort));
          pairedPorts.put(newPort.getName(), service);
        }
      }
    } catch (Exception e) {
      Log log = this.log;
      if (log != null)
        log.log(ERROR, e);
      e.printStackTrace();
    }
  }

  private void removeServices(
      Map<String, ServiceRegistration<CommsPort>> services,
      String[] newPortNames) {
    Set<String> portNames = newPortNames == null ? emptySet() : new HashSet<>(asList(newPortNames));

    for (String port : services.keySet()) {
      if (!portNames.contains(port)) {
        services.get(port).unregister();
      }
    }
  }

  private void updateServices(
      BundleContext context,
      Map<String, ServiceRegistration<CommsPort>> services,
      String[] newPortNames,
      Function<String, CommsPort> newPortFactory) {
    removeServices(services, newPortNames);

    if (newPortNames != null) {
      for (String portName : newPortNames) {
        if (!services.containsKey(portName)) {
          CommsPort newPort = newPortFactory.apply(portName);
          ServiceRegistration<CommsPort> service = context
              .registerService(CommsPort.class, newPort, getProperties(newPort));
          services.put(newPort.getName(), service);
        }
      }
    }
  }

  private Dictionary<String, String> getProperties(CommsPort port) {
    Dictionary<String, String> properties = new Hashtable<>();
    properties.put(NAME, port.getName());
    return properties;
  }
}
