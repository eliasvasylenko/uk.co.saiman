package uk.co.saiman.comms.copley.impl;

import static java.util.Collections.emptyMap;
import static java.util.stream.Stream.of;
import static uk.co.saiman.comms.rest.CommsRESTAction.Behaviour.MODIFIES_OUTPUT_DATA;
import static uk.co.saiman.comms.rest.CommsRESTAction.Behaviour.POLLABLE;
import static uk.co.saiman.comms.rest.CommsRESTAction.Behaviour.RECEIVES_INPUT_DATA;
import static uk.co.saiman.comms.rest.CommsRESTAction.Behaviour.SENDS_OUTPUT_DATA;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;

import osgi.enroute.dto.api.DTOs;
import uk.co.saiman.comms.copley.BankedVariable;
import uk.co.saiman.comms.copley.CopleyComms;
import uk.co.saiman.comms.copley.CopleyVariableID;
import uk.co.saiman.comms.copley.Variable;
import uk.co.saiman.comms.copley.WritableVariable;
import uk.co.saiman.comms.rest.CommsRESTAction;
import uk.co.saiman.comms.rest.CommsRESTEntry;
import uk.co.saiman.comms.rest.SimpleCommsREST;
import uk.co.strangeskies.collection.stream.StreamUtilities;

public class CopleyCommsREST extends SimpleCommsREST<CopleyComms<?>> {
	private static final String READ_VALUE = "readValue";
	private static final String WRITE_VALUE = "writeValue";
	private static final String SWITCH_BANK = "switchBank";

	public class VariableCommsRESTEntry implements CommsRESTEntry {
		private Variable<?, ?> variable;

		public VariableCommsRESTEntry(Variable<?, ?> variable) {
			this.variable = variable;
		}

		@Override
		public String getID() {
			return variable.getID().toString();
		}

		@Override
		public Stream<String> getActions() {
			return Stream.empty();
		}

		@Override
		public Map<String, Object> getInputData() {
			return emptyMap();
		}

		@Override
		public Map<String, Object> getOutputData() {
			return emptyMap();
		}

		public void readValue() {
			variable.get(axis);
		}

		public void writeValue() {
			((WritableVariable<?, ?>) variable).set(axis, value);
		}

		public void switchBank() {
			variable = ((BankedVariable<?, ?>) variable).switchBank();
		}
	}

	private final DTOs dtos;

	private final Map<String, VariableCommsRESTEntry> variableEntries;

	private final CommsRESTAction readValue;
	private final CommsRESTAction writeValue;
	private final CommsRESTAction switchBank;

	public CopleyCommsREST(CopleyComms<?> comms, DTOs dtos) {
		super(comms);
		this.dtos = dtos;

		this.variableEntries = new LinkedHashMap<>();
		Arrays.stream(CopleyVariableID.values()).map(comms::getVariable).forEach(
				variable -> variableEntries
						.put(variable.getID().toString(), new VariableCommsRESTEntry(variable)));

		readValue = new CommsRESTAction() {
			@Override
			public void invoke(String entry) throws Exception {
				variableEntries.get(entry).readValue();
			}

			@Override
			public boolean hasBehaviour(Behaviour behaviour) {
				return behaviour == RECEIVES_INPUT_DATA || behaviour == POLLABLE;
			}

			@Override
			public String getID() {
				return READ_VALUE;
			}
		};
		writeValue = new CommsRESTAction() {
			@Override
			public void invoke(String entry) throws Exception {
				variableEntries.get(entry).writeValue();
			}

			@Override
			public boolean hasBehaviour(Behaviour behaviour) {
				return behaviour == SENDS_OUTPUT_DATA;
			}

			@Override
			public String getID() {
				return WRITE_VALUE;
			}
		};
		switchBank = new CommsRESTAction() {
			@Override
			public void invoke(String entry) throws Exception {
				variableEntries.get(entry).switchBank();
			}

			@Override
			public boolean hasBehaviour(Behaviour behaviour) {
				return behaviour == RECEIVES_INPUT_DATA || behaviour == MODIFIES_OUTPUT_DATA;
			}

			@Override
			public String getID() {
				return SWITCH_BANK;
			}
		};
	}

	@Override
	public Stream<CommsRESTEntry> getEntries() {
		return StreamUtilities.upcastStream(variableEntries.values().stream());
	}

	@Override
	public Stream<CommsRESTAction> getActions() {
		return of(readValue, writeValue, switchBank);
	}

	@Override
	public String getLocalisedText(String key, Locale locale) {
		return key;
	}
}
