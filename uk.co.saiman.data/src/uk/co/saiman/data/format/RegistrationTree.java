package uk.co.saiman.data.format;

import java.util.Optional;

/**
 * IANA registration tree for {@link MediaType media types}.
 * 
 * @author Elias N Vasylenko
 */
public enum RegistrationTree {
  STANDARDS(), VENDOR("vnd"), PERSONAL("prs"), UNREGISTERED("x");

  private final String prefix;

  private RegistrationTree() {
    this.prefix = null;
  }

  private RegistrationTree(String prefix) {
    this.prefix = prefix;
  }

  public Optional<String> prefix() {
    return Optional.ofNullable(prefix);
  }
}
