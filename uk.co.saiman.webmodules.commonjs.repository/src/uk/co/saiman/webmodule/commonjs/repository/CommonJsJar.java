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
 * This file is part of uk.co.saiman.webmodules.commonjs.repository.
 *
 * uk.co.saiman.webmodules.commonjs.repository is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.webmodules.commonjs.repository is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.webmodule.commonjs.repository;

import static java.lang.Character.isBmpCodePoint;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Stream.concat;
import static org.osgi.framework.Constants.FILTER_DIRECTIVE;
import static org.osgi.namespace.service.ServiceNamespace.SERVICE_NAMESPACE;
import static uk.co.saiman.webmodule.WebModuleConstants.ID_ATTRIBUTE;
import static uk.co.saiman.webmodule.WebModuleConstants.VERSION_ATTRIBUTE;
import static uk.co.saiman.webmodule.commonjs.cache.Retention.WEAK;
import static uk.co.saiman.webmodule.commonjs.repository.CommonJsBundleVersion.PACKAGE_ROOT;
import static uk.co.saiman.webmodule.commonjs.repository.CommonJsBundleVersion.RESOURCE_ROOT;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.EXTENDER_VERSION;
import static uk.co.saiman.webmodule.extender.WebModuleExtenderConstants.EXTENDER_VERSION_ATTRIBUTE;

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
import aQute.bnd.osgi.resource.FilterBuilder;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.enumeration.EnumerationDescription;
import net.bytebuddy.description.enumeration.EnumerationDescription.ForLoadedEnumeration;
import net.bytebuddy.description.type.TypeDescription.ForLoadedType;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.implementation.attribute.AnnotationValueFilter;
import uk.co.saiman.webmodule.PackageId;
import uk.co.saiman.webmodule.commonjs.RegistryResolutionException;
import uk.co.saiman.webmodule.commonjs.cache.Cache;
import uk.co.saiman.webmodule.extender.RequireWebModuleExtender;

public class CommonJsJar {
  private static final String REQUIREMENT_ANNOTATION_NAME = "Require%sWebModule";
  private static final String REQUIREMENT_ANNOTATION_NAMESPACE_ATTRIBUTE = "namespace";
  private static final String REQUIREMENT_ANNOTATION_ATTRIBUTES_ATTRIBUTE = "attribute";

  private static final String CLASS_EXTENSION = ".class";
  private static final String JAR_EXTENSION = ".jar";

  private final PackageId name;
  private final String bsn;
  private final Version version;

  private final String packagePrefix;

  private final Manifest manifest;

  private final Path path;

  public CommonJsJar(
      Cache cache,
      CommonJsResource resource,
      Path resources,
      PackageId name,
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

  public PackageId getName() {
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
    main.putValue(Constants.BUNDLE_NAME, name.toString());
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

  private Path writeBundleJar(Cache cache, Path resources) {
    try {
      return cache.fetchResource(bsn + JAR_EXTENSION, entry -> {
        try (Jar jar = new Jar(bsn)) {
          jar.setManifest(manifest);

          Files.walk(resources).filter(Files::isRegularFile).forEach(file -> {
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
              .with(AnnotationValueFilter.Default.SKIP_DEFAULTS)
              .makeAnnotation()
              .name(getJavaAnnotationNames().collect(joining(".")))
              .annotateType(createRequireExtenderAnnotation())
              .annotateType(createRetentionAnnotation())
              .annotateType(createTargetAnnotation())
              .annotateType(createRequirementAnnotation())
              .make();

          jar
              .putResource(
                  getJavaAnnotationNames().collect(joining("/", "/", CLASS_EXTENSION)),
                  new EmbeddedResource(annotationBytes.getBytes(), 0));

          jar.write(entry.getLocation().toFile());
        }
      }, WEAK);
    } catch (Exception e) {
      throw new RegistryResolutionException(
          "Failed to write jar to cache directory " + cache.getCacheRoot(),
          e);
    }
  }

  private AnnotationDescription createRequireExtenderAnnotation() {
    return AnnotationDescription.Builder.ofType(RequireWebModuleExtender.class).build();
  }

  private AnnotationDescription createRetentionAnnotation() {
    return AnnotationDescription.Builder
        .ofType(Retention.class)
        .define("value", RetentionPolicy.CLASS)
        .build();
  }

  private AnnotationDescription createTargetAnnotation() {
    return AnnotationDescription.Builder
        .ofType(Target.class)
        .define(
            "value",
            AnnotationValue.ForDescriptionArray
                .of(
                    new ForLoadedType(ElementType.class),
                    new EnumerationDescription[] {
                        new ForLoadedEnumeration(ElementType.TYPE),
                        new ForLoadedEnumeration(ElementType.PACKAGE) }))
        .build();
  }

  private AnnotationDescription createRequirementAnnotation() {
    return AnnotationDescription.Builder
        .ofType(Requirement.class)
        .define(REQUIREMENT_ANNOTATION_NAMESPACE_ATTRIBUTE, SERVICE_NAMESPACE)
        .define(FILTER_DIRECTIVE, new FilterBuilder().eq(ID_ATTRIBUTE, name.toString()).toString())
        .define(VERSION_ATTRIBUTE, version.toString())
        .defineArray(
            REQUIREMENT_ANNOTATION_ATTRIBUTES_ATTRIBUTE,
            new String[] { EXTENDER_VERSION_ATTRIBUTE + "=" + EXTENDER_VERSION })
        .build();
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

  static Stream<String> getJavaNameParts(PackageId name, boolean separated) {
    return Stream
        .concat(
            name.getScope().map(s -> getJavaNameParts(s, separated)).orElse(Stream.empty()),
            getJavaNameParts(name.getName(), separated));
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
