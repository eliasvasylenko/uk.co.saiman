package uk.co.saiman.comms.rest;

import java.util.Optional;

import uk.co.saiman.comms.Comms;

public abstract class SimpleCommsREST<T extends Comms> implements CommsREST {
	private final T comms;

	public SimpleCommsREST(T comms) {
		this.comms = comms;
	}

	protected T getComms() {
		return comms;
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
