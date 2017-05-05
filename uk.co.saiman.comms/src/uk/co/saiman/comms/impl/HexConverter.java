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
 * This file is part of uk.co.saiman.comms.
 *
 * uk.co.saiman.comms is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.impl;

import static java.lang.Character.digit;

import java.nio.ByteBuffer;

import org.apache.felix.service.command.Converter;
import org.osgi.service.component.annotations.Component;

/**
 * Converter from hex strings to byte buffers for the GoGo shell
 * 
 * @author Elias N Vasylenko
 */
@Component
public class HexConverter implements Converter {
	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

	@Override
	public Object convert(Class<?> type, Object object) {
		if (type.isAssignableFrom(ByteBuffer.class)) {
			if (object instanceof CharSequence) {
				String hexString = object.toString().trim();

				if (!hexString.startsWith("0x")) {
					return null;
				}

				hexString = hexString.substring(2);

				int len = hexString.length();
				byte[] data = new byte[len / 2];
				for (int i = 0; i < data.length; i++) {
					int j = i + i;
					data[i] = (byte) ((digit(hexString.charAt(j), 16) << 4)
							+ digit(hexString.charAt(j + 1), 16));
				}

				return ByteBuffer.wrap(data);
			}
		}

		return null;
	}

	@Override
	public CharSequence format(Object object, int p1, Converter p2) {
		if (object instanceof ByteBuffer) {
			ByteBuffer buffer = (ByteBuffer) object;
			byte[] bytes = new byte[buffer.remaining()];
			buffer.get(bytes);

			char[] hexChars = new char[bytes.length * 2];
			for (int j = 0; j < bytes.length; j++) {
				int v = bytes[j] & 0xFF;
				hexChars[j * 2] = hexArray[v >>> 4];
				hexChars[j * 2 + 1] = hexArray[v & 0x0F];
			}

			return "0x" + new String(hexChars);
		}

		return null;
	}
}
