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

import java.util.Optional;
import java.util.stream.Stream;

import uk.co.strangeskies.observable.ObservableValue;

public interface Comms<T> {
	/**
	 * A description of the status of a comms channel.
	 * 
	 * @author Elias N Vasylenko
	 */
	enum CommsStatus {
		/**
		 * The byte channel is currently open. Subsequent invocations of
		 * {@link CommsPort#openChannel()} will fail until the previously opened
		 * byte channel is closed.
		 */
		OPEN,

		/**
		 * The byte channel is ready to be opened. This does not guarantee that a
		 * subsequent invocation of {@link CommsPort#openChannel()} will succeed, it
		 * simply indicates that it is expected to succeed. If invocation fails, the
		 * comms channel will enter the {@link #FAULT} state.
		 */
		READY,

		/**
		 * There is a problem with the comms channel. If a comms channel is in the
		 * {@link #FAULT} state, then invocation of {@link CommsPort#openChannel()}
		 * should either clear the fault or throw an exception describing the fault.
		 */
		FAULT
	}

	String getName();

	ObservableValue<CommsStatus> status();

	/**
	 * @return if the comms object is in a fault state an optional containing the
	 *         cause of the fault, otherwise an empty optional
	 */
	Optional<CommsException> fault();

	void open();

	void reset();

	Stream<T> getCommands();

	Command<T, ?, ?> getCommand(T id);

	CommsPort getPort();
}
