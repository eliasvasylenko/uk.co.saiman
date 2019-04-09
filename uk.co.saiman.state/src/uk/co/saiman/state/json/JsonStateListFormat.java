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
 * This file is part of uk.co.saiman.experiment.
 *
 * uk.co.saiman.experiment is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.state.json;

import static uk.co.saiman.data.format.MediaType.APPLICATION_TYPE;
import static uk.co.saiman.data.format.RegistrationTree.VENDOR;

import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONTokener;
import org.osgi.service.component.annotations.Component;

import uk.co.saiman.data.format.MediaType;
import uk.co.saiman.data.format.Payload;
import uk.co.saiman.state.StateList;

@Component
public class JsonStateListFormat extends JsonStateFormat<StateList> {
  public static final int VERSION = 1;

  public static final MediaType MEDIA_TYPE = new MediaType(
      APPLICATION_TYPE,
      "saiman.statelist.v" + VERSION,
      VENDOR).withSuffix("json");

  @Override
  public String getExtension() {
    return "sl";
  }

  @Override
  public Stream<MediaType> getMediaTypes() {
    return Stream.of(MEDIA_TYPE);
  }

  @Override
  public Payload<? extends StateList> decodeString(String string) {
    JSONArray object = new JSONArray(new JSONTokener(string));
    return new Payload<>(toStateList(object));
  }

  @Override
  public String encodeString(Payload<? extends StateList> payload) {
    return toJsonArray(payload.data).toString(2);
  }
}
