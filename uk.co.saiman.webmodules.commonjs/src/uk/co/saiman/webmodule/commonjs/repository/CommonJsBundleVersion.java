package uk.co.saiman.webmodule.commonjs.repository;

import static java.nio.file.Files.newInputStream;
import static java.util.stream.Collectors.toList;
import static uk.co.saiman.webmodule.commonjs.registry.cache.Cache.getBytes;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.osgi.framework.Version;

import uk.co.saiman.webmodule.EntryPoints;
import uk.co.saiman.webmodule.PackageId;
import uk.co.saiman.webmodule.commonjs.registry.Archive;
import uk.co.saiman.webmodule.commonjs.registry.ArchiveType;
import uk.co.saiman.webmodule.commonjs.registry.PackageVersion;
import uk.co.saiman.webmodule.commonjs.registry.RegistryResolutionException;
import uk.co.saiman.webmodule.commonjs.registry.cache.Cache;
import uk.co.saiman.webmodule.commonjs.registry.cache.CacheEntry;
import uk.co.saiman.webmodule.semver.Range;

public class CommonJsBundleVersion {
  private static final String SHASUM_CACHE = ".shasum";

  static final String PACKAGE_ROOT = "package/";
  private static final String PACKAGE_JSON = "package.json";
  private static final String DIST = "dist";

  static final String RESOURCE_ROOT = "static/";

  private final CommonJsBundle bundle;
  private final PackageVersion packageVersion;
  private final Version version;

  private final Cache cache;

  private JSONObject packageJson;
  private CommonJsResource resource;
  private Path packageDist;
  private CommonJsJar bundleJar;

  public CommonJsBundleVersion(CommonJsBundle bundle, PackageVersion version) {
    this.bundle = bundle;
    this.packageVersion = version;
    this.version = version.getVersion().toOsgiVersion();

    Path cacheRoot = bundle.getRepository().getCache();
    Path cachePath = getSha1()
        .map(sha1 -> cacheRoot.resolve(SHASUM_CACHE).resolve(sha1))
        .orElse(
            cacheRoot
                .resolve(bundle.getModuleName().toString())
                .resolve(version.getVersion().toString()));

    this.cache = new Cache(cachePath);
  }

  public CommonJsBundle getBundle() {
    return bundle;
  }

  public uk.co.saiman.webmodule.semver.Version getSemver() {
    return packageVersion.getVersion();
  }

  public Version getVersion() {
    return version;
  }

  EntryPoints getExplicitEntryPoints() {
    return getBundle().getExplicitEntryPoints(getSemver());
  }

  public Stream<PackageId> getDependencies() {
    return packageVersion.getDependencies();
  }

  public Range getDependencyRange(PackageId module) {
    return packageVersion.getDependencyRange(module);
  }

  public Optional<String> getSha1() {
    return packageVersion.getSha1().map(String::toUpperCase);
  }

  public synchronized CommonJsResource getResource() {
    if (resource == null) {
      resource = new CommonJsResource(
          getBundle().getModuleName(),
          getVersion(),
          getExplicitEntryPoints(),
          getPackageJson());
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

  private Path fetchPackageJson() throws IOException {
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
        archive.getUrl().openStream(),
        getSha1().orElse(null))) {

      input.findEntry(PACKAGE_ROOT + PACKAGE_JSON);

      entry.writeBytes(getBytes(input));
    } catch (IOException e) {
      throw new RegistryResolutionException(
          "Failed to extract archive from URL " + archive.getUrl(),
          e);
    }
  }

  public synchronized Path getPackageDist() {
    if (packageDist == null) {
      try {
        packageDist = fetchPackageDist();
      } catch (JSONException | IOException e) {
        throw new RegistryResolutionException("Failed to open " + DIST, e);
      }
    }
    return packageDist;
  }

  private Path fetchPackageDist() throws IOException {
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
        archive.getUrl().openStream(),
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
          "Failed to extract archive from URL " + archive.getUrl(),
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
