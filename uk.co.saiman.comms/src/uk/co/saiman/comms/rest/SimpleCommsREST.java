package uk.co.saiman.comms.rest;

import static java.util.Optional.ofNullable;

import java.util.Optional;

import uk.co.saiman.comms.Comms;
import uk.co.saiman.comms.Comms.CommsStatus;
import uk.co.saiman.comms.CommsException;

public abstract class SimpleCommsREST<U extends Comms<T>, T> implements CommsREST {
  private final U comms;
  private T controller;
  private ControllerREST controllerREST;
  private CommsException lastFault;

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
    return ofNullable(lastFault).map(CommsException::getMessage);
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
