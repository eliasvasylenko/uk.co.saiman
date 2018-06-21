package uk.co.saiman.comms.copley.rest;

import javax.ws.rs.core.MediaType;

public class CopleyRestServiceConstants {
  public static final int MAJOR_VERSION = 1;
  public static final String VERSION = MAJOR_VERSION + ".0.0";

  public static final String ENDPOINT = "api/copley";

  public static final String APPLICATION_COPLEY_JSON = "application/vnd.saiman.copley.v"
      + MAJOR_VERSION
      + "+json";
  public static final MediaType COPLEY_MEDIA_TYPE = MediaType.valueOf(APPLICATION_COPLEY_JSON);
}
