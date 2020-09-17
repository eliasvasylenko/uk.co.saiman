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
 * This file is part of uk.co.saiman.copley.
 *
 * uk.co.saiman.copley is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.copley is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.comms.copley;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedType;

import org.osgi.service.component.annotations.Component;

import uk.co.saiman.bytes.BitArray;
import uk.co.saiman.bytes.conversion.ByteConversionAnnotations;
import uk.co.saiman.bytes.conversion.ByteConverter;
import uk.co.saiman.bytes.conversion.ByteConverterProvider;
import uk.co.saiman.bytes.conversion.ByteConverterService;
import uk.co.saiman.bytes.conversion.ClassByteConverterProvider;
import uk.co.saiman.bytes.conversion.Convertible;

@Component(service = ByteConverterProvider.class)
public class AmplifierStateByteConverter extends ClassByteConverterProvider<AmplifierState> {
  @Convertible
  public @interface Marker {}

  public AmplifierStateByteConverter() {
    super(AmplifierState.class);
  }

  @Override
  public boolean supportsAnnotation(Class<? extends Annotation> annotationType) {
    return Marker.class.equals(annotationType);
  }

  @Override
  public ByteConverter<AmplifierState> getClassConverter(
      AnnotatedType type,
      ByteConversionAnnotations annotations,
      ByteConverterService converters) {
    return new ByteConverter<AmplifierState>() {
      @Override
      public BitArray toBits(AmplifierState object) {
        return BitArray.fromInt(object.mode.getCode()).resize(16);
      }

      @Override
      public AmplifierState toObject(BitArray bits) {
        AmplifierState state = new AmplifierState();
        state.mode = AmplifierMode.forCode(bits.resize(16).toInt());
        return state;
      }
    };
  }
}
