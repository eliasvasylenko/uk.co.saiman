package uk.co.saiman.comms.rest;

public class ConnectionDTO {
  public static enum Status {
    CONNECTED, FAULT
  }

  public String channel;
  public Status status;
  public String fault;
}
