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

import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.empty;
import static java.util.stream.Stream.of;
import static uk.co.saiman.comms.rest.CommsRESTAction.Behaviour.MODIFIES_OUTPUT_DATA;
import static uk.co.saiman.comms.rest.CommsRESTAction.Behaviour.POLLABLE;
import static uk.co.saiman.comms.rest.CommsRESTAction.Behaviour.RECEIVES_INPUT_DATA;
import static uk.co.saiman.comms.rest.CommsRESTAction.Behaviour.SENDS_OUTPUT_DATA;
import static uk.co.strangeskies.collection.stream.StreamUtilities.upcastStream;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import osgi.enroute.dto.api.DTOs;
import uk.co.saiman.comms.CommsException;
import uk.co.saiman.comms.rest.CommsREST;
import uk.co.saiman.comms.rest.CommsRESTAction;
import uk.co.saiman.comms.rest.CommsRESTEntry;
import uk.co.saiman.comms.saint.SaintComms;
import uk.co.saiman.comms.saint.Value;
import uk.co.saiman.comms.saint.ValueReadback;
import uk.co.saiman.comms.saint.ValueRequest;

public class SaintCommsREST implements CommsREST {
	private class CommsRESTEntryImpl implements CommsRESTEntry {
		private final Map<String, Object> inputData = new HashMap<>();
		private final Map<String, Object> outputData = new HashMap<>();

		private final String id;
		private final ValueReadback<?> readback;
		private final ValueRequest<?> request;

		public CommsRESTEntryImpl(String name, ValueReadback<?> readback, ValueRequest<?> request) {
			this.id = name;
			this.readback = readback;
			this.request = request;

			entries.put(id, this);
		}

		@Override
		public String getID() {
			return id;
		}

		@Override
		public Stream<String> getActions() {
			return concat(
					readback != null ? of(GET_ACTUAL_VALUE) : empty(),
					request != null ? of(SET_REQUESTED_VALUE, GET_REQUESTED_VALUE) : empty());
		}

		@Override
		public Map<String, Object> getInputData() {
			return new HashMap<>(inputData);
		}

		@Override
		public Map<String, Object> getOutputData() {
			return new HashMap<>(outputData);
		}

		public void getActualValue() throws Exception {
			inputData.clear();
			inputData.putAll(dtos.asMap(readback.getActual()));
		}

		public void setRequestedValue() throws Exception {
			setRequestedValue(request);
		}

		private <T> void setRequestedValue(ValueRequest<T> request) throws Exception {
			request.request(dtos.convert(outputData).to(request.getType()));
		}

		public void getRequestedValue() throws Exception {
			outputData.clear();
			outputData.putAll(dtos.asMap(request.getRequested()));
		}
	}

	private static final String GET_ACTUAL_VALUE = "getActual";
	private static final String SET_REQUESTED_VALUE = "setRequested";
	private static final String GET_REQUESTED_VALUE = "getRequested";

	private final SaintComms comms;
	private final DTOs dtos;
	private final Map<String, CommsRESTEntryImpl> entries;

	public SaintCommsREST(SaintComms comms, DTOs dtos) {
		this.comms = comms;
		this.dtos = dtos;
		this.entries = new LinkedHashMap<>();

		/*
		 * It's a bit naff to use reflection for this, but it keeps the API tidy and
		 * makes evolution easier so sue me
		 */

		try {
			for (Method method : SaintComms.class.getDeclaredMethods()) {
				String name = method.getName();

				if (method.getReturnType() == Value.class) {
					Value<?> value = (Value<?>) method.invoke(comms);
					new CommsRESTEntryImpl(name, value, value);

				} else if (method.getReturnType() == ValueReadback.class) {
					new CommsRESTEntryImpl(name, (ValueReadback<?>) method.invoke(comms), null);

				} else if (method.getReturnType() == ValueRequest.class) {
					new CommsRESTEntryImpl(name, null, (ValueRequest<?>) method.invoke(comms));
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new CommsException("Problem initialising REST interface for " + comms.getName(), e);
		}
	}

	@Override
	public String getID() {
		return (comms.getName() + "-" + comms.getPort().getName()).replace(' ', '-').replace('/', '-');
	}

	@Override
	public String getName() {
		return comms.getName() + " " + comms.getPort().getName();
	}

	@Override
	public Stream<CommsRESTEntry> getEntries() {
		return upcastStream(entries.values().stream());
	}

	@Override
	public Stream<CommsRESTAction> getActions() {
		return of(getActualValueAction(), setRequestedValueAction(), getRequestedValueAction());
	}

	private CommsRESTAction getActualValueAction() {
		return new CommsRESTAction() {
			@Override
			public boolean hasBehaviour(Behaviour behaviour) {
				return behaviour == POLLABLE || behaviour == RECEIVES_INPUT_DATA;
			}

			@Override
			public void invoke(String entry) throws Exception {
				entries.get(entry).getActualValue();
			}

			@Override
			public String getID() {
				return GET_ACTUAL_VALUE;
			}
		};
	}

	private CommsRESTAction setRequestedValueAction() {
		return new CommsRESTAction() {
			@Override
			public boolean hasBehaviour(Behaviour behaviour) {
				return behaviour == SENDS_OUTPUT_DATA;
			}

			@Override
			public void invoke(String entry) throws Exception {
				entries.get(entry).setRequestedValue();
			}

			@Override
			public String getID() {
				return SET_REQUESTED_VALUE;
			}
		};
	}

	private CommsRESTAction getRequestedValueAction() {
		return new CommsRESTAction() {
			@Override
			public boolean hasBehaviour(Behaviour behaviour) {
				return behaviour == MODIFIES_OUTPUT_DATA;
			}

			@Override
			public void invoke(String entry) throws Exception {
				entries.get(entry).getRequestedValue();
			}

			@Override
			public String getID() {
				return GET_REQUESTED_VALUE;
			}
		};
	}

	@Override
	public String getLocalisedText(String key, Locale locale) {
		return key;
	}

	@Override
	public String getStatus() {
		return comms.status().get().toString();
	}

	@Override
	public String getPort() {
		return comms.getPort().getName();
	}

	@Override
	public Optional<String> getFaultText() {
		return comms.fault().map(f -> f.getMessage());
	}

	@Override
	public void open() {
		comms.open();
	}

	@Override
	public void reset() {
		comms.reset();
	}
}
