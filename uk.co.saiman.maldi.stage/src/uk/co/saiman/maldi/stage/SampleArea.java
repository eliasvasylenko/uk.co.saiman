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
 * This file is part of uk.co.saiman.maldi.stage.
 *
 * uk.co.saiman.maldi.stage is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.maldi.stage is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.maldi.stage;

import javax.measure.quantity.Length;

import uk.co.saiman.measurement.coordinate.XYCoordinate;

// TODO sealed/record type, for pattern matching
public abstract class SampleArea {
  private final String id;
  private final SamplePreparation preparation;
  private final XYCoordinate<Length> center;
  private final XYCoordinate<Length> lowerBound;
  private final XYCoordinate<Length> upperBound;

  /*
   * 
   * 
   * 
   * TODO things we'd like to support, how do they fit together?
   * 
   * - manual exact laser positioning by click & arrow keys
   * 
   * - different area shapes (extensible to include e.g. ring with hole in middle)
   * should this be same API as stage itself? i.e. no way to directly inspect
   * shape, just a way to pick a location and test whether it's valid? This would
   * make it easy to implement a "virtual stage" over the area... But harder to
   * render nicely. Maybe also can return a stream of suggested "starting points"?
   * 
   * - raster for imaging
   * 
   * - raster-like patterns for optimizing position (i.e. search for best spot in
   * a grid)
   * 
   * - non-raster-like patterns for optimizing position (e.g. spiral out from
   * center)
   * 
   * TODO things we DON'T need to support
   * 
   * - this is a sample STAGE, over sample PLATES. Let's not get bogged down
   * worrying about entirely different concepts like linear or point
   * configurations, they can be done with separate stage implementations.
   * 
   * - Maybe we don't need to support different patterns at all, and it's enough
   * to support different shapes?
   * 
   * 
   * 
   * 
   * 
   * Maybe all patterns can be raster-like, same as existing MALDI. But we also
   * want to be able to select manual positions. Patterns which collect data about
   * successful raster locations can bin the results of acquiring at a custom
   * location in the nearest raster square.
   * 
   * 
   * 
   * 
   * 
   * TODO do we create a virtual stage/raster device per experiment?
   * 
   * 
   */

  /**
   * 
   * @param id          the id of the sample area
   * @param preparation the preparation to which this sample area belongs
   * @param center      the center of the sample area, from which locations within
   *                    the sample area are specified as offsets
   * @param lowerBound  the lower reachable bound of the sample area, specified as
   *                    an offset from the center
   * @param upperBound  the upper reachable bound of the sample area, specified as
   *                    an offset from the center
   */
  public SampleArea(
      String id,
      SamplePreparation preparation,
      XYCoordinate<Length> center,
      XYCoordinate<Length> lowerBound,
      XYCoordinate<Length> upperBound) {
    this.id = id;
    this.preparation = preparation;
    this.center = center;
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
  }

  /**
   * @return The id of the sample area. This should be unique within the
   *         preparation, though this isn't a requirement.
   */
  public String id() {
    return id;
  }

  /**
   * @return The preparation to which the sample area belongs.
   */
  public SamplePreparation preparation() {
    return preparation;
  }

  public XYCoordinate<Length> center() {
    return center;
  }

  public XYCoordinate<Length> lowerBound() {
    return lowerBound;
  }

  public XYCoordinate<Length> upperBound() {
    return upperBound;
  }

  public abstract boolean isLocationReachable(XYCoordinate<Length> location);
}
