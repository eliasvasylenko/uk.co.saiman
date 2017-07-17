package uk.co.saiman.comms.rest;

import java.util.Optional;

import uk.co.saiman.comms.Comms;
import uk.co.saiman.comms.Comms.CommsStatus;

public abstract class SimpleCommsREST<U extends Comms<T>, T> implements CommsREST {
  private final U comms;
  private T controller;
  private ControllerREST controllerREST;

  public SimpleCommsREST(U comms) {
    this.comms = comms;
  }

  protected U getComms() {
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
  public CommsStatus getStatus() {
    return comms.status().get();
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
  public ControllerREST openController() {
    T controller = comms.openController();
    if (this.controller != controller) {
      this.controller = controller;
      controllerREST = createControllerREST(controller);
    }
    return controllerREST;
  }

  public abstract ControllerREST createControllerREST(T controller);

  @Override
  public void reset() {
    comms.reset();
  }
}
