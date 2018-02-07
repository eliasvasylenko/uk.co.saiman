package uk.co.saiman.comms.impl;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Stream.concat;
import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static org.osgi.service.component.annotations.ReferenceCardinality.OPTIONAL;
import static org.osgi.service.component.annotations.ReferencePolicy.DYNAMIC;
import static uk.co.saiman.log.Log.Level.ERROR;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.comms.impl.SerialCommsSimulation.SerialPortSimulationConfiguration;
import uk.co.saiman.comms.serial.SerialPort;
import uk.co.saiman.comms.serial.SerialPorts;
import uk.co.saiman.log.Log;

@Designate(ocd = SerialPortSimulationConfiguration.class, factory = true)
@Component(
    configurationPid = SerialCommsSimulation.CONFIGURATION_PID,
    configurationPolicy = REQUIRE)
public class SerialCommsSimulation implements SerialPorts {
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

  @Reference(cardinality = OPTIONAL, policy = DYNAMIC)
  volatile Log log;

  private Map<String, DumpSerialPort> dumpPorts;
  private Map<String, InvalidSerialPort> invalidPorts;
  private Map<String, PairedSerialPort> pairedPorts;

  public SerialCommsSimulation() {
    dumpPorts = new HashMap<>();
    invalidPorts = new HashMap<>();
    pairedPorts = new HashMap<>();
  }

  @Activate
  public void activate(SerialPortSimulationConfiguration configuration) {
    modified(configuration);
  }

  @Modified
  public void modified(SerialPortSimulationConfiguration configuration) {
    try {
      Set<String> dumpPorts = asSet(configuration.dumpPorts());
      this.dumpPorts.keySet().retainAll(dumpPorts);
      for (String port : dumpPorts)
        this.dumpPorts.computeIfAbsent(port, DumpSerialPort::new);

      Set<String> invalidPorts = asSet(configuration.invalidPorts());
      this.invalidPorts.keySet().retainAll(invalidPorts);
      for (String port : invalidPorts)
        this.invalidPorts.computeIfAbsent(port, InvalidSerialPort::new);

      Set<String> pairedPorts = asSet(configuration.pairedPorts());
      this.pairedPorts.keySet().retainAll(pairedPorts);
      for (String port : pairedPorts)
        this.pairedPorts
            .computeIfAbsent(port, p -> new PairedSerialPort(p, p + "." + PARTNER_POSTFIX));
    } catch (Exception e) {
      Log log = this.log;
      if (log != null)
        log.log(ERROR, e);
      else
        e.printStackTrace();
    }
  }

  private Set<String> asSet(String[] ports) {
    return ports == null ? Collections.emptySet() : new HashSet<>(asList(ports));
  }

  @Override
  public Stream<SerialPort> getPorts() {
    return concat(
        concat(dumpPorts.values().stream(), invalidPorts.values().stream()),
        pairedPorts.values().stream().flatMap(p -> Stream.of(p, p.getPartner())));
  }

  @Override
  public SerialPort getPort(String port) {
    requireNonNull(port);

    if (dumpPorts.containsKey(port)) {
      return dumpPorts.get(port);
    } else if (invalidPorts.containsKey(port)) {
      return invalidPorts.get(port);
    } else if (pairedPorts.containsKey(port)) {
      return pairedPorts.get(port);
    } else if (port.endsWith("." + PARTNER_POSTFIX)) {
      String pairPort = port.substring(0, port.length() - PARTNER_POSTFIX.length() - 1);
      if (pairedPorts.containsKey(pairPort)) {
        return pairedPorts.get(pairPort).getPartner();
      }
    }

    return new InvalidSerialPort(port);
  }
}
