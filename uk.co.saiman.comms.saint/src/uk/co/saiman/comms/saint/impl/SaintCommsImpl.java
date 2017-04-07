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

import static java.lang.Byte.BYTES;
import static java.lang.Long.reverse;
import static java.lang.Math.ceil;
import static java.nio.ByteBuffer.allocate;
import static org.osgi.service.component.annotations.ReferencePolicy.STATIC;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;
import static uk.co.saiman.comms.saint.InOutBlock.inOutBlock;
import static uk.co.saiman.comms.saint.OutBlock.outBlock;
import static uk.co.saiman.comms.saint.SaintCommandId.SaintCommandAddress.HV_DAC_1;
import static uk.co.saiman.comms.saint.SaintCommandId.SaintCommandAddress.HV_DAC_2;
import static uk.co.saiman.comms.saint.SaintCommandId.SaintCommandAddress.HV_DAC_3;
import static uk.co.saiman.comms.saint.SaintCommandId.SaintCommandAddress.HV_DAC_4;
import static uk.co.saiman.comms.saint.SaintCommandId.SaintCommandAddress.HV_LAT;
import static uk.co.saiman.comms.saint.SaintCommandId.SaintCommandAddress.HV_PORT;
import static uk.co.saiman.comms.saint.SaintCommandId.SaintCommandAddress.LED_LAT;
import static uk.co.saiman.comms.saint.SaintCommandId.SaintCommandAddress.LED_PORT;
import static uk.co.saiman.comms.saint.SaintCommandId.SaintCommandAddress.NULL;
import static uk.co.saiman.comms.saint.SaintCommandId.SaintCommandAddress.VACUUM_LAT;
import static uk.co.saiman.comms.saint.SaintCommandId.SaintCommandAddress.VACUUM_PORT;
import static uk.co.saiman.comms.saint.SaintCommandId.SaintCommandType.INPUT;
import static uk.co.saiman.comms.saint.SaintCommandId.SaintCommandType.OUTPUT;
import static uk.co.saiman.comms.saint.SaintCommandId.SaintCommandType.PING;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.comms.Command;
import uk.co.saiman.comms.CommandSet;
import uk.co.saiman.comms.CommandSetImpl;
import uk.co.saiman.comms.CommsException;
import uk.co.saiman.comms.NamedBits;
import uk.co.saiman.comms.NumberedBits;
import uk.co.saiman.comms.saint.HighVoltageBit;
import uk.co.saiman.comms.saint.InOutBlock;
import uk.co.saiman.comms.saint.OutBlock;
import uk.co.saiman.comms.saint.SaintCommandId;
import uk.co.saiman.comms.saint.SaintCommandId.SaintCommandAddress;
import uk.co.saiman.comms.saint.SaintCommandId.SaintCommandType;
import uk.co.saiman.comms.saint.SaintComms;
import uk.co.saiman.comms.saint.VacuumBit;
import uk.co.saiman.comms.saint.impl.SaintCommsImpl.SaintCommsConfiguration;
import uk.co.saiman.comms.serial.SerialPort;
import uk.co.saiman.comms.serial.SerialPorts;

@Designate(ocd = SaintCommsConfiguration.class, factory = true)
@Component(
		name = SaintCommsImpl.CONFIGURATION_PID,
		configurationPid = SaintCommsImpl.CONFIGURATION_PID)
public class SaintCommsImpl extends CommandSetImpl<SaintCommandId>
		implements SaintComms, CommandSet<SaintCommandId> {
	public static final String CONFIGURATION_PID = "uk.co.saiman.comms.saint";
	public static final int MESSAGE_SIZE = 4;

	private static final String LED_PREFIX = "STATUS_LED_";
	private static final int LED_COUNT = 8;

	private static final int HV_DAC_PAD = 4;
	private static final int HV_DAC_BITS = 12;

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
	SerialPorts comms;
	private SerialPort port;

	private InOutBlock<NumberedBits> ledStatus;
	private InOutBlock<NamedBits<VacuumBit>> vacuumStatus;
	private InOutBlock<NamedBits<HighVoltageBit>> highVoltageStatus;

	private OutBlock<Integer> highVoltageDAC1;
	private OutBlock<Integer> highVoltageDAC2;
	private OutBlock<Integer> highVoltageDAC3;
	private OutBlock<Integer> highVoltageDAC4;

	public SaintCommsImpl() {
		super(SaintComms.ID, SaintCommandId.class);
	}

	@Activate
	void activate(SaintCommsConfiguration configuration) throws IOException {
		configure(configuration);

		ledStatus = inOutBlock(
				addOutput(LED_LAT, () -> new NumberedBits(LED_PREFIX, LED_COUNT), NumberedBits::getBytes),
				addInput(LED_LAT, b -> new NumberedBits(LED_PREFIX, LED_COUNT, b)),
				addInput(LED_PORT, b -> new NumberedBits(LED_PREFIX, LED_COUNT, b)));

		vacuumStatus = inOutBlock(
				addOutput(VACUUM_LAT, () -> new NamedBits<>(VacuumBit.class), NamedBits::getBytes),
				addInput(VACUUM_LAT, b -> new NamedBits<>(VacuumBit.class, b)),
				addInput(VACUUM_PORT, b -> new NamedBits<>(VacuumBit.class, b)));

		highVoltageStatus = inOutBlock(
				addOutput(HV_LAT, () -> new NamedBits<>(HighVoltageBit.class), NamedBits::getBytes),
				addInput(HV_LAT, b -> new NamedBits<>(HighVoltageBit.class, b)),
				addInput(HV_PORT, b -> new NamedBits<>(HighVoltageBit.class, b)));

		highVoltageDAC1 = outBlock(
				addOutput(HV_DAC_1, () -> 0, i -> intToBytes(i, HV_DAC_PAD, HV_DAC_BITS)),
				addInput(HV_DAC_1, b -> (int) bytesToInt(b, HV_DAC_PAD, HV_DAC_BITS)));
		highVoltageDAC2 = outBlock(
				addOutput(HV_DAC_2, () -> 0, i -> intToBytes(i, HV_DAC_PAD, HV_DAC_BITS)),
				addInput(HV_DAC_2, b -> (int) bytesToInt(b, HV_DAC_PAD, HV_DAC_BITS)));
		highVoltageDAC3 = outBlock(
				addOutput(HV_DAC_3, () -> 0, i -> intToBytes(i, HV_DAC_PAD, HV_DAC_BITS)),
				addInput(HV_DAC_3, b -> (int) bytesToInt(b, HV_DAC_PAD, HV_DAC_BITS)));
		highVoltageDAC4 = outBlock(
				addOutput(HV_DAC_4, () -> 0, i -> intToBytes(i, HV_DAC_PAD, HV_DAC_BITS)),
				addInput(HV_DAC_4, b -> (int) bytesToInt(b, HV_DAC_PAD, HV_DAC_BITS)));
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
	public InOutBlock<NumberedBits> led() {
		return ledStatus;
	}

	@Override
	public InOutBlock<NamedBits<VacuumBit>> vacuum() {
		return vacuumStatus;
	}

	@Override
	public OutBlock<NamedBits<HighVoltageBit>> highVoltage() {
		return highVoltageStatus;
	}

	private byte[] intToBytes(long integer, int bitPad, int bitCount) {
		int padBytes = (int) ceil(bitPad / (double) BYTES);
		int byteCount = (int) ceil((padBytes + bitCount) / (double) BYTES);

		byte[] bytes = allocate(8).putLong(reverse(integer)).array();
		byte[] result = new byte[byteCount];

		result[0] = (byte) (bytes[0] >> bitPad);
		for (int i = 1; i < byteCount; i++) {
			result[i] = (byte) (bytes[i - 1] << 4 | bytes[i] >> 4);
		}

		return result;
	}

	private long bytesToInt(byte[] bytes, int bitPad, int bitCount) {
		return reverse(bytes[0] << 4 | bytes[1] >> 4);
	}

	@Override
	protected void checkComms() {
		ping();
	}

	private void ping() {
		useChannel(
				channel -> executeSaintCommand(PING, NULL, channel, new byte[NULL.getBytes().length]));
	}

	private <T> Supplier<T> addInput(SaintCommandAddress address, Function<byte[], T> inputFunction) {
		Command<SaintCommandId, T, Void> inputCommand = addCommand(
				new SaintCommandId(INPUT, address),
				(output, channel) -> {
					byte[] outputBytes = new byte[address.getBytes().length];
					byte[] inputBytes = executeSaintCommand(INPUT, address, channel, outputBytes);
					return inputFunction.apply(inputBytes);
				},
				() -> null);
		return () -> inputCommand.invoke((Void) null);
	}

	private <T> Consumer<T> addOutput(
			SaintCommandAddress address,
			Supplier<T> prototype,
			Function<T, byte[]> outputFunction) {
		Command<SaintCommandId, Void, T> outputCommand = addCommand(
				new SaintCommandId(OUTPUT, address),
				(output, channel) -> {
					byte[] outputBytes = outputFunction.apply(output);
					executeSaintCommand(OUTPUT, address, channel, outputBytes);
					return null;
				},
				prototype);
		return outputCommand::invoke;
	}

	private byte[] executeSaintCommand(
			SaintCommandType command,
			SaintCommandAddress address,
			ByteChannel channel,
			byte[] output) {
		ByteBuffer buffer;

		int addressSize = address.getSize();
		byte[] addressBytes = address.getBytes();
		buffer = ByteBuffer.allocate(MESSAGE_SIZE * addressSize);
		for (int i = 0; i < addressSize; i++) {
			buffer.put(command.getByte());
			buffer.put(addressBytes[i]);
			buffer.put((byte) 0);
			buffer.put(output[i]);
		}
		try {
			buffer.flip();
			channel.write(buffer);
		} catch (IOException e) {
			throw port.setFault(new CommsException("Problem dispatching command"));
		}

		int inputRead;
		byte[] inputBytes = new byte[addressSize];
		buffer = ByteBuffer.allocate(MESSAGE_SIZE * addressSize);
		try {
			inputRead = channel.read(buffer);
			buffer.flip();
		} catch (IOException e) {
			throw port.setFault(new CommsException("Problem receiving command response"));
		}
		if (inputRead != MESSAGE_SIZE * addressSize) {
			throw port.setFault(new CommsException("Response too short " + inputRead));
		}
		for (int i = 0; i < addressSize; i++) {
			inputBytes[i] = buffer.get();
			buffer.get();
			buffer.get();
			buffer.get();
		}

		return inputBytes;
	}
}
