/*
 * Copyright (C) 2018 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *          ______         ___      ___________
 *       ,'========\     ,'===\    /========== \
 *      /== \___/== \  ,'==.== \   \__/== \___\/
 *     /==_/____\__\/,'==__|== |     /==  /
 *     \========`. ,'========= |    /==  /
 *   ___`-___)== ,'== \____|== |   /==  /
 *  /== \__.-==,'==  ,'    |== '__/==  /_
 *  \======== /==  ,'      |== ========= \
 *   \_____\.-\__\/        \__\\________\/
 *
 * This file is part of uk.co.saiman.webmodules.commonjs.
 *
 * uk.co.saiman.webmodules.commonjs is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.webmodules.commonjs is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.webmodules.commonjs.repository.bnd;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySortedSet;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static uk.co.saiman.log.Log.Level.ERROR;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;

import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.util.converter.Converters;

import aQute.bnd.annotation.plugin.BndPlugin;
import aQute.bnd.build.Workspace;
import aQute.bnd.osgi.repository.BaseRepository;
import aQute.bnd.service.Plugin;
import aQute.bnd.service.Refreshable;
import aQute.bnd.service.Registry;
import aQute.bnd.service.RegistryDonePlugin;
import aQute.bnd.service.RegistryPlugin;
import aQute.bnd.service.RepositoryPlugin;
import aQute.bnd.service.ResourceHandle;
import aQute.bnd.service.Strategy;
import aQute.bnd.version.Version;
import aQute.bnd.version.VersionRange;
import aQute.service.reporter.Reporter;
import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.text.Glob;
import uk.co.saiman.webmodules.commonjs.registry.impl.RegistryImpl;
import uk.co.saiman.webmodules.commonjs.repository.CommonJsRepository;
import uk.co.saiman.webmodules.commonjs.repository.CommonJsRepository.CommonJsBundle;
import uk.co.saiman.webmodules.commonjs.repository.CommonJsResource;

@BndPlugin(name = "CommonJS", parameters = CommonJsRepositoryPluginConfiguration.class)
public class CommonJsRepositoryPlugin extends BaseRepository implements Plugin, RegistryPlugin,
    RepositoryPlugin, Refreshable, Closeable, RegistryDonePlugin {
  private static final String URL_KEY = "url";
  private static final String MODULES_KEY = "modules";
  private static final String BSN_PREFIX_KEY = "bsnPrefix";

  private static final String VERSION_KEY = "version";
  private static final String VERSION_LATEST = "latest";
  private static final String VERSION_SNAPSHOT = "snapshot";
  private static final String VERSION_PROJECT = "project";

  private Registry bndRegistry;
  private Reporter bndReporter;
  private Workspace bndWorkspace;
  private Log log = Log.discardingLog();

  private CommonJsRepositoryPluginConfiguration configuration;
  private String name;
  private String cache;
  private boolean initialized;

  private CommonJsRepository repository;
  private Path cachePath;
  private Map<String, CommonJsBundle> bundles;

  public CommonJsRepositoryPlugin() {
    clearRepository();
  }

  protected synchronized void clearRepository() {
    configuration = null;
    name = null;
    cache = null;

    cachePath = null;
    bundles = new HashMap<>();
  }

  @Override
  public synchronized void setProperties(Map<String, String> map) throws Exception {
    clearRepository();

    boolean success = true;

    if (!map.containsKey(URL_KEY)) {
      log.log(ERROR, "Must provide registry root URL \"" + URL_KEY + "\"");
      success = false;
    }

    if (!map.containsKey(MODULES_KEY)) {
      log.log(ERROR, "Must provide comma-separated module list \"" + MODULES_KEY + "\"");
      success = false;
    }

    if (!map.containsKey(BSN_PREFIX_KEY)) {
      log.log(ERROR, "Must provide BSN prefix for generated bundles \"" + BSN_PREFIX_KEY + "\"");
      success = false;
    }

    if (success) {
      configuration = Converters
          .standardConverter()
          .convert(map)
          .to(CommonJsRepositoryPluginConfiguration.class);
      name = configuration.name("CommonJS-" + configuration.url());
      cache = configuration.cache("cnf/cache/CommonJS/" + name);
    }
    log.log(Level.WARN, "config: " + configuration + " " + success + " - " + map);
  }

  protected synchronized boolean initialize() {
    log.log(Level.WARN, "try init: " + initialized + " " + configuration);
    if (initialized) {
      return true;
    }
    if (configuration == null) {
      return false;
    }
    return initialized = initializeImpl();
  }

  private synchronized boolean initializeImpl() {
    Set<String> modules = new HashSet<>();
    String modulesString = configuration.modules().trim();
    if (!modulesString.isEmpty()) {
      for (String module : modulesString.split("\\s+")) {
        modules.add(module);
      }
    }

    log.log(Level.WARN, "modules to initialize... " + modules);

    try {
      cachePath = bndWorkspace.getFile(this.cache).toPath();
      Files.createDirectories(cachePath);
    } catch (Exception e) {
      log.log(Level.ERROR, "Unable to initialize cache", e);
      return false;
    }

    try {
      URL url = configuration.url().toURL();
      repository = new CommonJsRepository(
          new RegistryImpl(url),
          log,
          modules,
          cachePath,
          configuration.bsnPrefix());
      bundles
          .putAll(
              repository
                  .getBundles()
                  .collect(toMap(CommonJsBundle::getBundleSymbolicName, identity())));
    } catch (Exception e) {
      log.log(Level.ERROR, "Unable to initialize registry", e);
      return false;
    }

    log.log(Level.WARN, "successfully initialized? " + bundles);

    return true;
  }

  @Override
  public synchronized void setReporter(Reporter reporter) {
    this.bndReporter = reporter;
    this.log = new ReporterLog(bndReporter);
  }

  @Override
  public void done() throws Exception {
    this.bndWorkspace = bndRegistry.getPlugin(Workspace.class);
    this.log = new ReporterLog(bndReporter, bndWorkspace);
  }

  @Override
  public synchronized void setRegistry(Registry registry) {
    this.bndRegistry = registry;
  }

  @Override
  public String getName() {
    log.log(Level.WARN, "get name! " + name);
    return name;
  }

  @Override
  public String getLocation() {
    log.log(Level.WARN, "get location!");
    if (!initialize()) {
      return null;
    }

    return cachePath.toString();
  }

  @Override
  public PutResult put(InputStream stream, PutOptions options) throws Exception {
    log.log(Level.WARN, "put!");
    throw new UnsupportedOperationException("Read-only repository.");
  }

  @Override
  public File get(
      String bsn,
      Version version,
      Map<String, String> properties,
      DownloadListener... listeners) throws Exception {
    log.log(Level.WARN, "get!" + bsn + " " + version + " " + properties);
    if (!initialize()) {
      return null;
    }

    String versionStr;
    if (version != null)
      versionStr = version.toString();
    else
      versionStr = properties.get(VERSION_KEY);
    ResourceHandle handle = getHandle(bsn, versionStr, Strategy.EXACT, properties);
    if (handle == null)
      return null;

    File f = handle.request();
    if (f == null)
      return null;

    for (DownloadListener l : listeners) {
      try {
        l.success(f);
      } catch (Exception e) {
        log.log(Level.ERROR, "Download listener for " + f + ": " + e);
      }
    }
    return f;
  }

  @Override
  public boolean canWrite() {
    log.log(Level.WARN, "can write!");
    return false;
  }

  @Override
  public List<String> list(String pattern) throws Exception {
    log.log(Level.WARN, "list!" + pattern);
    if (!initialize()) {
      return emptyList();
    }

    List<String> result;

    if (pattern != null) {
      Glob glob = new Glob(pattern);
      result = bundles.keySet().stream().filter(glob::matches).collect(toList());
    } else {
      result = new ArrayList<>(bundles.keySet());
    }

    return result;
  }

  @Override
  public SortedSet<Version> versions(String bsn) throws Exception {
    log.log(Level.WARN, "versions!" + bsn);
    if (!initialize()) {
      return emptySortedSet();
    }

    return getResources(bsn).navigableKeySet();
  }

  private NavigableMap<Version, CommonJsResource> getResources(String bsn) {
    NavigableMap<Version, CommonJsResource> packageVersions = new TreeMap<>();

    bundles.get(bsn).getResources().forEach(resource -> {
      org.osgi.framework.Version version = resource.getVersion();
      packageVersions
          .put(
              new Version(
                  version.getMajor(),
                  version.getMinor(),
                  version.getMicro(),
                  version.getQualifier()),
              resource);
    });

    return packageVersions;
  }

  public ResourceHandle getHandle(
      String bsn,
      String version,
      Strategy strategy,
      Map<String, String> properties) throws Exception {
    log.log(Level.WARN, "get handle!" + bsn + " - " + version + " " + strategy + " " + properties);
    if (!initialize()) {
      throw new IllegalArgumentException("Cannot initialize repository");
    }

    if (bsn == null) {
      throw new IllegalArgumentException("Cannot resolve null BSN");
    }

    if (version == null) {
      version = "0.0.0";
    } else if (VERSION_PROJECT.equals(version)) {
      return null;
    }

    CommonJsResource resource;

    if (VERSION_SNAPSHOT.equals(version) || VERSION_LATEST.equals(version)) {
      NavigableMap<Version, CommonJsResource> resources = getResources(bsn);

      if (resources.isEmpty()) {
        return null;
      }

      resource = resources.lastEntry().getValue();

    } else {
      VersionRange range = new VersionRange(version);

      if (range.isRange() && strategy == Strategy.EXACT) {
        return null;
      }

      NavigableMap<Version, CommonJsResource> versions = getResources(bsn);
      versions = versions
          .subMap(range.getLow(), range.includeLow(), range.getHigh(), range.includeHigh());

      if (versions.isEmpty()) {
        return null;
      }

      switch (strategy) {
      case LOWEST:
        resource = versions.firstEntry().getValue();
        break;
      default:
        resource = versions.lastEntry().getValue();
      }
    }

    return getHandle(resource);
  }

  private ResourceHandle getHandle(CommonJsResource resource) {
    log
        .log(
            Level.WARN,
            "get handle for package version!"
                + resource.getBundle().getBundleSymbolicName()
                + " / "
                + resource.getVersion());

    return new ResourceHandle() {
      @Override
      public File request() throws IOException, Exception {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public String getName() {
        // TODO Auto-generated method stub
        return null;
      }

      @Override
      public Location getLocation() {
        // TODO Auto-generated method stub
        return null;
      }
    };
  }

  /*-
  @Override
  public File getCacheDirectory() {
    log.log(Level.WARN, "get cache directory!");
    if (!initialize()) {
      return null;
    }
  
    return cachePath.toFile();
  }
  */

  @Override
  public File getRoot() throws Exception {
    log.log(Level.WARN, "get root!");
    if (!initialize()) {
      return null;
    }

    return cachePath.toFile();
  }

  @Override
  public synchronized boolean refresh() throws Exception {
    log.log(Level.WARN, "refresh!");
    initialized = false;
    initialize();
    return true;
  }

  @Override
  public Map<Requirement, Collection<Capability>> findProviders(
      Collection<? extends Requirement> requirements) {
    log.log(Level.WARN, "find providers!");
    if (!initialize()) {
      return emptyMap();
    }

    return repository.findProviders(requirements);
  }

  @Override
  public void close() throws IOException {
    log.log(Level.WARN, "close!");
    clearRepository();
  }

  @Override
  public String toString() {
    log.log(Level.WARN, "toString!");
    return "CommonJSRepositoryPlugin [" + getName() + "]";
  }
}
