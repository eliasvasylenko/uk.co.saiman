/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.osgi.
 *
 * uk.co.saiman.osgi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.osgi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.osgi;

import static org.osgi.namespace.extender.ExtenderNamespace.EXTENDER_NAMESPACE;
import static uk.co.saiman.log.Log.forwardingLog;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.util.tracker.BundleTracker;

import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;

/**
 * An abstract class intended to facilitate implementation of OSGi extenders.
 * <p>
 * The extender capability relating to an implementation of
 * {@link ExtenderManager} should be the only one provided by the containing
 * bundle. The capability will typically be provided via the
 * {@link ProvideExtender} annotation on the implementing class.
 * 
 * @author Elias N Vasylenko
 */
public abstract class ExtenderManager extends BundleTracker<Bundle> {
  private BundleContext context;
  private BundleCapability capability;
  private Log log;
  private final Set<Bundle> extendedBundles = new HashSet<>();

  public ExtenderManager(BundleContext context) {
    super(context, Bundle.ACTIVE, null);
    this.context = context;

    List<BundleCapability> extenderCapabilities = this.context
        .getBundle()
        .adapt(BundleWiring.class)
        .getCapabilities(EXTENDER_NAMESPACE);

    if (extenderCapabilities.isEmpty()) {
      throw new IllegalStateException(
          "Cannot initiate extender, no capability is present on the implementing bundle");
    }
    if (extenderCapabilities.size() > 1) {
      throw new IllegalStateException(
          "Cannot initiate extender, capability on the implementing bundle is ambiguous between: "
              + extenderCapabilities);
    }

    capability = extenderCapabilities.get(0);
  }

  protected Version getVersion() {
    var version = capability.getAttributes().get(Constants.VERSION_ATTRIBUTE);
    if (version instanceof Version) {
      return (Version) version;
    } else {
      return Version.parseVersion(version.toString());
    }
  }

  protected Log getLog() {
    return forwardingLog(() -> log);
  }

  @Override
  public void modifiedBundle(Bundle bundle, BundleEvent event, Bundle object) {
    tryUpdate(bundle);
  }

  protected void tryUpdate(Bundle bundle) {
    try {
      if (extendedBundles.contains(bundle)) {
        update(bundle);
      }
    } catch (Exception e) {
      getLog()
          .log(
              Level.ERROR,
              "Problem updating bundle '"
                  + bundle.getSymbolicName()
                  + "' with extension manager '"
                  + this
                  + "'",
              e);
    }
  }

  @Override
  public Bundle addingBundle(Bundle bundle, BundleEvent event) {
    tryRegister(bundle);
    return bundle;
  }

  protected void tryRegister(Bundle bundle) {
    try {
      if (isExtensible(bundle) && register(bundle)) {
        extendedBundles.add(bundle);
      }
    } catch (Exception e) {
      getLog()
          .log(
              Level.ERROR,
              "Problem registering bundle '"
                  + bundle.getSymbolicName()
                  + "' with extension manager '"
                  + this
                  + "'",
              e);
    }
  }

  private boolean isExtensible(Bundle bundle) {
    return bundle
        .adapt(BundleWiring.class)
        .getRequirements(EXTENDER_NAMESPACE)
        .stream()
        .anyMatch(r -> r.matches(capability));
  }

  @Override
  public void removedBundle(Bundle bundle, BundleEvent event, Bundle object) {
    tryUnregister(bundle);
  }

  private void tryUnregister(Bundle bundle) {
    try {
      if (extendedBundles.remove(bundle)) {
        unregister(bundle);
      }
    } catch (Exception e) {
      getLog()
          .log(
              Level.ERROR,
              "Problem unregistering bundle '"
                  + bundle.getSymbolicName()
                  + "' with extension manager '"
                  + this
                  + "'",
              e);
    }
  }

  @Reference(policy = ReferencePolicy.DYNAMIC)
  protected void setLog(Log log) {
    this.log = log;
  }

  protected void unsetLog(Log log) {
    if (this.log == log)
      this.log = null;
  }

  protected abstract boolean register(Bundle bundle);

  protected abstract void update(Bundle bundle);

  protected abstract void unregister(Bundle bundle);
}
