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
import static javax.script.ScriptContext.ENGINE_SCOPE;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;

public class Transpiler {
  private static final String RESOURCE_ROOT = "/static/";
  private static final String BABEL_SCRIPT = RESOURCE_ROOT + "babel.js";
  private static final String ENGINE_NAME = "nashorn";

  private static final String PLUGINS_KEY = "plugins";
  private static final String PRESETS_KEY = "presets";
  private static final String INPUT_KEY = "input";

  private static final String TEMP_ARRAY = "tempArray";
  private static final String ASSIGN_FROM_TEMP_ARRAY = "%s=Java.from(" + TEMP_ARRAY + ")";
  private static final String TRANSFORM_SCRIPT = "Babel.transform(input, { plugins: plugins, presets: presets }).code";

  private static final String ERROR_INITIALIZING_BABEL = "Error initialising Babel";

  private final ScriptEngine engine;
  private final ScriptContext context;

  public Transpiler() {
    System.setProperty("nashorn.args", "--language=es6");

    ScriptEngine engine = new ScriptEngineManager().getEngineByName(ENGINE_NAME);
    ScriptContext context = new SimpleScriptContext();

    context.setBindings(engine.createBindings(), ENGINE_SCOPE);

    try (InputStream babelScript = Transpiler.class.getResourceAsStream(BABEL_SCRIPT)) {
      engine.eval(new InputStreamReader(babelScript), context);

      this.engine = engine;
      this.context = context;
    } catch (Throwable e) {
      throw new IllegalArgumentException(ERROR_INITIALIZING_BABEL, e);
    }
  }

  public String transpile(
      String source,
      Collection<? extends String> plugins,
      Collection<? extends String> presets) throws Exception {
    context.getBindings(ENGINE_SCOPE).put(INPUT_KEY, source);
    putArray(PLUGINS_KEY, plugins);
    putArray(PRESETS_KEY, presets);

    return engine.eval(TRANSFORM_SCRIPT, context).toString();
  }

  private void putArray(String name, Collection<? extends String> array) throws ScriptException {
    context.getBindings(ENGINE_SCOPE).put(TEMP_ARRAY, new ArrayList<>(array));
    engine.eval(format(ASSIGN_FROM_TEMP_ARRAY, name), context);
    context.getBindings(ENGINE_SCOPE).remove(TEMP_ARRAY);
  }
}
