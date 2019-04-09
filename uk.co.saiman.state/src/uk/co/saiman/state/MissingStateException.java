package uk.co.saiman.state;

import static java.lang.String.format;

public class MissingStateException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public MissingStateException(String id) {
    super(format("Missing state in map for id %s", id));
  }
}
