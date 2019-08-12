package uk.co.saiman.maldi.acquisition.msapex;

import javax.annotation.PostConstruct;

/*
 * Even if nothing else, this addon is used by MaldiAcquisitionPart
 * to store state, since the part is overridden in the model every
 * start.
 */
public class MaldiAcquisitionAddon {
  @PostConstruct
  void initialize() {}
}
