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
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import osgi.enroute.dto.api.DTOs;
import uk.co.saiman.comms.CommsException;
import uk.co.saiman.comms.rest.CommsRESTAction;
import uk.co.saiman.comms.rest.CommsRESTEntry;
import uk.co.saiman.comms.rest.SimpleCommsREST;
import uk.co.saiman.comms.saint.SaintComms;
import uk.co.saiman.comms.saint.Value;
import uk.co.saiman.comms.saint.ValueReadback;
import uk.co.saiman.comms.saint.ValueRequest;

public class SaintCommsREST extends SimpleCommsREST<SaintComms> {
	private static final String GET_ACTUAL_VALUE = "getActual";
	private static final String SET_REQUESTED_VALUE = "setRequested";
	private static final String GET_REQUESTED_VALUE = "getRequested";

	private final DTOs dtos;
	private final Map<String, SAINTCommsRESTEntry> entries;

	private final CommsRESTAction getActual;
	private final CommsRESTAction setRequested;
	private final CommsRESTAction getRequested;

	public SaintCommsREST(SaintComms comms, DTOs dtos) {
		super(comms);

		this.dtos = dtos;
		this.entries = new LinkedHashMap<>();

		/*
		 * It's a bit naff to use reflection for this, but it keeps the API tidy and
		 * makes evolution easier so sue me
		 */

		Set<SAINTCommsRESTEntry> entries = new TreeSet<>(
				Comparator.comparing(
						e -> 0xFF & (e.request != null
								? e.request.getRequestedValueAddress()
								: e.readback.getActualValueAddress()).getBytes()[0]));

		try {
			for (Method method : SaintComms.class.getDeclaredMethods()) {
				String name = method.getName();

				if (method.getReturnType() == Value.class) {
					Value<?> value = (Value<?>) method.invoke(comms);
					entries.add(new SAINTCommsRESTEntry(name, value, value));

				} else if (method.getReturnType() == ValueReadback.class) {
					entries.add(new SAINTCommsRESTEntry(name, (ValueReadback<?>) method.invoke(comms), null));

				} else if (method.getReturnType() == ValueRequest.class) {
					entries.add(new SAINTCommsRESTEntry(name, null, (ValueRequest<?>) method.invoke(comms)));
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new CommsException("Problem initialising REST interface for " + comms.getName(), e);
		}

		entries.forEach(e -> this.entries.put(e.getID(), e));

		getActual = new CommsRESTAction() {
			@Override
			public boolean hasBehaviour(Behaviour behaviour) {
				return behaviour == POLLABLE || behaviour == RECEIVES_INPUT_DATA;
			}

			@Override
			public void invoke(String entry) throws Exception {
				SaintCommsREST.this.entries.get(entry).getActualValue();
			}

			@Override
			public String getID() {
				return GET_ACTUAL_VALUE;
			}
		};
		setRequested = new CommsRESTAction() {
			@Override
			public boolean hasBehaviour(Behaviour behaviour) {
				return behaviour == SENDS_OUTPUT_DATA;
			}

			@Override
			public void invoke(String entry) throws Exception {
				SaintCommsREST.this.entries.get(entry).setRequestedValue();
			}

			@Override
			public String getID() {
				return SET_REQUESTED_VALUE;
			}
		};
		getRequested = new CommsRESTAction() {
			@Override
			public boolean hasBehaviour(Behaviour behaviour) {
				return behaviour == MODIFIES_OUTPUT_DATA;
			}

			@Override
			public void invoke(String entry) throws Exception {
				SaintCommsREST.this.entries.get(entry).getRequestedValue();
			}

			@Override
			public String getID() {
				return GET_REQUESTED_VALUE;
			}
		};
	}

	@Override
	public Stream<Class<? extends Enum<?>>> getEnums() {
		return Stream.empty();
	}

	@Override
	public Stream<CommsRESTEntry> getEntries() {
		return upcastStream(entries.values().stream());
	}

	@Override
	public Stream<CommsRESTAction> getActions() {
		return of(getRequested, setRequested, getActual);
	}

	@Override
	public String getLocalisedText(String key, Locale locale) {
		return key;
	}

	@Override
	public void open() {
		super.open();
		entries.values().stream().filter(e -> e.request != null).forEach(
				SAINTCommsRESTEntry::getRequestedValue);
	}

	private class SAINTCommsRESTEntry implements CommsRESTEntry {
		private final Map<String, Object> inputData = new HashMap<>();
		private final Map<String, Object> outputData = new HashMap<>();

		private final String id;
		private final ValueReadback<?> readback;
		private final ValueRequest<?> request;

		public SAINTCommsRESTEntry(String name, ValueReadback<?> readback, ValueRequest<?> request) {
			this.id = name;
			this.readback = readback;
			this.request = request;
		}

		@Override
		public String getID() {
			return id;
		}

		@Override
		public Stream<String> getActions() {
			return concat(
					request != null ? of(SET_REQUESTED_VALUE, GET_REQUESTED_VALUE) : empty(),
					readback != null ? of(GET_ACTUAL_VALUE) : empty());
		}

		@Override
		public Map<String, Object> getInputData() {
			return new HashMap<>(inputData);
		}

		@Override
		public Map<String, Object> getOutputData() {
			return outputData;
		}

		public void setRequestedValue() {
			setRequestedValue(request);
		}

		private <T> void setRequestedValue(ValueRequest<T> request) {
			T value;
			try {
				value = dtos.convert(outputData).to(request.getType());
			} catch (Exception e) {
				throw new CommsException(
						"Cannot convert output data map to " + request.getType().getSimpleName(),
						e);
			}
			request.request(value);
		}

		public void getRequestedValue() {
			Object requested = request.getRequested();
			Map<String, Object> outputData;
			try {
				outputData = dtos.asMap(requested);
			} catch (Exception e) {
				throw new CommsException("Cannot convert " + requested + " to map", e);
			}
			this.outputData.clear();
			this.outputData.putAll(outputData);
		}

		public void getActualValue() {
			Object actual = readback.getActual();
			Map<String, Object> inputData;
			try {
				inputData = dtos.asMap(actual);
			} catch (Exception e) {
				throw new CommsException("Cannot convert " + actual + " to map", e);
			}
			this.inputData.clear();
			this.inputData.putAll(inputData);
		}
	}
}
