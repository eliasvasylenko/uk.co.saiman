package uk.co.saiman.comms.saint.impl;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import uk.co.saiman.comms.CommsException;
import uk.co.saiman.comms.rest.CommsREST;
import uk.co.saiman.comms.rest.CommsRESTItem;
import uk.co.saiman.comms.saint.SaintCommandAddress;
import uk.co.saiman.comms.saint.SaintComms;

public class SaintCommsREST implements CommsREST {
	private final SaintComms comms;
	private final SaintProperties properties;

	public SaintCommsREST(SaintComms comms, SaintProperties properties) {
		this.comms = comms;
		this.properties = properties;
	}

	@Override
	public Stream<String> getItems() {
		return Arrays.stream(SaintCommandAddress.values()).map(Objects::toString);
	}

	@Override
	public CommsRESTItem getItem(String item) {
		return Arrays
				.stream(SaintCommandAddress.values())
				.filter(a -> a.toString().equals(item))
				.findAny()
				.map(this::createItem)
				.orElseThrow(() -> new CommsException(properties.cannotFindCommsCommand()));
	}

	private CommsRESTItem createItem(SaintCommandAddress address) {
		return new CommsRESTItem() {
			@Override
			public boolean isPollable() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Stream<String> getActions() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getInputAction() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Map<String, Object> getInputData() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getRefreshOutputAction() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getOutputAction() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Map<String, Object> getOutputData() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void invokeAction(String action) {
				// TODO Auto-generated method stub

			}
		};
	}

	@Override
	public String getText(String key, Locale locale) {
		return key;
	}
}
