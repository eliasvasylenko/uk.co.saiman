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
 * This file is part of uk.co.saiman.comms.
 *
 * uk.co.saiman.comms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms;

import static uk.co.saiman.comms.Comms.CommsStatus.FAULT;
import static uk.co.saiman.comms.Comms.CommsStatus.OPEN;
import static uk.co.saiman.comms.Comms.CommsStatus.READY;

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import uk.co.strangeskies.observable.ObservableProperty;
import uk.co.strangeskies.observable.ObservableValue;

/**
 * A simple immutable class defining named addresses for pushing and requesting
 * bytes to and from a {@link CommsPort comms channel}.
 * 
 * @author Elias N Vasylenko
 * @param <T>
 *          The command identifier type. This may typically consist of an
 *          address and an operation type.
 */
public abstract class CommsImpl<T> implements Comms<T> {
	public class CommandImpl<I, O> implements Command<T, I, O> {
		private final T id;
		private final CommandFunction<I, O> definition;
		private final Supplier<O> prototype;

		protected CommandImpl(T id, CommandFunction<I, O> definition, Supplier<O> prototype) {
			this.id = id;
			this.definition = definition;
			this.prototype = prototype;
		}

		@Override
		public T getId() {
			return id;
		}

		@Override
		public I invoke(O argument) {
			return useChannel(channel -> {
				try {
					I lastInput = definition.execute(argument, channel);
					return lastInput;
				} catch (Exception e) {
					throw new CommsException("Problem transferring data for command " + id, e);
				}
			});
		}

		@Override
		public O prototype() {
			return prototype.get();
		}
	}

	private final String name;
	private final Class<T> idClass;
	private final Map<T, Command<T, ?, ?>> commands;

	private CommsPort comms;
	private CommsChannel channel;

	private final ObservableProperty<CommsStatus, CommsStatus> status;
	private CommsException lastFault;

	/**
	 * Initialize an empty address space.
	 */
	public CommsImpl(String name, Class<T> idClass) {
		this.name = name;
		this.idClass = idClass;
		this.commands = new HashMap<>();

		status = ObservableProperty.over(READY);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<T> getCommandIdClass() {
		return idClass;
	}

	@Override
	public ObservableValue<CommsStatus> status() {
		return status;
	}

	@Override
	public synchronized Optional<CommsException> fault() {
		return status.get() == FAULT ? Optional.of(lastFault) : Optional.empty();
	}

	protected synchronized CommsException setFault(CommsException commsException) {
		status.set(FAULT);
		this.lastFault = commsException;
		return commsException;
	}

	protected abstract void checkComms();

	protected synchronized void unsetComms() throws IOException {
		try {
			reset();
		} catch (Exception e) {}
		this.comms = null;
		setFault(new CommsException("No port configured"));
	}

	protected synchronized void setComms(CommsPort comms) throws IOException {
		try {
			reset();
		} catch (Exception e) {}
		this.comms = comms;
		status.set(READY);
	}

	@Override
	public synchronized void open() {
		switch (status().get()) {
		case OPEN:
			break;

		case FAULT:
			reset();
		case READY:
			try {
				channel = comms.openChannel();
				status.set(OPEN);
				checkComms();
			} catch (CommsException e) {
				throw setFault(e);
			} catch (Exception e) {
				setFault(new CommsException("Problem opening comms", e));
			}
		}
	}

	@Override
	public synchronized void reset() {
		switch (status().get()) {
		case READY:
			break;

		case FAULT:
		case OPEN:
			try {
				comms.close();
				status.set(READY);
			} catch (CommsException e) {
				throw setFault(e);
			} catch (Exception e) {
				setFault(new CommsException("Problem closing comms", e));
			}
			break;
		}
	}

	@Override
	public CommsPort getPort() {
		return comms;
	}

	protected synchronized <U> U useChannel(Function<ByteChannel, U> action) {
		switch (status().get()) {
		case OPEN:
			return action.apply(channel);

		case READY:
			throw new CommsException("Port is closed");

		case FAULT:
			throw fault().get();
		}

		return action.apply(channel);
	}

	protected <I, O> Command<T, I, O> addCommand(
			T id,
			CommandFunction<I, O> definition,
			Supplier<O> prototype) {
		Command<T, I, O> command = new CommandImpl<>(id, definition, prototype);
		commands.put(id, command);

		return command;
	}

	@Override
	public Stream<T> getCommands() {
		return commands.keySet().stream();
	}

	@Override
	public Command<T, ?, ?> getCommand(T id) {
		Command<T, ?, ?> command = commands.get(id);

		if (command == null) {
			throw new CommsException("Command undefined " + id);
		}

		return command;
	}
}
