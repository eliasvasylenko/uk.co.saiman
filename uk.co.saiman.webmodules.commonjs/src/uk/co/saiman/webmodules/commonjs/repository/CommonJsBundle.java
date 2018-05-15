package uk.co.saiman.webmodules.commonjs.repository;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.saiman.log.Log;
import uk.co.saiman.log.Log.Level;
import uk.co.saiman.webmodules.commonjs.registry.PackageRoot;
import uk.co.saiman.webmodules.commonjs.registry.PackageVersion;
import uk.co.saiman.webmodules.semver.Range;
import uk.co.saiman.webmodules.semver.Version;

public class CommonJsBundle {
  private final CommonJsRepository repository;
  private final PackageRoot packageRoot;
  private final String bundleName;

  private final Map<uk.co.saiman.webmodules.semver.Version, CommonJsBundleVersion> resources;

  public CommonJsBundle(CommonJsRepository repository, PackageRoot packageRoot) {
    this.repository = repository;
    this.packageRoot = packageRoot;
    this.bundleName = createBundleSymbolicName();
    this.resources = new HashMap<>();
  }

  Log getLog() {
    return repository.getLog();
  }

  public CommonJsRepository getRepository() {
    return repository;
  }

  public String getModuleName() {
    return packageRoot.getName();
  }

  public String getBundleSymbolicName() {
    return bundleName;
  }

  private String createBundleSymbolicName() {
    return repository.getBundleSymbolicNamePrefix()
        + "."
        + stream(packageRoot.getName().split("[^a-zA-Z0-9_-]")).collect(joining("_"));
  }

  public Stream<uk.co.saiman.webmodules.semver.Version> getSemvers() {
    return resources.keySet().stream();
  }

  public Stream<CommonJsBundleVersion> getBundleVersions() {
    return resources.values().stream();
  }

  public Optional<CommonJsBundleVersion> getBundleVersion(
      uk.co.saiman.webmodules.semver.Version semver) {
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
