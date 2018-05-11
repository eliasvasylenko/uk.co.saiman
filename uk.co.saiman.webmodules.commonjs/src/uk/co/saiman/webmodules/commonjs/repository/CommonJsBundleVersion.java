package uk.co.saiman.webmodules.commonjs.repository;

import static java.nio.file.Files.newInputStream;
import static java.nio.file.Files.walk;
import static java.util.stream.Collectors.toList;
import static uk.co.saiman.webmodules.commonjs.repository.CommonJsCache.getBytes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Stream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.Version;

import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.FileResource;
import aQute.bnd.osgi.Jar;
import uk.co.saiman.webmodules.commonjs.registry.Archive;
import uk.co.saiman.webmodules.commonjs.registry.ArchiveType;
import uk.co.saiman.webmodules.commonjs.registry.PackageVersion;
import uk.co.saiman.webmodules.commonjs.registry.RegistryResolutionException;
import uk.co.saiman.webmodules.semver.Range;

public class CommonJsBundleVersion {
  private static final String PACKAGE_ROOT = "package/";
  private static final String PACKAGE_JSON = "package.json";
  private static final String DIST = "dist";

  static final String RESOURCE_ROOT = "static";

  private static final String JAR = ".jar";

  private final CommonJsBundle bundle;
  private final PackageVersion packageVersion;
  private final Version version;

  private final CommonJsCache cache;

  private JSONObject packageJson;
  private CommonJsResource resource;
  private Path packageDist;
  private Path bundleJar;

  public CommonJsBundleVersion(CommonJsBundle bundle, PackageVersion version) {
    this.bundle = bundle;
    this.packageVersion = version;
    this.version = parseSemver(version.getVersion());

    this.cache = new CommonJsCache(this);
  }

  protected static Version parseSemver(String versionString) {
    org.osgi.framework.Version osgiVersion = new uk.co.saiman.webmodules.semver.Version(
        versionString).toOsgiVersion();

    return new Version(
        osgiVersion.getMajor(),
        osgiVersion.getMinor(),
        osgiVersion.getMicro(),
        osgiVersion.getQualifier());
  }

  protected static Filter parseSemverRange(String versionRangeString) {
    return new Range(versionRangeString).toOsgiFilter();
  }

  public CommonJsBundle getBundle() {
    return bundle;
  }

  public String getSemver() {
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
      resource = new CommonJsResource(this);
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

  public synchronized Path getBundleJar() {
    if (bundleJar == null) {
      bundleJar = writeBundleJar();
    }
    return bundleJar;
  }

  public Path writeBundleJar() {
    return cache.fetchUnstableResource(getBundle().getBundleSymbolicName() + JAR, entry -> {
      try (Jar jar = new Jar(getBundle().getBundleSymbolicName())) {

        jar.setManifest(generateManifest());

        Path dist = getPackageDist();
        walk(dist).filter(Files::isRegularFile).forEach(file -> {
          try {
            String location = Paths
                .get(RESOURCE_ROOT)
                .resolve(dist.resolve(PACKAGE_ROOT).relativize(file))
                .toString();
            jar.putResource(location, new FileResource(file));
          } catch (IOException e) {
            throw new RegistryResolutionException(
                "Failed to write dist file to jar " + entry.getLocation(),
                e);
          }
        });

        jar.write(entry.getLocation().toFile());
      } catch (Exception e) {
        throw new RegistryResolutionException(
            "Failed to write jar to cache directory " + entry.getLocation(),
            e);
      }
    });
  }

  private Manifest generateManifest() {
    Manifest manifest = new Manifest();
    Attributes main = manifest.getMainAttributes();

    main.put(Attributes.Name.MANIFEST_VERSION, "1.0");
    main.putValue(Constants.BUNDLE_MANIFESTVERSION, "2");
    main.putValue(Constants.BUNDLE_NAME, getBundle().getModuleName());
    main.putValue(Constants.BUNDLE_SYMBOLICNAME, getBundle().getBundleSymbolicName());
    main.putValue(Constants.BUNDLE_VERSION, getVersion().toString());
    main.putValue(Constants.EXPORT_PACKAGE, getBundle().getBundleSymbolicName());

    Parameters requirements = new Parameters();
    Parameters capabilities = new Parameters();

    getResource()
        .getRequirements()
        .forEach(req -> requirements.add(req.getNamespace(), req.toAttrs()));
    getResource()
        .getCapabilities()
        .forEach(cap -> capabilities.add(cap.getNamespace(), cap.toAttrs()));

    main.putValue(Constants.REQUIRE_CAPABILITY, requirements.toString());
    main.putValue(Constants.PROVIDE_CAPABILITY, capabilities.toString());

    return manifest;
  }
}
