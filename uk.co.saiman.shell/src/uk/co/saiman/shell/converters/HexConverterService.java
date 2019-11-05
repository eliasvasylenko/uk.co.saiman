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

import static org.osgi.namespace.service.ServiceNamespace.CAPABILITY_OBJECTCLASS_ATTRIBUTE;
import static org.osgi.namespace.service.ServiceNamespace.SERVICE_NAMESPACE;
import static uk.co.saiman.bytes.ByteBuffers.fromPrefixedHexString;
import static uk.co.saiman.bytes.ByteBuffers.toPrefixedHexString;
import static uk.co.saiman.shell.converters.RequireConverter.TYPE;

import java.nio.ByteBuffer;

import org.apache.felix.service.command.Converter;
import org.osgi.annotation.bundle.Capability;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.propertytypes.ServiceDescription;

/**
 * Converter from hex strings to byte buffers for the GoGo shell
 * 
 * @author Elias N Vasylenko
 */
@ServiceDescription("java.nio.ByteBuffer")
@Capability(
    namespace = SERVICE_NAMESPACE,
    attribute = {
        CAPABILITY_OBJECTCLASS_ATTRIBUTE + "=org.apache.felix.service.command.Converter",
        TYPE + "=java.nio.ByteBuffer" })
@Component(property = TYPE + "=java.nio.ByteBuffer")
public class HexConverterService implements Converter {
  @Override
  public ByteBuffer convert(Class<?> type, Object object) {
    if (type.isAssignableFrom(ByteBuffer.class)) {
      if (object instanceof CharSequence) {
        try {
          return fromPrefixedHexString(object.toString());
        } catch (Exception e) {
          return null;
        }
      }
    }

    return null;
  }

  @Override
  public String format(Object object, int p1, Converter p2) {
    if (object instanceof ByteBuffer) {
      return toPrefixedHexString((ByteBuffer) object);
    }

    return null;
  }
}
