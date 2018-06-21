package uk.co.saiman.webmodule.i18n;

import static java.lang.System.lineSeparator;
import static java.util.Collections.list;
import static java.util.Locale.forLanguageTag;
import static java.util.stream.Collectors.joining;
import static uk.co.saiman.webmodule.WebModuleConstants.DEFAULT_ENTRY_POINT;
import static uk.co.saiman.webmodule.WebModuleConstants.ESM_FORMAT;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.stream.Stream;

import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleWiring;

import uk.co.saiman.webmodule.ModuleFormat;
import uk.co.saiman.webmodule.PackageId;
import uk.co.saiman.webmodule.WebModule;

public abstract class ResourceBundleWebModule implements WebModule {
  private static final String JS_EXTENSION = ".js";

  private final PackageId id;
  private final Version version;
  private final Function<? super Locale, ? extends ResourceBundle> resourceBundleSupplier;

  private final Map<Locale, String> resources = new HashMap<>();

  public ResourceBundleWebModule(PackageId id, Version version, String resourceBundle) {
    this.id = id;
    this.version = version;
    this.resourceBundleSupplier = locale -> ResourceBundle
        .getBundle(
            resourceBundle,
            locale,
            FrameworkUtil.getBundle(getClass()).adapt(BundleWiring.class).getClassLoader());
  }

  public ResourceBundleWebModule(
      PackageId id,
      Version version,
      Function<? super Locale, ? extends ResourceBundle> resourceBundleSupplier) {
    this.id = id;
    this.version = version;
    this.resourceBundleSupplier = resourceBundleSupplier;
  }

  @Override
  public PackageId id() {
    return id;
  }

  @Override
  public Version version() {
    return version;
  }

  @Override
  public ModuleFormat format() {
    return ESM_FORMAT;
  }

  @Override
  public String entryPoint() {
    return DEFAULT_ENTRY_POINT;
  }

  @Override
  public String openResource(String name) throws IOException {
    if (!name.endsWith(JS_EXTENSION)) {
      throw new FileNotFoundException();
    }

    Locale locale = forLanguageTag(name.substring(0, name.length() - JS_EXTENSION.length()));

    return resources.computeIfAbsent(locale, this::createResource);
  }

  private String createResource(Locale locale) {
    ResourceBundle resourceBundle = resourceBundleSupplier.apply(locale);

    StringBuilder builder = new StringBuilder();

    builder.append("const i18n = {").append(lineSeparator());

    builder
        .append(
            list(resourceBundle.getKeys())
                .stream()
                .map(
                    key -> "  \""
                        + key
                        + "\" : "
                        + getValueSource(key, resourceBundle.getString(key)))
                .collect(joining("," + lineSeparator())));

    builder.append(lineSeparator()).append("}").append(lineSeparator());

    builder.append("export const localize = (key) => properties[key]");

    return builder.toString();
  }

  protected String getValueSource(String key, String value) {
    return "`" + value + "`";
  }

  @Override
  public Stream<WebModule> dependencies() {
    return Stream.empty();
  }
}
