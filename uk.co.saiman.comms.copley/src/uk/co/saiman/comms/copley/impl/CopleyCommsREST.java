package uk.co.saiman.comms.copley.impl;

import java.util.Locale;

import osgi.enroute.dto.api.DTOs;
import uk.co.saiman.comms.copley.CopleyComms;
import uk.co.saiman.comms.copley.CopleyController;
import uk.co.saiman.comms.rest.ControllerREST;
import uk.co.saiman.comms.rest.SimpleCommsREST;

public class CopleyCommsREST extends SimpleCommsREST<CopleyComms, CopleyController> {
  private final DTOs dtos;

  public CopleyCommsREST(CopleyComms comms, DTOs dtos) {
    super(comms);
    this.dtos = dtos;
  }

  @Override
  public String getLocalisedText(String key, Locale locale) {
    return key;
  }

  @Override
  public ControllerREST createControllerREST(CopleyController controller) {
    return new CopleyControllerREST(controller, dtos);
  }
}
