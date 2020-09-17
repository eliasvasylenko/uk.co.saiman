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
package uk.co.saiman.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.annotation.Testable;

import uk.co.saiman.property.IdentityProperty;

/**
 * It is very difficult or impossible to test this class properly, as the
 * important aspects of its behavior either only occur in extremely rare
 * circumstances, or change slightly with implementation details of the VM, or
 * both.
 * 
 * @author Elias N Vasylenko
 *
 */
@Testable
public class IdentityPropertyTest {
  /**
   * Confirm default constructor initializes with null value
   */
  @Test
  public void testNullInitialisation() {
    assertEquals(null, new IdentityProperty<>().get());
  }

  /**
   * Confirm value constructor initializes properly
   */
  @Test
  public void testValueInitialisation() {
    assertEquals("value", new IdentityProperty<>("value").get());
  }

  /**
   * Confirm property setting works
   */
  @Test
  public void testSet() {
    IdentityProperty<String> property = new IdentityProperty<>();
    property.set("set");
    assertEquals("set", property.get());
  }

  /**
   * Confirm property setting works multiple times
   */
  @Test
  public void testMultipleSet() {
    IdentityProperty<String> property = new IdentityProperty<>();
    property.set("set");
    property.set("again");
    assertEquals("again", property.get());
  }
}
