package uk.co.saiman.webmodules.commonjs.repository;

import static java.nio.file.Files.newInputStream;
import static java.util.stream.Collectors.toList;
import static uk.co.saiman.webmodules.commonjs.repository.CommonJsCache.getBytes;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.osgi.framework.Filter;
import org.osgi.framework.Version;

import uk.co.saiman.webmodules.commonjs.registry.Archive;
import uk.co.saiman.webmodules.commonjs.registry.ArchiveType;
import uk.co.saiman.webmodules.commonjs.registry.PackageVersion;
import uk.co.saiman.webmodules.commonjs.registry.RegistryResolutionException;
import uk.co.saiman.webmodules.semver.Range;

public class CommonJsBundleVersion {
  static final String PACKAGE_ROOT = "package/";
  private static final String PACKAGE_JSON = "package.json";
  private static final String DIST = "dist";

  static final String RESOURCE_ROOT = "static";

  private final CommonJsBundle bundle;
  private final PackageVersion packageVersion;
  private final Version version;

  private final CommonJsCache cache;

  private JSONObject packageJson;
  private CommonJsResource resource;
  private Path packageDist;
  private CommonJsJar bundleJar;

  public CommonJsBundleVersion(CommonJsBundle bundle, PackageVersion version) {
    this.bundle = bundle;
    this.packageVersion = version;
    this.version = version.getVersion().toOsgiVersion();

    this.cache = new CommonJsCache(
        bundle.getRepository().getCache(),
        bundle.getModuleName(),
        version.getVersion());
  }

  protected static Filter parseSemverRange(String versionRangeString) {
    return new Range(versionRangeString).toOsgiFilter();
  }

  public CommonJsBundle getBundle() {
    return bundle;
  }

  public uk.co.saiman.webmodules.semver.Version getSemver() {
    return packageVersion.getVersion();
  }

  public Version getVersion() {
    return version;
  }

  protected Stream<String> getDependencies() {
    // TODO Auto-generated method stub
    return Stream.empty();
  }

  public Optional<String> getSha1() {
    return packageVersion.getSha1().map(String::toUpperCase);
  }

  public synchronized CommonJsResource getResource() {
    if (resource == null) {
      resource = new CommonJsResource(getBundle().getModuleName(), getVersion(), getPackageJson());
    }
    return resource;
  }

  synchronized JSONObject getPackageJson() {
    if (packageJson == null) {
      try {
        packageJson = new JSONObject(new JSONTokener(newInputStream(fetchPackageJson())));
      } catch (JSONException | IOException e) {
        throw new RegistryResolutionException("Failed to open " + PACKAGE_JSON, e);
      }
    }
    return packageJson;
  }

  private Path fetchPackageJson() {
    return cache.fetchResource(PACKAGE_JSON, entry -> {
      if (packageVersion.getArchives().anyMatch(ArchiveType.TARBALL::equals)) {
        extractTarballPackageJson(packageVersion.getArchive(ArchiveType.TARBALL), entry);

      } else {
        throw new RegistryResolutionException(
            "No supported archive types amongst candidates "
                + packageVersion.getArchives().collect(toList()));
      }
    });
  }

  private void extractTarballPackageJson(Archive archive, CacheEntry entry) {
    try (TarGzInputStream input = new TarGzInputStream(
        archive.getURL().openStream(),
        getSha1().orElse(null))) {

      input.findEntry(PACKAGE_ROOT + PACKAGE_JSON);

      entry.writeBytes(getBytes(input));
    } catch (IOException e) {
      throw new RegistryResolutionException(
          "Failed to extract archive from URL " + packageVersion.getUrl(),
          e);
    }
  }

  public synchronized Path getPackageDist() {
    if (packageDist == null) {
      packageDist = fetchPackageDist();
    }
    return packageDist;
  }

  private Path fetchPackageDist() {
    return cache.fetchResource(DIST, entry -> {
      if (packageVersion.getArchives().anyMatch(ArchiveType.TARBALL::equals)) {
        extractTarballPackageDist(packageVersion.getArchive(ArchiveType.TARBALL), entry);

      } else {
        throw new RegistryResolutionException(
            "No supported archive types amongst candidates "
                + packageVersion.getArchives().collect(toList()));
      }
    });
  }

  private void extractTarballPackageDist(Archive archive, CacheEntry entry) {
    try (TarGzInputStream input = new TarGzInputStream(
        archive.getURL().openStream(),
        getSha1().orElse(null))) {

      TarArchiveEntry tarEntry = input.getNextTarEntry();
      while (tarEntry != null) {
        if (tarEntry.isFile()) {
          entry.writeBytes(tarEntry.getName(), getBytes(input));
        }
        tarEntry = input.getNextTarEntry();
      }
    } catch (IOException e) {
      throw new RegistryResolutionException(
          "Failed to extract archive from URL " + packageVersion.getUrl(),
          e);
    }
  }

  public synchronized CommonJsJar getJar() {
    if (bundleJar == null) {
      bundleJar = new CommonJsJar(
          cache,
          getResource(),
          getPackageDist(),
          getBundle().getModuleName(),
          getBundle().getBundleSymbolicName(),
          getVersion(),
          getBundle().getRepository().getBundleSymbolicNamePrefix());
    }
    return bundleJar;
  }
}
