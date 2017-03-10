package uk.co.saiman.comms.saint.impl;

import static org.osgi.service.component.annotations.ReferencePolicy.STATIC;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;
import static uk.co.saiman.comms.saint.InOutBlock.inOutBlock;
import static uk.co.saiman.comms.saint.SaintCommandId.SaintCommandAddress.HV_LAT;
import static uk.co.saiman.comms.saint.SaintCommandId.SaintCommandAddress.HV_PORT;
import static uk.co.saiman.comms.saint.SaintCommandId.SaintCommandAddress.LED_LAT;
import static uk.co.saiman.comms.saint.SaintCommandId.SaintCommandAddress.LED_PORT;
import static uk.co.saiman.comms.saint.SaintCommandId.SaintCommandAddress.VACUUM_LAT;
import static uk.co.saiman.comms.saint.SaintCommandId.SaintCommandAddress.VACUUM_PORT;
import static uk.co.saiman.comms.saint.SaintCommandId.SaintCommandType.INPUT;
import static uk.co.saiman.comms.saint.SaintCommandId.SaintCommandType.OUTPUT;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.comms.CommandDefinition;
import uk.co.saiman.comms.CommandSet;
import uk.co.saiman.comms.CommandSetImpl;
import uk.co.saiman.comms.saint.HighVoltageBits;
import uk.co.saiman.comms.saint.InOutBlock;
import uk.co.saiman.comms.saint.OutBlock;
import uk.co.saiman.comms.saint.SaintCommandId;
import uk.co.saiman.comms.saint.SaintCommandId.SaintCommandAddress;
import uk.co.saiman.comms.saint.SaintComms;
import uk.co.saiman.comms.saint.SaintInputPayload;
import uk.co.saiman.comms.saint.SaintOutputPayload;
import uk.co.saiman.comms.saint.StatusLedBits;
import uk.co.saiman.comms.saint.VacuumBits;
import uk.co.saiman.comms.saint.impl.SaintCommsImpl.SaintCommsConfiguration;
import uk.co.saiman.comms.serial.SerialComms;
import uk.co.saiman.comms.serial.SerialPort;

@Designate(ocd = SaintCommsConfiguration.class)
@Component(name = SaintCommsImpl.CONFIGURATION_PID, configurationPid = SaintCommsImpl.CONFIGURATION_PID)
public class SaintCommsImpl extends CommandSetImpl<SaintCommandId> implements SaintComms, CommandSet<SaintCommandId> {
	static final String CONFIGURATION_PID = "uk.co.saiman.comms.saint";

	@SuppressWarnings("javadoc")
	@ObjectClassDefinition(
			id = CONFIGURATION_PID,
			name = "SAINT Comms Configuration",
			description = "The configuration for the underlying serial comms for a SAINT instrument")
	public @interface SaintCommsConfiguration {
		@AttributeDefinition(name = "Serial Port", description = "The serial port for comms")
		String serialPort();
	}

	@Reference(policy = STATIC, policyOption = GREEDY)
	SerialComms comms;
	private SerialPort port;

	private InOutBlock<StatusLedBits> ledStatus;
	private InOutBlock<VacuumBits> vacuumStatus;
	private InOutBlock<HighVoltageBits> highVoltageStatus;

	private OutBlock<byte[]> highVoltageDAC1LSB;
	private OutBlock<byte[]> highVoltageDAC1MSB;
	private OutBlock<Integer> highVoltageDAC1;

	public SaintCommsImpl() {
		super(SaintComms.ID, SaintCommandId.class);
	}

	private <T> Supplier<T> addInput(SaintCommandAddress address, Class<T> inputClass, Function<Byte, T> inputFunction) {
		CommandDefinition<SaintCommandId, T, Void> inputCommand = addCommand(
				new SaintCommandId(INPUT, address),
				new SaintInputPayload<>(inputClass, inputFunction),
				SaintOutputPayload.voidOutput());
		return () -> inputCommand.invoke(null);
	}

	private <T> Consumer<T> addOutput(
			SaintCommandAddress address,
			Class<T> outputClass,
			Function<T, Byte> outputFunction) {
		CommandDefinition<SaintCommandId, Void, T> outputCommand = addCommand(
				new SaintCommandId(OUTPUT, address),
				SaintInputPayload.voidInput(),
				new SaintOutputPayload<>(outputClass, outputFunction));
		return outputCommand::invoke;
	}

	@Activate
	void activate(SaintCommsConfiguration configuration) {
		configure(configuration);
	}

	@Modified
	void configure(SaintCommsConfiguration configuration) {
		port = comms.getPort(configuration.serialPort());
		setChannel(port);

		ledStatus = inOutBlock(
				addOutput(LED_LAT, StatusLedBits.class, StatusLedBits::getByte),
				addInput(LED_LAT, StatusLedBits.class, StatusLedBits::new),
				addInput(LED_PORT, StatusLedBits.class, StatusLedBits::new));

		vacuumStatus = inOutBlock(
				addOutput(VACUUM_LAT, VacuumBits.class, VacuumBits::getByte),
				addInput(VACUUM_LAT, VacuumBits.class, VacuumBits::new),
				addInput(VACUUM_PORT, VacuumBits.class, VacuumBits::new));

		highVoltageStatus = inOutBlock(
				addOutput(HV_LAT, HighVoltageBits.class, HighVoltageBits::getByte),
				addInput(HV_LAT, HighVoltageBits.class, HighVoltageBits::new),
				addInput(HV_PORT, HighVoltageBits.class, HighVoltageBits::new));

		/*-
		highVoltageDAC1LSB = saintOutBlock(
				commandSpace,
				new SaintCommandId(OUTPUT, HV_DAC_1_LSB),
				new SaintCommandId(INPUT, HV_DAC_1_LSB),
				new CommandBytes<byte[]>(byte[]::clone, byte[]::clone, 1));
		highVoltageDAC1MSB = saintOutBlock(
				commandSpace,
				new SaintCommandId(OUTPUT, HV_DAC_1_MSB),
				new SaintCommandId(INPUT, HV_DAC_1_MSB),
				new CommandBytes<byte[]>(byte[]::clone, byte[]::clone, 1));
		highVoltageDAC1 = outBlock(data -> {
			byte[] bytes = allocate(4).putInt(reverse(data)).array();
			highVoltageDAC1LSB.request(new byte[] { (byte) (bytes[0] >> 4) });
			highVoltageDAC1MSB.request(new byte[] { (byte) (bytes[0] << 4 | bytes[1] >> 4) });
		}, () -> reverse(highVoltageDAC1LSB.getRequested()[0] << 4 | highVoltageDAC1MSB.getRequested()[0] >> 4));
		*/
	}

	@Override
	public InOutBlock<StatusLedBits> led() {
		return ledStatus;
	}

	@Override
	public InOutBlock<VacuumBits> vacuum() {
		return vacuumStatus;
	}

	@Override
	public OutBlock<HighVoltageBits> highVoltage() {
		return highVoltageStatus;
	}
}
