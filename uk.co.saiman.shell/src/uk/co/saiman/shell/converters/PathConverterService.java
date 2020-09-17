/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.shell.
 *
 * uk.co.saiman.shell is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.shell is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.shell.converters;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.felix.service.command.Converter;
import org.osgi.service.component.annotations.Component;

/**
 * Converter from strings to paths for the GoGo shell
 * 
 * @author Elias N Vasylenko
 */
@Component
public class PathConverterService implements Converter {
  @Override
  public Object convert(Class<?> type, Object object) {
    if (!(object instanceof CharSequence)) {
      return null;
    }

    if (type.isAssignableFrom(Path.class)) {
      return Path.of(object.toString());

    } else if (type.isAssignableFrom(File.class)) {
      return new File(object.toString());
    }

    return null;
  }

  @Override
  public String format(Object object, int p1, Converter p2) throws IOException {
    if (object instanceof Path) {
      return ((Path) object).toString();

    } else if (object instanceof File) {
      return ((File) object).getCanonicalPath();
    }

    return null;
  }
}
