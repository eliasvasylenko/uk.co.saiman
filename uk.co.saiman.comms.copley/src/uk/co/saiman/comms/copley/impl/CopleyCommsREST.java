package uk.co.saiman.comms.copley.impl;

import java.util.Locale;
import java.util.stream.Stream;

import osgi.enroute.dto.api.DTOs;
import uk.co.saiman.comms.copley.CopleyComms;
import uk.co.saiman.comms.rest.CommsRESTAction;
import uk.co.saiman.comms.rest.CommsRESTEntry;
import uk.co.saiman.comms.rest.SimpleCommsREST;

public class CopleyCommsREST extends SimpleCommsREST<CopleyComms<?>> {
	private final DTOs dtos;

	public CopleyCommsREST(CopleyComms<?> comms, DTOs dtos) {
		super(comms);
		this.dtos = dtos;
	}

	@Override
	public Stream<CommsRESTEntry> getEntries() {
		return Stream.empty();
	}

	@Override
	public Stream<CommsRESTAction> getActions() {
		return Stream.empty();
	}

	@Override
	public String getLocalisedText(String key, Locale locale) {
		return key;
	}
}
