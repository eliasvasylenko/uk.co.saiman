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
package uk.co.saiman.webmodules.commonjs.registry.impl;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.http.whiteboard.annotations.RequireHttpWhiteboard;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import uk.co.saiman.webmodules.commonjs.registry.PackageRoot;
import uk.co.saiman.webmodules.commonjs.registry.Registry;

@RequireHttpWhiteboard
@Designate(ocd = RegistryImpl.CommonJsRegistryConfiguration.class, factory = true)
@Component(
    immediate = true,
    configurationPid = RegistryImpl.CONFIGURATION_PID,
    configurationPolicy = ConfigurationPolicy.REQUIRE)
public class RegistryImpl implements Registry {
  @ObjectClassDefinition(
      name = "CommonJS Registry",
      description = "The CommonJS registry service allows access to javascript repositories following the CommonJS registry specification")
  public @interface CommonJsRegistryConfiguration {
    @AttributeDefinition(name = "Registry Root URL")
    String registryRootUrl();

    @AttributeDefinition(name = "Local Cache Path")
    String localCachePath();
  }

  static final String CONFIGURATION_PID = "uk.co.saiman.webmodules.commonsjs.registry";

  private final Map<String, Object> packageLocks = new HashMap<>();
  private final Map<String, PackageRoot> packageRoots = new HashMap<>();

  private final URL registryRoot;
  private final Path localCache;

  public RegistryImpl(URL registryRoot, Path localCache) {
    this.registryRoot = registryRoot;
    this.localCache = localCache;
  }

  @Activate
  public RegistryImpl(CommonJsRegistryConfiguration configuration) throws IOException {
    this.registryRoot = new URL(configuration.registryRootUrl());
    this.localCache = Paths.get(configuration.localCachePath());
    Files.createDirectories(this.localCache);
  }

  @Override
  public URL getURL() {
    return registryRoot;
  }

  @Override
  public PackageRoot getPackageRoot(String name) {
    PackageRoot root;
    Object lock;
    synchronized (packageLocks) {
      root = packageRoots.get(name);
      lock = (root == null) ? packageLocks.computeIfAbsent(name, n -> new Object()) : null;
    }
    if (root == null) {
      synchronized (lock) {
        root = packageRoots.get(name);
        if (root == null) {
          root = new PackageRootImpl(registryRoot, localCache, name);
          synchronized (packageLocks) {
            packageRoots.put(name, root);
            packageLocks.remove(name);
          }
        }
      }
    }
    return root;
  }
}
