package uk.co.saiman.comms.rest;

import java.util.Optional;
import java.util.stream.Stream;

/*
 * TODO? NOTE: Since this class does not actually rely on any other comms API, and
 * since the type of a {@link Comms#getControllerClass() comms controller} is
 * also unrelated to the comms API, it may be useful at some point to factor
 * this interface out into an independent bundle. This way, alternative
 * controller implementations may be provided that do not operate over serial
 * comms, and they would be able to share some of the same plumbing for
 * providing a REST interface.
 */
/**
 * A view of a comms device controller to adapt its functionality over a common
 * REST interface.
 * 
 * @author Elias N Vasylenko
 */
public interface ControllerREST {
  /**
   * @return a list of enumeration types the REST consumer should be aware of
   *         when presenting entry data
   */
  Stream<Class<? extends Enum<?>>> getEnums();

  Stream<ControllerRESTEntry> getEntries();

  default Optional<ControllerRESTEntry> getEntry(String entry) {
    return getEntries().filter(e -> e.getID().equals(entry)).findAny();
  }

  Stream<ControllerRESTAction> getActions();

  default Optional<ControllerRESTAction> getAction(String action) {
    return getActions().filter(a -> a.getID().equals(action)).findAny();
  }
}
