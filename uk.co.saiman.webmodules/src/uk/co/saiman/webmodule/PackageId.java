package uk.co.saiman.webmodule;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Objects;
import java.util.Optional;

public class PackageId {
  private final Optional<String> scope;
  private final String name;

  public PackageId(String name) {
    this(Optional.empty(), name);
  }

  public PackageId(String scope, String name) {
    this(Optional.of(scope), name);
  }

  private PackageId(Optional<String> scope, String name) {
    this.scope = scope;
    this.name = name;
  }

  public Optional<String> getScope() {
    return scope;
  }

  public String getName() {
    return name;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (!(obj instanceof PackageId))
      return false;

    PackageId that = (PackageId) obj;
    return Objects.equals(this.scope, that.scope) && Objects.equals(this.name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(scope, name);
  }

  public static PackageId parseId(String idString) {
    if (!idString.startsWith("@"))
      return new PackageId(idString);

    idString = idString.substring(1);
    String[] idStrings = idString.split("/", 2);
    return new PackageId(idStrings[0], idStrings[1]);
  }

  @Override
  public String toString() {
    return scope.map(s -> "@" + s + "/" + name).orElse(name);
  }

  public static String urlEncodeName(PackageId name) throws UnsupportedEncodingException {
    if (name.scope.isPresent()) {
      return "@" + URLEncoder.encode(name.scope.get() + "/" + name.name, "UTF-8");
    } else {
      return URLEncoder.encode(name.name, "UTF-8");
    }
  }

  public static PackageId urlDecodeName(String name) throws UnsupportedEncodingException {
    return parseId(URLDecoder.decode(name, "UTF-8"));
  }
}
