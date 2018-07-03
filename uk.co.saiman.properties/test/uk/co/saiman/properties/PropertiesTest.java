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
 * This file is part of uk.co.saiman.properties.
 *
 * uk.co.saiman.properties is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.properties is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static uk.co.saiman.properties.LocaleManager.getManager;

import org.junit.Test;

import uk.co.saiman.properties.LocaleManager;
import uk.co.saiman.properties.PropertyLoader;

@SuppressWarnings("javadoc")
public class PropertiesTest {
  public TestProperties text(LocaleManager manager) {
    try {
      return PropertyLoader.newPropertyLoader(manager).getProperties(TestProperties.class);
    } catch (Throwable t) {
      t.printStackTrace();
      throw t;
    }
  }

  @Test
  public void managerTest() {
    getManager();
  }

  @Test
  public void propertiesTest() {
    text(getManager());
  }

  @Test
  public void simpleTextTest() {
    TestProperties text = text(getManager());

    assertEquals("simple property value", text.simple());
  }

  @Test
  public void substitutionTextTest() {
    TestProperties text = text(getManager());

    assertEquals("value of substitution", text.substitution("substitution"));
  }

  @Test
  public void multipleSubstitutionTextTest() {
    TestProperties text = text(getManager());

    assertEquals(
        "values of substitution one and substitution two",
        text.multipleSubstitution("substitution one", "substitution two"));
  }

  @Test
  public void defaultTextTest() {
    TestProperties text = text(getManager());

    assertEquals("value of default", text.defaultMethod());
  }

  @Test
  public void copyTextTest() {
    TestProperties text = text(getManager());

    assertEquals("simple property value", text.simple());
  }

  @Test
  public void nestedTextTest() {
    TestProperties text = text(getManager());

    assertEquals("nested text value", text.nesting().nestedText());
  }

  @Test
  public void deeplyNestedTextTest() {
    TestProperties text = text(getManager());

    assertEquals("deeply nested text value", text.nesting().deeply().deeplyNestedText());
  }

  @Test
  public void optionalPresentTextTest() {
    TestProperties text = text(getManager());

    assertTrue(text.optional().isPresent());
  }

  @Test
  public void optionalMissingTextTest() {
    TestProperties text = text(getManager());

    assertFalse(text.optionalMissing().isPresent());
  }
}
