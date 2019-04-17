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
 * This file is part of uk.co.saiman.utilities.
 *
 * uk.co.saiman.utilities is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.utilities is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.reflection.classloading;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.function.Function;

/**
 * Utilities for safely running code under a different thread context class
 * loader.
 * 
 * @author Elias N Vasylenko
 */
public class ContextClassLoaderExecutor implements Executor {
	private final Executor executor;
	private final Function<ClassLoader, ClassLoader> classLoaderTransformation;

	public ContextClassLoaderExecutor(
			Executor executor,
			Function<ClassLoader, ClassLoader> classLoaderTransformation) {
		this.executor = executor;
		this.classLoaderTransformation = classLoaderTransformation;
	}

	public ContextClassLoaderExecutor(Executor executor, ClassLoader classLoader) {
		this(executor, c -> classLoader);
	}

	public ContextClassLoaderExecutor(Executor executor, URL... jars) {
		this(executor, c -> new URLClassLoader(jars, c));
	}

	public ContextClassLoaderExecutor(Executor executor, Collection<URL> jars) {
		this(executor, jars.toArray(new URL[jars.size()]));
	}

	@Override
	public void execute(Runnable command) {
		executor.execute(() -> {
			ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
			Thread.currentThread().setContextClassLoader(
					classLoaderTransformation.apply(originalClassLoader));

			try {
				command.run();
			} finally {
				Thread.currentThread().setContextClassLoader(originalClassLoader);
			}
		});
	}
}
