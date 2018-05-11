package uk.co.saiman.webmodules.commonjs.repository;

import static java.lang.Character.isBmpCodePoint;
import static java.lang.String.format;
import static java.nio.file.Files.walk;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.concat;
import static org.osgi.framework.Constants.VERSION_ATTRIBUTE;
import static uk.co.saiman.webmodules.WebModulesConstants.WEB_MODULE_CAPABILITY;
import static uk.co.saiman.webmodules.commonjs.repository.CommonJsBundleVersion.PACKAGE_ROOT;
import static uk.co.saiman.webmodules.commonjs.repository.CommonJsBundleVersion.RESOURCE_ROOT;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Stream;

import org.osgi.annotation.bundle.Requirement;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;

import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.EmbeddedResource;
import aQute.bnd.osgi.FileResource;
import aQute.bnd.osgi.Jar;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.enumeration.EnumerationDescription;
import net.bytebuddy.description.enumeration.EnumerationDescription.ForLoadedEnumeration;
import net.bytebuddy.description.type.TypeDescription.ForLoadedType;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import uk.co.saiman.webmodules.commonjs.registry.RegistryResolutionException;

public class CommonJsJar {
  private static final String REQUIREMENT_ANNOTATION_NAME = "Require%sWebModule";
  private static final String REQUIREMENT_ANNOTATION_NAME_ATTRIBUTE = "name";
  private static final String REQUIREMENT_ANNOTATION_NAMESPACE_ATTRIBUTE = "namespace";

  private static final String CLASS_EXTENSION = ".class";
  private static final String JAR_EXTENSION = ".jar";

  private final String name;
  private final String bsn;
  private final Version version;

  private final String packagePrefix;

  private final Manifest manifest;

  private final Path path;

  public CommonJsJar(
      CommonJsCache cache,
      CommonJsResource resource,
      Path resources,
      String name,
      String bsn,
      Version version,
      String packagePrefix) {
    this.name = name;
    this.bsn = bsn;
    this.version = version;

    this.packagePrefix = packagePrefix;

    this.manifest = generateManifest(resource);
    this.path = writeBundleJar(cache, resources);
  }

  public String getBsn() {
    return bsn;
  }

  public Version getVersion() {
    return version;
  }

  public String getName() {
    return name;
  }

  public Manifest getManifest() {
    return manifest;
  }

  public Path getPath() {
    return path;
  }

  private Manifest generateManifest(CommonJsResource resource) {
    Manifest manifest = new Manifest();
    Attributes main = manifest.getMainAttributes();

    main.put(Attributes.Name.MANIFEST_VERSION, "1.0");
    main.putValue(Constants.BUNDLE_MANIFESTVERSION, "2");
    main.putValue(Constants.BUNDLE_NAME, name);
    main.putValue(Constants.BUNDLE_SYMBOLICNAME, bsn);
    main.putValue(Constants.BUNDLE_VERSION, version.toString());
    main.putValue(Constants.EXPORT_PACKAGE, getJavaPackageNames().collect(joining(".")));

    Parameters requirements = new Parameters();
    Parameters capabilities = new Parameters();

    resource.getRequirements().forEach(req -> requirements.add(req.getNamespace(), req.toAttrs()));
    resource.getCapabilities().forEach(cap -> capabilities.add(cap.getNamespace(), cap.toAttrs()));

    main.putValue(Constants.REQUIRE_CAPABILITY, requirements.toString());
    main.putValue(Constants.PROVIDE_CAPABILITY, capabilities.toString());

    return manifest;
  }

  private Path writeBundleJar(CommonJsCache cache, Path resources) {
    return cache.fetchUnstableResource(bsn + JAR_EXTENSION, entry -> {
      try (Jar jar = new Jar(bsn)) {

        jar.setManifest(manifest);

        walk(resources).filter(Files::isRegularFile).forEach(file -> {
          try {
            String location = Paths
                .get(RESOURCE_ROOT)
                .resolve(resources.resolve(PACKAGE_ROOT).relativize(file))
                .toString();
            jar.putResource(location, new FileResource(file));
          } catch (IOException e) {
            throw new RegistryResolutionException(
                "Failed to write dist file to jar " + entry.getLocation(),
                e);
          }
        });

        Unloaded<? extends Annotation> annotationBytes = new ByteBuddy()
            .makeAnnotation()
            .name(getJavaAnnotationNames().collect(joining(".")))
            .annotateType(
                AnnotationDescription.Builder
                    .ofType(Retention.class)
                    .define("value", RetentionPolicy.CLASS)
                    .build())
            .annotateType(
                AnnotationDescription.Builder
                    .ofType(Target.class)
                    .define(
                        "value",
                        AnnotationValue.ForDescriptionArray
                            .of(
                                new ForLoadedType(ElementType.class),
                                new EnumerationDescription[] {
                                    new ForLoadedEnumeration(ElementType.TYPE),
                                    new ForLoadedEnumeration(ElementType.PACKAGE) }))
                    .build())
            .annotateType(
                AnnotationDescription.Builder
                    .ofType(Requirement.class)
                    .define(REQUIREMENT_ANNOTATION_NAMESPACE_ATTRIBUTE, WEB_MODULE_CAPABILITY)
                    .define(REQUIREMENT_ANNOTATION_NAME_ATTRIBUTE, name)
                    .define(VERSION_ATTRIBUTE, version.toString())
                    .build())
            .make();

        jar
            .putResource(
                getJavaAnnotationNames().collect(joining("/", "/", CLASS_EXTENSION)),
                new EmbeddedResource(annotationBytes.getBytes(), 0));

        jar.write(entry.getLocation().toFile());
      } catch (Exception e) {
        throw new RegistryResolutionException(
            "Failed to write jar to cache directory " + entry.getLocation(),
            e);
      }
    });
  }

  public Stream<String> getJavaPackageNames() {
    return Stream.concat(stream(packagePrefix.split("\\.")), getJavaNameParts(name, true));
  }

  public Stream<String> getJavaAnnotationNames() {
    String annotationName = format(
        REQUIREMENT_ANNOTATION_NAME,
        getJavaNameParts(name, false).map(CommonJsJar::capitalize).collect(joining()));
    return concat(getJavaPackageNames(), Stream.of(annotationName));
  }

  static String capitalize(String name) {
    int[] codePoints = name.codePoints().toArray();
    if (Character.isLetter(codePoints[0])) {
      codePoints[0] = Character.toUpperCase(codePoints[0]);
    }
    return new String(codePoints, 0, codePoints.length);
  }

  static Stream<String> getJavaNameParts(String name, boolean separated) {
    List<String> parts = new ArrayList<>();
    StringBuilder builder = null;

    for (int codePoint : name.codePoints().toArray()) {
      Optional<Character> character = isBmpCodePoint(codePoint)
          ? Optional.of((char) codePoint)
          : Optional.empty();

      if (character.filter(c -> c == '_' || c == '-' || c == '.').isPresent()) {
        if (builder != null) {
          parts.add(builder.toString());
          builder = null;
        }
      } else {
        if (builder == null) {
          builder = new StringBuilder();

          if ((parts.isEmpty() || separated)
              && character.map(c -> !Character.isJavaIdentifierStart(c)).orElse(false)) {
            builder.append('_');
          }
        }

        if (Character.isJavaIdentifierPart(codePoint)) {
          builder.appendCodePoint(codePoint);
        } else {
          builder.append('_');
        }
      }
    }

    if (builder != null) {
      parts.add(builder.toString());
    }

    return parts.stream();
  }
}
