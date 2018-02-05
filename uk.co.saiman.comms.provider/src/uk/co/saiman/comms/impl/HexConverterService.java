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
 * This file is part of uk.co.saiman.comms.provider.
 *
 * uk.co.saiman.comms.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.impl;

import java.nio.ByteBuffer;

import org.apache.felix.service.command.Converter;
import org.osgi.service.component.annotations.Component;

import uk.co.saiman.comms.HexConverter;

/**
 * Converter from hex strings to byte buffers for the GoGo shell
 * 
 * @author Elias N Vasylenko
 */
@Component
public class HexConverterService extends HexConverter implements Converter {
  @Override
  public Object convert(Class<?> type, Object object) {
    if (type.isAssignableFrom(ByteBuffer.class)) {
      if (object instanceof CharSequence) {
        return super.convert(object.toString());
      }
    }

    return null;
  }

  @Override
  public CharSequence format(Object object, int p1, Converter p2) {
    if (object instanceof ByteBuffer) {
      return super.format((ByteBuffer) object);
    }

    return null;
  }
}
