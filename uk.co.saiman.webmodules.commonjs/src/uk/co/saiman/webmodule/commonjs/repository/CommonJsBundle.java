package uk.co.saiman.webmodule.commonjs.repository;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.webmodule.EntryPoints;
import uk.co.saiman.webmodule.PackageId;
import uk.co.saiman.webmodule.commonjs.registry.PackageRoot;
import uk.co.saiman.webmodule.commonjs.registry.PackageVersion;
import uk.co.saiman.webmodule.semver.Range;
import uk.co.saiman.webmodule.semver.Version;

public class CommonJsBundle {
  private final CommonJsRepository repository;
  private final PackageRoot packageRoot;
  private final String bundleName;

  private final Map<uk.co.saiman.webmodule.semver.Version, CommonJsBundleVersion> resources;
  private final Map<Range, EntryPoints> explicitEntryPoints;

  public CommonJsBundle(CommonJsRepository repository, PackageRoot packageRoot) {
    this.repository = repository;
    this.packageRoot = packageRoot;
    this.bundleName = createBundleSymbolicName();
    this.resources = new HashMap<>();
    this.explicitEntryPoints = new LinkedHashMap<>();
  }

  Log getLog() {
    return repository.getLog();
  }

  public CommonJsRepository getRepository() {
    return repository;
  }

  public PackageId getModuleName() {
    return packageRoot.getName();
  }

  public String getBundleSymbolicName() {
    return bundleName;
  }

  private String createBundleSymbolicName() {
    PackageId name = packageRoot.getName();
    String nameString = name.getScope().map(s -> s + "." + name.getName()).orElse(name.getName());
    return repository.getBundleSymbolicNamePrefix()
        + "."
        + stream(nameString.split("[^a-zA-Z0-9._-]")).collect(joining("_"));
  }

  public Stream<uk.co.saiman.webmodule.semver.Version> getSemvers() {
    return resources.keySet().stream();
  }

  public Stream<CommonJsBundleVersion> getBundleVersions() {
    return resources.values().stream();
  }

  public Optional<CommonJsBundleVersion> getBundleVersion(
      uk.co.saiman.webmodule.semver.Version semver) {
    return Optional.ofNullable(resources.get(semver));
  }

  Stream<CommonJsBundleVersion> fetchDependencies(Range range) {
    return packageRoot
        .getPackageVersions()
        .parallel()
        .filter(range::matches)
        .filter(v -> !resources.containsKey(v))
        .flatMap(this::loadBundleVersion);
  }

  EntryPoints getExplicitEntryPoints(Version version) {
    for (Entry<Range, EntryPoints> entryPoint : explicitEntryPoints.entrySet()) {
      if (entryPoint.getKey().matches(version)) {
        return entryPoint.getValue();
      }
    }
    return EntryPoints.empty();
  }

  private Stream<CommonJsBundleVersion> loadBundleVersion(Version version) {
    try {
      PackageVersion packageVersion = packageRoot.getPackageVersion(version);

      synchronized (resources) {
        return Stream
            .of(
                resources
                    .computeIfAbsent(
                        packageVersion.getVersion(),
                        v -> new CommonJsBundleVersion(this, packageVersion)));
      }
    } catch (Exception e) {
      getLog()
          .log(
              Level.WARN,
              "Cannot load bundle version " + packageRoot.getName() + " - " + version,
              e);
      return Stream.empty();
    }
  }
}
