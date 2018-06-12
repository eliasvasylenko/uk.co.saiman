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
package uk.co.saiman.webmodule.commonjs.repository.bnd;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptyNavigableMap;
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
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.function.Predicate;

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
import uk.co.saiman.webmodule.ModuleFormat;
import uk.co.saiman.webmodule.commonjs.registry.impl.RegistryImpl;
import uk.co.saiman.webmodule.commonjs.repository.CommonJsBundle;
import uk.co.saiman.webmodule.commonjs.repository.CommonJsBundleVersion;
import uk.co.saiman.webmodule.commonjs.repository.CommonJsJar;
import uk.co.saiman.webmodule.commonjs.repository.CommonJsRepository;

@BndPlugin(name = "CommonJS", parameters = CommonJsRepositoryPluginConfiguration.class)
public class CommonJsRepositoryPlugin extends BaseRepository implements Plugin, RegistryPlugin,
    RepositoryPlugin, Refreshable, Closeable, RegistryDonePlugin {
  private static final String URL_KEY = "url";
  private static final String INITIAL_DEPENDENCIES_KEY = "initialDependencies";
  private static final String BSN_PREFIX_KEY = "bsnPrefix";

  private static final String VERSION_KEY = "version";
  private static final String VERSION_LATEST = "latest";
  private static final String VERSION_SNAPSHOT = "snapshot";
  private static final String VERSION_PROJECT = "project";

  private static final ModuleFormat NO_FORMAT = new ModuleFormat(" ");

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

    if (!map.containsKey(INITIAL_DEPENDENCIES_KEY)) {
      log.log(ERROR, "Must provide path to dependencies JSON \"" + INITIAL_DEPENDENCIES_KEY + "\"");
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
  }

  protected synchronized boolean initialize() {
    if (initialized) {
      return true;
    }
    if (configuration == null) {
      return false;
    }
    return initialized = initializeImpl();
  }

  private synchronized boolean initializeImpl() {
    Path initialDependenciesPath;
    String initialDependenciesString = configuration.initialDependencies();
    if (initialDependenciesString != null) {
      initialDependenciesPath = Paths.get(initialDependenciesString.trim());
    } else {
      log.log(Level.ERROR, "Unable to resolve initial dependencies");
      return false;
    }

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
          new RegistryImpl(url, cachePath),
          log,
          initialDependenciesPath,
          cachePath,
          configuration.bsnPrefix());
      bundles
          .putAll(
              repository
                  .getBundles()
                  .collect(toMap(b -> b.getBundleSymbolicName(NO_FORMAT), identity())));
    } catch (Exception e) {
      log.log(Level.ERROR, "Unable to initialize registry", e);
      return false;
    }

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
    return name;
  }

  @Override
  public String getLocation() {
    if (!initialize()) {
      return null;
    }

    return cachePath.toString();
  }

  @Override
  public PutResult put(InputStream stream, PutOptions options) throws Exception {
    throw new UnsupportedOperationException("Read-only repository.");
  }

  @Override
  public File get(
      String bsn,
      Version version,
      Map<String, String> properties,
      DownloadListener... listeners) throws Exception {
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
    return false;
  }

  @Override
  public List<String> list(String pattern) throws Exception {
    if (!initialize()) {
      return emptyList();
    }

    Predicate<String> matcher;

    if (pattern != null) {
      matcher = new Glob(pattern)::matches;
    } else {
      matcher = s -> true;
    }

    return bundles
        .values()
        .stream()
        .flatMap(b -> b.getFormats().map(b::getBundleSymbolicName))
        .filter(matcher::test)
        .collect(toList());
  }

  @Override
  public SortedSet<Version> versions(String bsn) throws Exception {
    if (!initialize()) {
      return emptySortedSet();
    }

    return getVersions(bsn).navigableKeySet();
  }

  private ModuleFormat getModuleFormat(String bsn) {
    int lastDot = bsn.lastIndexOf('.');
    return new ModuleFormat(bsn.substring(lastDot + 1));
  }

  private NavigableMap<Version, CommonJsBundleVersion> getVersions(String bsn) {
    int lastDot = bsn.lastIndexOf('.');

    if (lastDot == -1) {
      return emptyNavigableMap();
    }

    String prefix = bsn.substring(0, lastDot + 1) + NO_FORMAT;
    ModuleFormat format = getModuleFormat(bsn);

    if (!bundles.containsKey(prefix)) {
      return emptyNavigableMap();
    }

    NavigableMap<Version, CommonJsBundleVersion> packageVersions = new TreeMap<>();

    bundles.get(prefix).getBundleVersions(format).forEach(version -> {
      org.osgi.framework.Version v = version.getVersion();
      packageVersions
          .put(new Version(v.getMajor(), v.getMinor(), v.getMicro(), v.getQualifier()), version);
    });

    return packageVersions;
  }

  public ResourceHandle getHandle(
      String bsn,
      String version,
      Strategy strategy,
      Map<String, String> properties) throws Exception {
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

    CommonJsBundleVersion resource;

    if (VERSION_SNAPSHOT.equals(version) || VERSION_LATEST.equals(version)) {
      NavigableMap<Version, CommonJsBundleVersion> resources = getVersions(bsn);

      if (resources.isEmpty()) {
        return null;
      }

      resource = resources.lastEntry().getValue();

    } else {
      VersionRange range = new VersionRange(version);

      if (range.isRange() && strategy == Strategy.EXACT) {
        return null;
      }

      NavigableMap<Version, CommonJsBundleVersion> versions = getVersions(bsn);
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

    return getHandle(resource.getJar(getModuleFormat(bsn)).get());
  }

  private ResourceHandle getHandle(CommonJsJar resource) {
    return new ResourceHandle() {
      @Override
      public File request() throws IOException, Exception {
        return resource.getPath().toFile();
      }

      @Override
      public String getName() {
        return resource.getBsn();
      }

      @Override
      public Location getLocation() {
        return Location.remote;
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
    if (!initialize()) {
      return null;
    }

    return cachePath.toFile();
  }

  @Override
  public synchronized boolean refresh() throws Exception {
    initialized = false;
    initialize();
    return true;
  }

  @Override
  public Map<Requirement, Collection<Capability>> findProviders(
      Collection<? extends Requirement> requirements) {
    if (!initialize()) {
      return emptyMap();
    }

    return repository.findProviders(requirements);
  }

  @Override
  public void close() throws IOException {
    clearRepository();
  }

  @Override
  public String toString() {
    return "CommonJSRepositoryPlugin [" + getName() + "]";
  }
}
