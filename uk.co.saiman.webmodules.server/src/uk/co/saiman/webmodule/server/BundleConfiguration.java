package uk.co.saiman.webmodule.server;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;
import static org.osgi.namespace.service.ServiceNamespace.CAPABILITY_OBJECTCLASS_ATTRIBUTE;
import static uk.co.saiman.webmodule.WebModuleConstants.AMD_FORMAT;
import static uk.co.saiman.webmodule.WebModuleConstants.CJS_FORMAT;
import static uk.co.saiman.webmodule.WebModuleConstants.ESM_FORMAT;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Stream;

import org.json.JSONObject;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

import uk.co.saiman.webmodule.ModuleFormat;
import uk.co.saiman.webmodule.WebModule;

public class BundleConfiguration {
  private static final int STRING_INDENTATION = 4;

  private final Bundle bundle;
  private final String pathRoot;

  private final JSONObject meta;
  private final JSONObject paths;
  private final JSONObject globalMap;
  private final JSONObject contextMap;

  public BundleConfiguration(Bundle bundle, String pathRoot) {
    this.bundle = bundle;
    this.pathRoot = pathRoot;

    meta = configureMeta();
    paths = configurePaths();
    globalMap = configureGlobalMap();
    contextMap = configureContextMap();
  }

  @Override
  public String toString() {
    return meta.toString(STRING_INDENTATION)
        + paths.toString(STRING_INDENTATION)
        + globalMap.toString(STRING_INDENTATION)
        + contextMap.toString(STRING_INDENTATION);
  }

  public void writeConfig(OutputStream output) throws IOException {
    writeConfig(output, meta);
    writeConfig(output, paths);
    writeConfig(output, globalMap);
    writeConfig(output, contextMap);
  }

  private void writeConfig(OutputStream output, JSONObject configuration) throws IOException {
    Writer out = new OutputStreamWriter(output);

    out.write("System.config(");
    configuration.write(out);
    out.write(");\n");
    out.flush();
  }

  private ModuleFormat getBestFormat(WebModule webModule) {
    Set<ModuleFormat> formats = webModule.formats().collect(toSet());
    if (formats.contains(AMD_FORMAT)) {
      return AMD_FORMAT;

    } else if (formats.contains(CJS_FORMAT)) {
      return CJS_FORMAT;

    } else if (formats.contains(ESM_FORMAT)) {
      return ESM_FORMAT;

    } else {
      throw new IllegalArgumentException("Can't find supported module type for " + webModule);
    }
  }

  private JSONObject configurePaths() {
    JSONObject configuration = new JSONObject();

    configuration.put("baseURL", pathRoot);

    JSONObject paths = new JSONObject();
    configuration.put("paths", paths);

    Deque<WebModule> webModules = getVisibleWebModules(bundle)
        .collect(toCollection(LinkedList::new));
    Set<WebModule> processed = new HashSet<>();

    while (!webModules.isEmpty()) {
      WebModule webModule = webModules.poll();

      if (processed.add(webModule)) {
        paths.put(getVersionedModuleName(webModule), getPath(webModule));

        webModule.dependencies().forEach(webModules::add);
      }
    }

    return configuration;
  }

  private JSONObject configureMeta() {
    JSONObject configuration = new JSONObject();

    JSONObject meta = new JSONObject();
    configuration.put("meta", meta);

    Deque<WebModule> webModules = getVisibleWebModules(bundle)
        .collect(toCollection(LinkedList::new));
    Set<WebModule> processed = new HashSet<>();

    while (!webModules.isEmpty()) {
      WebModule webModule = webModules.poll();

      if (processed.add(webModule)) {
        JSONObject format = new JSONObject();
        format.put("format", getBestFormat(webModule));
        meta.put(pathRoot + "/" + getVersionedModuleName(webModule) + "/*", format);

        webModule.dependencies().forEach(webModules::add);
      }
    }

    return configuration;
  }

  private JSONObject configureContextMap() {
    JSONObject configuration = new JSONObject();

    JSONObject maps = new JSONObject();
    configuration.put("map", maps);

    Deque<WebModule> webModules = getVisibleWebModules(bundle)
        .collect(toCollection(LinkedList::new));
    Set<WebModule> processed = new HashSet<>();

    while (!webModules.isEmpty()) {
      WebModule webModule = webModules.poll();

      if (processed.add(webModule)) {
        maps.put(getVersionedModuleName(webModule), getMap(webModule));

        webModule.dependencies().forEach(webModules::add);
      }
    }

    return configuration;
  }

  private JSONObject getMap(WebModule webModule) {
    JSONObject map = new JSONObject();

    webModule.dependencies().forEach(m -> {
      map.put(m.id().toString(), getVersionedModuleName(m));
    });

    return map;
  }

  private JSONObject configureGlobalMap() {
    JSONObject configuration = new JSONObject();

    JSONObject map = new JSONObject();
    configuration.put("map", map);

    getVisibleWebModules(bundle).forEach(webModule -> {
      map.put(webModule.id().toString(), getVersionedModuleName(webModule));
    });

    return configuration;
  }

  private Stream<WebModule> getVisibleWebModules(Bundle bundle) {
    return concat(stream(bundle.getRegisteredServices()), stream(bundle.getServicesInUse()))
        .filter(this::isWebModule)
        .map(reference -> bundle.getBundleContext().getService(reference))
        .map(service -> (WebModule) service);
  }

  private boolean isWebModule(ServiceReference<?> reference) {
    Object classes = reference.getProperty(CAPABILITY_OBJECTCLASS_ATTRIBUTE);

    if (classes instanceof String[]) {
      return stream((String[]) classes).anyMatch(WebModule.class.getName()::equals);

    } else if (classes instanceof String) {
      return WebModule.class.getName().equals((String) classes);

    } else {
      return false;
    }
  }

  private String getVersionedModuleName(WebModule webModule) {
    String name = webModule.id().toString();
    name = name.replaceAll("/", "%2F");
    String version = webModule.version().toString();

    return name + "/" + version;
  }

  private String getPath(WebModule webModule) {
    ModuleFormat format = getBestFormat(webModule);

    /*
     * TODO we need to do some crazy babel transpilation for ES6 modules. We also
     * need to cache the result have the "modules" map contain some internal object
     * not just a service reference to deal with all this.
     */
    return getVersionedModuleName(webModule)
        + "/"
        + webModule.entryPoints().getEntryPoint(format).path();
  }
}
