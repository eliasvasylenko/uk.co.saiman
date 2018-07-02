package uk.co.saiman.comms.copley.rest;

import javax.ws.rs.core.MediaType;

public class CopleyRestConstants {
  public static final int MAJOR_VERSION = 1;
  public static final String VERSION = MAJOR_VERSION + ".0.0";

  public static final String ENDPOINT = "api/copley";

  public static final String CONTENT_TYPE = "application/vnd.saiman.copley.v"
      + MAJOR_VERSION
      + "+json";
  public static final MediaType MEDIA_TYPE = MediaType.valueOf(CONTENT_TYPE);
}
