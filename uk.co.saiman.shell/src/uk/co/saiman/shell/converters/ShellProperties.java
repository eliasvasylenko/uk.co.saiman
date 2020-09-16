package uk.co.saiman.shell.converters;

public class ShellProperties {
  private ShellProperties() {}

  public static final String COMMAND_SCOPE_KEY = "osgi.command.scope";
  public static final String COMMAND_FUNCTION_KEY = "osgi.command.function";

  public static final String COMMAND_SCOPE = "sai";
  public static final String COMMAND_SCOPE_PROPERTY = COMMAND_SCOPE_KEY + "=" + COMMAND_SCOPE;
}
