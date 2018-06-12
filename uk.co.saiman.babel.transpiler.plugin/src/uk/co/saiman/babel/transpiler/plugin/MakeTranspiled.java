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
 * This file is part of uk.co.saiman.babel.transpiler.plugin.
 *
 * uk.co.saiman.babel.transpiler.plugin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.babel.transpiler.plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.babel.transpiler.plugin;

import static java.nio.file.Files.readAllBytes;
import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

import javax.script.ScriptException;

import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.EmbeddedResource;
import aQute.bnd.osgi.Resource;
import aQute.bnd.service.MakePlugin;
import uk.co.saiman.babel.transpiler.Transpiler;

public class MakeTranspiled implements MakePlugin {
  private static final String TRANSPILE_TYPE = "transpile";

  private static final String TYPE_KEY = "type";
  private static final String SOURCE_KEY = "source";
  private static final String PLUGINS_KEY = "plugins";
  private static final String PRESETS_KEY = "presets";

  private static final String NO_SOURCE_ERROR = "No 'source' field in transpile %s";
  private static final String INVALID_SOURCE_ERROR = "Source file %s is invalid in transpile";
  private static final String NO_PLUGINS_ERROR = "No 'plugins' or 'presets' field in transpile %s";
  private static final String ERROR_TRANSPILING = "Error transpiling source %s";
  private static final String ERROR_CREATING_RESOURCE = "Error creating resource %s";

  private static Transpiler TRANSPILER;

  @Override
  public Resource make(Builder builder, String destination, Map<String, String> arguments)
      throws Exception {
    if (!TRANSPILE_TYPE.equals(arguments.get(TYPE_KEY)))
      return null;

    String source = arguments.get(SOURCE_KEY);
    if (source == null) {
      builder.error(NO_SOURCE_ERROR, arguments);
      return null;
    }

    Path sourceFile = builder.getFile(source).toPath();
    if (!Files.isRegularFile(sourceFile)) {
      builder.error(INVALID_SOURCE_ERROR, sourceFile);
      return null;
    }

    String plugins = arguments.get(PLUGINS_KEY);
    String presets = arguments.get(PRESETS_KEY);
    if (plugins == null && presets == null) {
      builder.error(NO_PLUGINS_ERROR, arguments);
      return null;
    }

    return transpile(
        builder,
        sourceFile,
        plugins == null ? emptySet() : asList(plugins.trim().split("\\s*,\\s*")),
        presets == null ? emptySet() : asList(presets.trim().split("\\s*,\\s*")));
  }

  private static synchronized Resource transpile(
      Builder builder,
      Path source,
      Collection<? extends String> plugins,
      Collection<? extends String> presets) throws ScriptException, IOException {
    String sourceString;
    try {
      sourceString = new String(readAllBytes(source));
    } catch (Throwable e) {
      builder.error(INVALID_SOURCE_ERROR, e, source);
      return null;
    }

    String result;
    try {
      if (TRANSPILER == null) {
        TRANSPILER = new Transpiler();
      }
      result = TRANSPILER.transpile(sourceString, plugins, presets);
    } catch (Throwable e) {
      builder.error(ERROR_TRANSPILING, e, source);
      return null;
    }

    try {
      return new EmbeddedResource(result, 0);
    } catch (Throwable e) {
      builder.error(ERROR_CREATING_RESOURCE, e, source);
      return null;
    }
  }
}
