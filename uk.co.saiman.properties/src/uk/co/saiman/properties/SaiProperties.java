package uk.co.saiman.properties;

import static uk.co.saiman.properties.Key.UNQUALIFIED_DOTTED;

/**
 * Utilities relating conceptually to the Scientific Analysis Instruments set of
 * products.
 * 
 * @author Elias N Vasylenko
 */
@Key(UNQUALIFIED_DOTTED)
public interface SaiProperties {
  /**
   * Namespace for GoGo commands.
   */
  public static String SAI_COMMAND_SCOPE = "sai";

  String copyrightHolderName();

  String copyrightHolderEmail();

  int copyrightYear();

  LocalizedString copyrightMessage();
}
