/*
 * Copyright (C) 2017 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.babel.transpiler.
 *
 * uk.co.saiman.babel.transpiler is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.babel.transpiler is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.babel.transpiler;

import static java.lang.String.format;
import static java.nio.file.Files.readAllBytes;
import static java.util.Arrays.asList;
import static uk.co.saiman.babel.standalone.BabelStandaloneConstants.BABEL_STANDALONE_WEB_RESOURCE_VERSION;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.EmbeddedResource;
import aQute.bnd.osgi.Resource;
import aQute.bnd.service.MakePlugin;

public class MakeTranspiled implements MakePlugin {
	private static final String RESOURCSE_ROOT = "/META-INF/resources/webjars/babel-standalone/"
			+ BABEL_STANDALONE_WEB_RESOURCE_VERSION + "/";
	private static final String BABEL_SCRIPT = RESOURCSE_ROOT + "babel.js";
	private static final String ENGINE_MIME_TYPE = "text/javascript";

	private static final String TRANSPILE_TYPE = "transpile";

	private static final String TYPE_KEY = "type";
	private static final String SOURCE_KEY = "source";
	private static final String PLUGINS_KEY = "plugins";
	private static final String PRESETS_KEY = "presets";
	private static final String INPUT_KEY = "input";

	private static final String TEMP_ARRAY = "tempArray";
	private static final String ASSIGN_FROM_TEMP_ARRAY = "%s=Java.from(" + TEMP_ARRAY + ")";
	private static final String TRANSFORM_SCRIPT = "Babel.transform(input, { plugins: plugins, presets: presets }).code";

	private static final String NO_SOURCE_ERROR = "No 'source' field in transpile %s";
	private static final String NO_PLUGINS_ERROR = "No 'plugins' or 'presets' field in transpile %s";

	private static ScriptEngine ENGINE;
	private static Bindings BINDINGS;

	@Override
	public Resource make(Builder builder, String destination, Map<String, String> arguments)
			throws Exception {
		if (!TRANSPILE_TYPE.equals(arguments.get(TYPE_KEY)))
			return null;

		String source = arguments.get(SOURCE_KEY);
		if (source == null)
			throw new IllegalArgumentException(format(NO_SOURCE_ERROR, arguments));

		String plugins = arguments.get(PLUGINS_KEY);
		String presets = arguments.get(PRESETS_KEY);
		if (plugins == null && presets == null)
			throw new IllegalArgumentException(format(NO_PLUGINS_ERROR, arguments));

		return transpile(
				builder.getFile(source).toPath(),
				plugins == null ? new String[] {} : plugins.split(","),
				presets == null ? new String[] {} : presets.split(","));
	}

	private Resource transpile(Path source, String[] plugins, String[] presets) throws Exception {
		return new EmbeddedResource(transpile(new String(readAllBytes(source)), plugins, presets), 0);
	}

	private synchronized String transpile(String source, String[] plugins, String[] presets)
			throws Exception {
		if (ENGINE == null) {
			try (InputStream babelScript = getClass().getResourceAsStream(BABEL_SCRIPT)) {
				ENGINE = new ScriptEngineManager().getEngineByMimeType(ENGINE_MIME_TYPE);
				BINDINGS = new SimpleBindings();
				ENGINE.eval(new InputStreamReader(babelScript), BINDINGS);
			}
		}

		BINDINGS.put(INPUT_KEY, source);
		putArray(PLUGINS_KEY, plugins);
		putArray(PRESETS_KEY, presets);
		return ENGINE.eval(TRANSFORM_SCRIPT, BINDINGS).toString();
	}

	private void putArray(String name, String[] array) throws ScriptException {
		BINDINGS.put(TEMP_ARRAY, asList(array));
		ENGINE.eval(format(ASSIGN_FROM_TEMP_ARRAY, name), BINDINGS);
		BINDINGS.remove(TEMP_ARRAY);
	}
}
