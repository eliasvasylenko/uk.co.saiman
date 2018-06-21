package uk.co.saiman.comms.copley.rest;

import java.util.List;

import javax.ws.rs.GET;

import uk.co.saiman.comms.copley.CopleyVariableID;

public interface CopleyVariablesRest {
  @GET
  public List<CopleyVariableID> getVariables();
}
