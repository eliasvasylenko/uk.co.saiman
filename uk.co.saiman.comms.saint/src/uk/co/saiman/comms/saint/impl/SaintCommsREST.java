package uk.co.saiman.comms.saint.impl;

import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.empty;
import static java.util.stream.Stream.of;
import static uk.co.saiman.comms.rest.CommsRESTAction.Behaviour.MODIFIES_OUTPUT_DATA;
import static uk.co.saiman.comms.rest.CommsRESTAction.Behaviour.POLLABLE;
import static uk.co.saiman.comms.rest.CommsRESTAction.Behaviour.RECEIVES_INPUT_DATA;
import static uk.co.saiman.comms.rest.CommsRESTAction.Behaviour.SENDS_OUTPUT_DATA;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import osgi.enroute.dto.api.DTOs;
import uk.co.saiman.comms.rest.CommsREST;
import uk.co.saiman.comms.rest.CommsRESTAction;
import uk.co.saiman.comms.rest.CommsRESTEntry;
import uk.co.saiman.comms.saint.SaintComms;
import uk.co.saiman.comms.saint.Value;
import uk.co.saiman.comms.saint.ValueReadback;
import uk.co.saiman.comms.saint.ValueRequest;

public class SaintCommsREST implements CommsREST {
	private static final String GET_ACTUAL_VALUE = "getActualValue";
	private static final String SET_REQUESTED_VALUE = "setRequestedValue";
	private static final String GET_REQUESTED_VALUE = "getRequestedValue";

	private final SaintComms comms;
	private final DTOs dtos;
	private final Map<String, CommsRESTEntry> entries;

	public SaintCommsREST(SaintComms comms, DTOs dtos) {
		this.comms = comms;
		this.dtos = dtos;
		this.entries = new LinkedHashMap<>();

		comms.values().forEach(this::createItem);
		comms.valueReadbacks().forEach(r -> createItem(r, null));
		comms.valueRequests().forEach(r -> createItem(null, r));
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
		return entries.values().stream();
	}

	private <T> void createItem(Value<T> value) {
		createItem(value, value);
	}

	private <T> void createItem(ValueReadback<T> readback, ValueRequest<T> request) {
		CommsRESTEntry item = new CommsRESTEntry() {
			private final Map<String, Object> inputData = new HashMap<>();
			private final Map<String, Object> outputData = new HashMap<>();

			@Override
			public String getID() {
				return null; // TODO
			}

			@Override
			public Stream<CommsRESTAction> getActions() {
				return concat(
						readback != null ? of(getActualValueAction()) : empty(),
						request != null ? of(setRequestedValueAction(), getRequestedValueAction()) : empty());
			}

			private CommsRESTAction getActualValueAction() {
				return new CommsRESTAction() {
					@Override
					public boolean hasBehaviour(Behaviour behaviour) {
						return behaviour == POLLABLE || behaviour == RECEIVES_INPUT_DATA;
					}

					@Override
					public void invoke() throws Exception {
						inputData.clear();
						inputData.putAll(dtos.asMap(readback.getActual()));
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
					public void invoke() throws Exception {
						request.request(dtos.convert(outputData).to(request.getType()));
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
					public void invoke() throws Exception {
						outputData.clear();
						outputData.putAll(dtos.asMap(request.getRequested()));
					}

					@Override
					public String getID() {
						return GET_REQUESTED_VALUE;
					}
				};
			}

			@Override
			public Map<String, Object> getInputData() {
				return new HashMap<>(inputData);
			}

			@Override
			public Map<String, Object> getOutputData() {
				return new HashMap<>(outputData);
			}
		};
		entries.put(item.getID(), item);
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
		// TODO Auto-generated method stub

	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}
}
