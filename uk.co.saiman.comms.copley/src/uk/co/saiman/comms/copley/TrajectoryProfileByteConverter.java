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
 * This file is part of uk.co.saiman.comms.copley.
 *
 * uk.co.saiman.comms.copley is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.comms.copley is distributed in the hope that it will be useful,
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
public class TrajectoryProfileByteConverter extends ClassByteConverterProvider<TrajectoryProfile> {
  @Convertible
  public @interface Marker {}

  public TrajectoryProfileByteConverter() {
    super(TrajectoryProfile.class);
  }

  @Override
  public boolean supportsAnnotation(Class<? extends Annotation> annotationType) {
    return Marker.class.equals(annotationType);
  }

  @Override
  public ByteConverter<TrajectoryProfile> getClassConverter(
      AnnotatedType type,
      ByteConversionAnnotations annotations,
      ByteConverterService converters) {
    return new ByteConverter<TrajectoryProfile>() {
      @Override
      public BitArray toBits(TrajectoryProfile object) {
        return BitArray
            .fromInt(object.mode.getCode())
            .resize(3)
            .resize(-16)
            .with(8, object.relative);
      }

      @Override
      public TrajectoryProfile toObject(BitArray bits) {
        TrajectoryProfile profile = new TrajectoryProfile();
        profile.mode = TrajectoryProfileMode.forCode(bits.resize(3).toInt());
        profile.relative = bits.get(8);
        return profile;
      }
    };
  }
}
