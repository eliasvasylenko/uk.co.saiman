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

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A simple immutable class defining named addresses for pushing and requesting
 * bytes to and from a {@link Comms comms channel}.
 * 
 * @author Elias N Vasylenko
 * @param <T>
 *          The command identifier type. This may typically consist of an
 *          address and an operation type.
 */
public abstract class CommandSetImpl<T> implements CommandSet<T> {
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

	private Comms comms;
	private CommsChannel channel;

	/**
	 * Initialize an empty address space.
	 */
	public CommandSetImpl(String name, Class<T> idClass) {
		this.name = name;
		this.idClass = idClass;
		this.commands = new HashMap<>();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<T> getCommandIdClass() {
		return idClass;
	}

	protected abstract void checkComms();

	private synchronized void performCheck() {
		try {
			if (comms.status().get() == FAULT) {
				comms.reset();
				open();
			}
			checkComms();
		} catch (CommsException e) {
			comms.setFault(e);
		} catch (Exception e) {
			comms.setFault(new CommsException("Problem checking comms", e));
		}
	}

	protected synchronized void unsetComms() throws IOException {
		close();
		this.comms = null;
	}

	protected synchronized void setComms(Comms comms) throws IOException {
		boolean wasOpen = close();
		this.comms = comms;
		if (wasOpen && open()) {
			performCheck();
		}
	}

	@Override
	public synchronized boolean isOpen() {
		return channel != null;
	}

	@Override
	public synchronized boolean open() {
		if (!isOpen()) {
			try {
				channel = comms.openChannel();
				return true;
			} catch (CommsException e) {
				comms.setFault(e);
				return false;
			}
		} else {
			if (comms.status().get() == FAULT) {
				performCheck();
			}
			return false;
		}
	}

	@Override
	public synchronized boolean close() {
		if (isOpen()) {
			try {
				channel.close();
			} catch (CommsException e) {
				comms.setFault(e);
			}
			channel = null;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Comms getChannel() {
		return comms;
	}

	protected synchronized <U> U useChannel(Function<ByteChannel, U> action) {
		if (open()) {
			performCheck();
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
