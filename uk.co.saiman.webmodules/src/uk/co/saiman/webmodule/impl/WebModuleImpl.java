package uk.co.saiman.webmodule.impl;

import static uk.co.saiman.webmodule.WebModuleConstants.ID_ATTRIBUTE;
import static uk.co.saiman.webmodule.WebModuleConstants.VERSION_ATTRIBUTE;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.ENTRY_POINT_ATTRIBUTE_PREFIX;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.RESOURCE_ROOT_ATTRIBUTE;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.osgi.framework.Bundle;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;

import uk.co.saiman.webmodule.EntryPoint;
import uk.co.saiman.webmodule.EntryPoints;
import uk.co.saiman.webmodule.ModuleFormat;
import uk.co.saiman.webmodule.PackageId;
import uk.co.saiman.webmodule.WebModule;

class WebModuleImpl implements WebModule {
  private final PackageId id;
  private final Version version;
  private final String root;
  private final Bundle bundle;
  private final List<ModuleFormat> formats;
  private final EntryPoints entryPoints;
  private final Set<WebModule> dependencies;

  public WebModuleImpl(BundleCapability capability, Collection<? extends WebModule> dependencies) {
    Map<String, Object> attributes = new HashMap<>(capability.getAttributes());

    this.id = getId(attributes);
    this.version = getVersion(attributes);
    this.root = getRoot(attributes);
    this.bundle = capability.getResource().getBundle();

    List<ModuleFormat> formats = new ArrayList<>();
    EntryPoints entryPoints = EntryPoints.empty();

    for (String attribute : attributes.keySet()) {
      if (attribute.startsWith(ENTRY_POINT_ATTRIBUTE_PREFIX)) {

        ModuleFormat format = new ModuleFormat(
            attribute.substring(ENTRY_POINT_ATTRIBUTE_PREFIX.length()));
        String entryPoint = attributes.get(attribute).toString();

        entryPoints = entryPoints.withEntryPoint(new EntryPoint(format, entryPoint));
        formats.add(format);
      }
    }

    this.formats = new ArrayList<>(formats);
    this.entryPoints = entryPoints;

    this.dependencies = new HashSet<>(dependencies);
  }

  private PackageId getId(Map<String, Object> attributes) {
    return PackageId.parseId((String) attributes.get(ID_ATTRIBUTE));
  }

  private Version getVersion(Map<String, Object> attributes) {
    return (Version) attributes.get(VERSION_ATTRIBUTE);
  }

  private String getRoot(Map<String, Object> attributes) {
    String root = (String) attributes.get(RESOURCE_ROOT_ATTRIBUTE);
    if (root.endsWith("/"))
      root = root.substring(0, root.length() - 1);
    return root;
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
  public Stream<ModuleFormat> formats() {
    return formats.stream();
  }

  @Override
  public EntryPoints entryPoints() {
    return entryPoints;
  }

  @Override
  public URL resource(String name) {
    System.out.println("GET!!!!!!");
    System.out.println(bundle);
    System.out.println(root + "/" + name);
    return bundle.getResource(root + "/" + name);
  }

  @Override
  public Stream<WebModule> dependencies() {
    return dependencies.stream();
  }
}
