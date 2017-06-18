/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.comms.saint.
 *
 * uk.co.saiman.comms.saint is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms.saint is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.saint.impl;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static org.osgi.service.component.annotations.ReferencePolicy.STATIC;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;
import static uk.co.saiman.comms.saint.SaintCommandAddress.CMOS_REF;
import static uk.co.saiman.comms.saint.SaintCommandAddress.CURRENT_READBACK_1_ADC;
import static uk.co.saiman.comms.saint.SaintCommandAddress.CURRENT_READBACK_2_ADC;
import static uk.co.saiman.comms.saint.SaintCommandAddress.CURRENT_READBACK_3_ADC;
import static uk.co.saiman.comms.saint.SaintCommandAddress.CURRENT_READBACK_4_ADC;
import static uk.co.saiman.comms.saint.SaintCommandAddress.HV_DAC_1;
import static uk.co.saiman.comms.saint.SaintCommandAddress.HV_DAC_2;
import static uk.co.saiman.comms.saint.SaintCommandAddress.HV_DAC_3;
import static uk.co.saiman.comms.saint.SaintCommandAddress.HV_DAC_4;
import static uk.co.saiman.comms.saint.SaintCommandAddress.HV_LAT;
import static uk.co.saiman.comms.saint.SaintCommandAddress.HV_PORT;
import static uk.co.saiman.comms.saint.SaintCommandAddress.HV_RB_LAT;
import static uk.co.saiman.comms.saint.SaintCommandAddress.HV_RB_PORT;
import static uk.co.saiman.comms.saint.SaintCommandAddress.LASER_DETECT_REF;
import static uk.co.saiman.comms.saint.SaintCommandAddress.LED_LAT;
import static uk.co.saiman.comms.saint.SaintCommandAddress.LED_PORT;
import static uk.co.saiman.comms.saint.SaintCommandAddress.MAGNETRON_READBACK_ADC;
import static uk.co.saiman.comms.saint.SaintCommandAddress.MOTOR_LAT;
import static uk.co.saiman.comms.saint.SaintCommandAddress.MOTOR_PORT;
import static uk.co.saiman.comms.saint.SaintCommandAddress.NULL;
import static uk.co.saiman.comms.saint.SaintCommandAddress.PIRANI_READBACK_ADC;
import static uk.co.saiman.comms.saint.SaintCommandAddress.SPARE_MON_1_ADC;
import static uk.co.saiman.comms.saint.SaintCommandAddress.SPARE_MON_2_ADC;
import static uk.co.saiman.comms.saint.SaintCommandAddress.SPARE_MON_3_ADC;
import static uk.co.saiman.comms.saint.SaintCommandAddress.SPARE_MON_4_ADC;
import static uk.co.saiman.comms.saint.SaintCommandAddress.TURBO_CONTROL;
import static uk.co.saiman.comms.saint.SaintCommandAddress.TURBO_READBACKS;
import static uk.co.saiman.comms.saint.SaintCommandAddress.VACUUM_LAT;
import static uk.co.saiman.comms.saint.SaintCommandAddress.VACUUM_PORT;
import static uk.co.saiman.comms.saint.SaintCommandAddress.VACUUM_RB_LAT;
import static uk.co.saiman.comms.saint.SaintCommandAddress.VACUUM_RB_PORT;
import static uk.co.saiman.comms.saint.SaintCommandAddress.VOLTAGE_READBACK_1_ADC;
import static uk.co.saiman.comms.saint.SaintCommandAddress.VOLTAGE_READBACK_2_ADC;
import static uk.co.saiman.comms.saint.SaintCommandAddress.VOLTAGE_READBACK_3_ADC;
import static uk.co.saiman.comms.saint.SaintCommandAddress.VOLTAGE_READBACK_4_ADC;
import static uk.co.saiman.comms.saint.SaintCommandType.INPUT;
import static uk.co.saiman.comms.saint.SaintCommandType.OUTPUT;
import static uk.co.saiman.comms.saint.SaintCommandType.PING;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.comms.ByteConverters;
import uk.co.saiman.comms.Comms;
import uk.co.saiman.comms.CommsException;
import uk.co.saiman.comms.CommsImpl;
import uk.co.saiman.comms.saint.ADC;
import uk.co.saiman.comms.saint.HighVoltageReadback;
import uk.co.saiman.comms.saint.HighVoltageStatus;
import uk.co.saiman.comms.saint.I2C;
import uk.co.saiman.comms.saint.LEDStatus;
import uk.co.saiman.comms.saint.MotorStatus;
import uk.co.saiman.comms.saint.SaintCommandAddress;
import uk.co.saiman.comms.saint.SaintCommandType;
import uk.co.saiman.comms.saint.SaintComms;
import uk.co.saiman.comms.saint.TurboControl;
import uk.co.saiman.comms.saint.TurboReadbacks;
import uk.co.saiman.comms.saint.VacuumControl;
import uk.co.saiman.comms.saint.VacuumReadback;
import uk.co.saiman.comms.saint.Value;
import uk.co.saiman.comms.saint.ValueReadback;
import uk.co.saiman.comms.saint.ValueRequest;
import uk.co.saiman.comms.saint.impl.SaintCommsImpl.SaintCommsConfiguration;
import uk.co.saiman.comms.serial.SerialPort;
import uk.co.saiman.comms.serial.SerialPorts;

@Designate(ocd = SaintCommsConfiguration.class, factory = true)
@Component(configurationPid = SaintCommsImpl.CONFIGURATION_PID, configurationPolicy = REQUIRE)
public class SaintCommsImpl extends CommsImpl implements SaintComms, Comms {
  public static final String CONFIGURATION_PID = "uk.co.saiman.comms.saint";

  @SuppressWarnings("javadoc")
  @ObjectClassDefinition(
      name = "SAINT Comms Configuration",
      description = "The configuration for the underlying serial comms for a SAINT instrument")
  public @interface SaintCommsConfiguration {
    @AttributeDefinition(name = "Serial Port", description = "The serial port for comms")
    String serialPort();
  }

  class ValueImpl<T> implements Value<T> {
    private final Class<T> type;
    private final SaintCommandAddress actualValue;
    private final SaintCommandAddress requestedValue;

    public ValueImpl(
        Class<T> type,
        SaintCommandAddress actualValue,
        SaintCommandAddress requestedValue) {
      this.type = type;
      this.actualValue = actualValue;
      this.requestedValue = requestedValue;
    }

    @Override
    public Class<T> getType() {
      return type;
    }

    @Override
    public SaintCommandAddress getActualValueAddress() {
      return actualValue;
    }

    @Override
    public T getActual() {
      return getValue(actualValue);
    }

    @Override
    public SaintCommandAddress getRequestedValueAddress() {
      return requestedValue;
    }

    @Override
    public T getRequested() {
      return getValue(requestedValue);
    }

    private T getValue(SaintCommandAddress address) {
      return useChannel(channel -> {
        byte[] outputBytes = new byte[address.getBytes().length];
        byte[] inputBytes = executeSaintCommand(INPUT, address, channel, outputBytes);
        return converters.getConverter(type).fromBytes(inputBytes);
      });
    }

    @Override
    public void request(T data) {
      useChannel(channel -> {
        byte[] outputBytes = converters.getConverter(type).toBytes(data);
        executeSaintCommand(OUTPUT, actualValue, channel, outputBytes);
        return null;
      });
    }
  }

  @Reference(policy = STATIC, policyOption = GREEDY)
  SerialPorts comms;
  private SerialPort port;

  @Reference
  ByteConverters converters;

  private final Set<Value<?>> values = new HashSet<>();
  private Value<LEDStatus> ledStatus;
  private Value<VacuumControl> vacuumStatus;
  private Value<HighVoltageStatus> highVoltageStatus;
  private Value<MotorStatus> motorStatus;
  private Value<VacuumReadback> vacuumReadback;
  private Value<HighVoltageReadback> highVoltageReadback;

  private final Set<ValueRequest<?>> valueRequests = new HashSet<>();
  private ValueRequest<I2C> highVoltageDAC1;
  private ValueRequest<I2C> highVoltageDAC2;
  private ValueRequest<I2C> highVoltageDAC3;
  private ValueRequest<I2C> highVoltageDAC4;
  private ValueRequest<I2C> cmosRef;
  private ValueRequest<I2C> laserDetectRef;

  private final Set<ValueReadback<?>> valueReadbacks = new HashSet<>();
  private ValueReadback<ADC> piraniReadback;
  private ValueReadback<ADC> magnetronReadback;
  private ValueReadback<ADC> spareMon1;
  private ValueReadback<ADC> spareMon2;
  private ValueReadback<ADC> spareMon3;
  private ValueReadback<ADC> spareMon4;
  private ValueReadback<ADC> currentReadback1;
  private ValueReadback<ADC> currentReadback2;
  private ValueReadback<ADC> currentReadback3;
  private ValueReadback<ADC> currentReadback4;
  private ValueReadback<ADC> voltageReadback1;
  private ValueReadback<ADC> voltageReadback2;
  private ValueReadback<ADC> voltageReadback3;
  private ValueReadback<ADC> voltageReadback4;

  private ValueRequest<TurboControl> turboControl;
  private ValueReadback<TurboReadbacks> turboReadbacks;

  public SaintCommsImpl() {
    super(SaintComms.ID);
  }

  <T> ValueReadback<T> inBlock(Class<T> type, SaintCommandAddress address) {
    ValueReadback<T> readback = new ValueImpl<>(type, address, null);
    valueReadbacks.add(readback);
    return readback;
  }

  <T> ValueRequest<T> outBlock(Class<T> type, SaintCommandAddress address) {
    ValueRequest<T> request = new ValueImpl<>(type, null, address);
    valueRequests.add(request);
    return request;
  }

  <T> Value<T> inOutBlock(Class<T> type, SaintCommandAddress actual, SaintCommandAddress request) {
    Value<T> value = new ValueImpl<>(type, actual, request);
    values.add(value);
    return value;
  }

  @Activate
  void activate(SaintCommsConfiguration configuration) throws IOException {
    configure(configuration);

    ledStatus = inOutBlock(LEDStatus.class, LED_PORT, LED_LAT);
    vacuumStatus = inOutBlock(VacuumControl.class, VACUUM_PORT, VACUUM_LAT);
    highVoltageStatus = inOutBlock(HighVoltageStatus.class, HV_PORT, HV_LAT);
    motorStatus = inOutBlock(MotorStatus.class, MOTOR_PORT, MOTOR_LAT);
    vacuumReadback = inOutBlock(VacuumReadback.class, VACUUM_RB_PORT, VACUUM_RB_LAT);
    highVoltageReadback = inOutBlock(HighVoltageReadback.class, HV_RB_PORT, HV_RB_LAT);

    highVoltageDAC1 = outBlock(I2C.class, HV_DAC_1);
    highVoltageDAC1 = outBlock(I2C.class, HV_DAC_2);
    highVoltageDAC1 = outBlock(I2C.class, HV_DAC_3);
    highVoltageDAC1 = outBlock(I2C.class, HV_DAC_4);
    cmosRef = outBlock(I2C.class, CMOS_REF);
    laserDetectRef = outBlock(I2C.class, LASER_DETECT_REF);

    piraniReadback = inBlock(ADC.class, PIRANI_READBACK_ADC);
    magnetronReadback = inBlock(ADC.class, MAGNETRON_READBACK_ADC);

    spareMon1 = inBlock(ADC.class, SPARE_MON_1_ADC);
    spareMon2 = inBlock(ADC.class, SPARE_MON_2_ADC);
    spareMon3 = inBlock(ADC.class, SPARE_MON_3_ADC);
    spareMon4 = inBlock(ADC.class, SPARE_MON_4_ADC);

    currentReadback1 = inBlock(ADC.class, CURRENT_READBACK_1_ADC);
    currentReadback2 = inBlock(ADC.class, CURRENT_READBACK_2_ADC);
    currentReadback3 = inBlock(ADC.class, CURRENT_READBACK_3_ADC);
    currentReadback4 = inBlock(ADC.class, CURRENT_READBACK_4_ADC);
    voltageReadback1 = inBlock(ADC.class, VOLTAGE_READBACK_1_ADC);
    voltageReadback2 = inBlock(ADC.class, VOLTAGE_READBACK_2_ADC);
    voltageReadback3 = inBlock(ADC.class, VOLTAGE_READBACK_3_ADC);
    voltageReadback4 = inBlock(ADC.class, VOLTAGE_READBACK_4_ADC);

    turboControl = outBlock(TurboControl.class, TURBO_CONTROL);
    turboReadbacks = inBlock(TurboReadbacks.class, TURBO_READBACKS);
  }

  @Modified
  void configure(SaintCommsConfiguration configuration) throws IOException {
    port = comms.getPort(configuration.serialPort());
    setComms(port);
  }

  @Deactivate
  void deactivate() throws IOException {
    unsetComms();
  }

  @Override
  public Stream<Value<?>> values() {
    return values.stream();
  }

  @Override
  public Stream<ValueRequest<?>> valueRequests() {
    return valueRequests.stream();
  }

  @Override
  public Stream<ValueReadback<?>> valueReadbacks() {
    return valueReadbacks.stream();
  }

  @Override
  public Value<LEDStatus> led() {
    return ledStatus;
  }

  @Override
  public Value<VacuumControl> vacuum() {
    return vacuumStatus;
  }

  @Override
  public ValueRequest<HighVoltageStatus> highVoltage() {
    return highVoltageStatus;
  }

  @Override
  protected void checkComms() {
    ping();
  }

  private void ping() {
    useChannel(
        channel -> executeSaintCommand(PING, NULL, channel, new byte[NULL.getBytes().length]));
  }

  private byte[] executeSaintCommand(
      SaintCommandType command,
      SaintCommandAddress address,
      ByteChannel channel,
      byte[] output) {
    ByteBuffer message_buffer = ByteBuffer.allocate(MESSAGE_SIZE);

    int addressSize = address.getSize();
    byte[] addressBytes = address.getBytes();
    byte[] inputBytes = new byte[addressSize];

    for (int i = 0; i < addressSize; i++) {
      // output to buffer
      message_buffer.clear();

      message_buffer.put(command.getByte());
      message_buffer.put(addressBytes[i]);
      message_buffer.put((byte) 0);
      message_buffer.put(output[i]);

      try {
        message_buffer.flip();
        channel.write(message_buffer);
      } catch (IOException e) {
        throw setFault(new CommsException("Problem dispatching command"));
      }

      // input from buffer
      message_buffer.clear();

      try {
        if (channel.read(message_buffer) != message_buffer.limit()) {
          throw setFault(new CommsException("Response too short " + message_buffer.limit()));
        }
        message_buffer.flip();
      } catch (IOException e) {
        throw setFault(new CommsException("Problem receiving command response"));
      }

      message_buffer.get();
      message_buffer.get();
      message_buffer.get();
      inputBytes[i] = message_buffer.get();
    }

    return inputBytes;
  }
}
