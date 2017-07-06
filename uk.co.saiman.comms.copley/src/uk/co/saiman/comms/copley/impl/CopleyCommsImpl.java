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
 * This file is part of uk.co.saiman.comms.copley.
 *
 * uk.co.saiman.comms.copley is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms.copley is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.copley.impl;

import static org.osgi.service.component.annotations.ConfigurationPolicy.REQUIRE;
import static org.osgi.service.component.annotations.ReferencePolicy.STATIC;
import static org.osgi.service.component.annotations.ReferencePolicyOption.GREEDY;
import static uk.co.saiman.comms.copley.CopleyOperation.COPY_VARIABLE;
import static uk.co.saiman.comms.copley.CopleyOperation.GET_VARIABLE;
import static uk.co.saiman.comms.copley.CopleyOperation.NO_OP;
import static uk.co.saiman.comms.copley.CopleyOperation.SET_VARIABLE;
import static uk.co.saiman.comms.copley.VariableBank.ACTIVE;
import static uk.co.saiman.comms.copley.VariableBank.DEFAULT;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.Arrays;
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
import uk.co.saiman.comms.copley.CopleyAxisInterface;
import uk.co.saiman.comms.copley.CopleyComms;
import uk.co.saiman.comms.copley.CopleyOperation;
import uk.co.saiman.comms.copley.CopleyVariable;
import uk.co.saiman.comms.copley.SingleMotorAxis;
import uk.co.saiman.comms.copley.VariableBank;
import uk.co.saiman.comms.copley.VariableIdentifier;
import uk.co.saiman.comms.copley.VariableInterface;
import uk.co.saiman.comms.copley.impl.CopleyCommsImpl.CopleyCommsConfiguration;
import uk.co.saiman.comms.serial.SerialPort;
import uk.co.saiman.comms.serial.SerialPorts;

@Designate(ocd = CopleyCommsConfiguration.class, factory = true)
@Component(
		name = CopleyCommsImpl.CONFIGURATION_PID,
		configurationPid = CopleyCommsImpl.CONFIGURATION_PID,
		configurationPolicy = REQUIRE)
public class CopleyCommsImpl<T extends Enum<T>> extends CommsImpl implements CopleyComms<T>, Comms {
	static final String CONFIGURATION_PID = "uk.co.saiman.comms.copley";

	static final int NODE_ID_MASK = 0x7F;
	static final int NODE_ID_MARK = 0x80;
	static final byte CHECKSUM = 0x5A;
	static final int WORD_SIZE = 2;

	@SuppressWarnings("javadoc")
	@ObjectClassDefinition(
			id = CONFIGURATION_PID,
			name = "Copley Comms Configuration",
			description = "The configuration for the underlying serial comms for a Copley motor control")
	public @interface CopleyCommsConfiguration {
		@AttributeDefinition(name = "Serial Port", description = "The serial port for comms")
		String serialPort();

		@AttributeDefinition(
				name = "Node Number",
				description = "The node number for multi-drop mode dispatch, or 0 for the directly connected node")
		int node()

		default 0;

		@AttributeDefinition(name = "Axes", description = "The names for the expected axes")
		Class<? extends Enum<?>> axes() default SingleMotorAxis.class;
	}

	@Reference(policy = STATIC, policyOption = GREEDY)
	SerialPorts comms;
	private SerialPort port;
	private int nodeID;
	private boolean nodeIDValid;
	private Class<T> axisClass;

	@Reference
	ByteConverters converters;

	private class VariableInterfaceImpl<U> implements VariableInterface<T, U> {
		private final CopleyVariable variable;
		private final Class<U> variableClass;
		private final CopleyAxisInterface<T> axis;

		public VariableInterfaceImpl(CopleyVariable variable, Class<U> variableClass, T axis) {
			this.variable = variable;
			this.variableClass = variableClass;
			this.axis = CopleyCommsImpl.this.getAxis(axis);
		}

		@Override
		public CopleyAxisInterface<T> getAxis() {
			return axis;
		}

		@Override
		public U getActive() {
			return invokeGetCommand(variable, ACTIVE);
		}

		@Override
		public void setActive(U value) {
			invokeSetCommand(value, variable, ACTIVE);
		}

		@Override
		public U getDefault() {
			return invokeGetCommand(variable, DEFAULT);
		}

		@Override
		public void setDefault(U value) {
			invokeSetCommand(value, variable, DEFAULT);
		}

		@Override
		public void loadDefault() {
			invokeCopyCommand(variable, ACTIVE);
		}

		@Override
		public void saveDefault() {
			invokeCopyCommand(variable, DEFAULT);
		}

		private VariableIdentifier getVariableID(CopleyVariable variable, T axis, VariableBank bank) {
			VariableIdentifier identifier = new VariableIdentifier();
			identifier.axis = (byte) axis.ordinal();
			identifier.variableID = (byte) variable.getCode();
			identifier.bank = bank.getBit();
			return identifier;
		}

		private byte[] concat(byte[] left, byte[] right) {
			byte[] bytes = new byte[left.length + right.length];
			System.arraycopy(left, 0, bytes, 0, left.length);
			System.arraycopy(right, 0, bytes, left.length, right.length);
			return bytes;
		}

		private U invokeGetCommand(CopleyVariable variable, VariableBank bank) {
			VariableIdentifier variableID = getVariableID(variable, axis.getID(), bank);

			byte[] outputBytes = converters.getConverter(VariableIdentifier.class).toBytes(variableID);

			byte[] inputBytes = executeCopleyCommand(GET_VARIABLE, outputBytes);

			return converters.getConverter(variableClass).fromBytes(inputBytes);
		}

		private void invokeCopyCommand(CopleyVariable variable, VariableBank bank) {
			VariableIdentifier variableID = getVariableID(variable, axis.getID(), bank);

			byte[] outputBytes = converters.getConverter(VariableIdentifier.class).toBytes(variableID);

			executeCopleyCommand(COPY_VARIABLE, outputBytes);
		}

		private void invokeSetCommand(U output, CopleyVariable variable, VariableBank bank) {
			VariableIdentifier variableID = getVariableID(variable, axis.getID(), bank);

			byte[] outputBytes = concat(
					converters.getConverter(VariableIdentifier.class).toBytes(variableID),
					converters.getConverter(variableClass).toBytes(output));

			executeCopleyCommand(SET_VARIABLE, outputBytes);
		}
	}

	public CopleyCommsImpl() {
		super(CopleyComms.ID);
	}

	@Activate
	void activate(CopleyCommsConfiguration configuration) throws IOException {
		configure(configuration);

		// addVariable(ACTUAL_POSITION, Int16.class);
	}

	@SuppressWarnings("unchecked")
	@Modified
	void configure(CopleyCommsConfiguration configuration) throws IOException {
		port = comms.getPort(configuration.serialPort());
		nodeIDValid = (configuration.node() & NODE_ID_MASK) == configuration.node();
		nodeID = configuration.node();
		axisClass = (Class<T>) configuration.axes();
		setComms(port);
		checkNodeId();
	}

	private boolean checkNodeId() {
		if (!nodeIDValid)
			setFault(new CommsException("Invalid node id number " + nodeID));

		return nodeIDValid;
	}

	@Deactivate
	void deactivate() throws IOException {
		unsetComms();
	}

	@Override
	protected void checkComms() {
		if (checkNodeId())
			ping();
	}

	private void ping() {
		executeCopleyCommand(NO_OP, new byte[] {});
	}

	@Override
	public Class<T> getAxisClass() {
		return axisClass;
	}

	@Override
	public Stream<CopleyAxisInterface<T>> getAxes() {
		return Arrays.stream(getAxisClass().getEnumConstants()).map(this::getAxis);
	}

	@Override
	public CopleyAxisInterface<T> getAxis(T axis) {
		if (axis.getDeclaringClass() != getAxisClass())
			throw new CopleyCommsException(
					"Unexpected requested axis class " + axis.getDeclaringClass() + " for configured class "
							+ axisClass);

		return new CopleyAxisInterface<T>() {
			@Override
			public T getID() {
				return axis;
			}
		};
	}

	private byte[] executeCopleyCommand(CopleyOperation operation, byte[] output) {
		return useChannel(channel -> {
			sendCopleyCommand(operation, channel, output);
			return receiveCopleyCommand(channel);
		});
	}

	private void sendCopleyCommand(CopleyOperation operation, ByteChannel channel, byte[] output) {
		byte id = (byte) (nodeID == 0 ? nodeID : (nodeID | NODE_ID_MARK));
		byte size = (byte) (output.length / WORD_SIZE);
		byte opCode = operation.getCode();
		byte checksum = (byte) (CHECKSUM ^ id ^ size ^ opCode);
		for (byte outputByte : output)
			checksum ^= outputByte;

		ByteBuffer message_buffer = ByteBuffer.allocate(HEADER_SIZE + output.length);
		message_buffer.put(id);
		message_buffer.put(checksum);
		message_buffer.put(size);
		message_buffer.put(opCode);
		message_buffer.put(output);

		try {
			message_buffer.flip();
			channel.write(message_buffer);
		} catch (IOException e) {
			throw setFault(new CommsException("Problem dispatching command"));
		}
	}

	private byte[] receiveCopleyCommand(ByteChannel channel) {
		ByteBuffer message_buffer = ByteBuffer.allocate(HEADER_SIZE);
		try {
			if (channel.read(message_buffer) != message_buffer.limit()) {
				throw setFault(new CommsException("Response too short " + message_buffer.limit()));
			}
			message_buffer.flip();
		} catch (IOException e) {
			throw setFault(new CommsException("Problem receiving command response"));
		}

		message_buffer.get(); // reserved
		byte checksum = message_buffer.get();
		int size = message_buffer.get() * WORD_SIZE;
		byte errorCode = message_buffer.get();

		message_buffer = ByteBuffer.allocate(size);
		try {
			if (channel.read(message_buffer) != message_buffer.limit()) {
				throw setFault(new CommsException("Response too short " + message_buffer.limit()));
			}
			message_buffer.flip();
		} catch (IOException e) {
			throw setFault(new CommsException("Problem receiving command response"));
		}

		byte[] input = new byte[size];
		message_buffer.get(input);

		return input;
	}
}
