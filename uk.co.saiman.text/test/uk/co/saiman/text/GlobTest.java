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
 * This file is part of uk.co.saiman.text.
 *
 * uk.co.saiman.text is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.text is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.text;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

@SuppressWarnings("javadoc")
public class GlobTest {
  @Test
  public void star_becomes_dot_star() throws Exception {
    assertEquals("gl.*b", Glob.convertGlobToRegex("gl*b"));
  }

  @Test
  public void escaped_star_is_unchanged() throws Exception {
    assertEquals("gl\\*b", Glob.convertGlobToRegex("gl\\*b"));
  }

  @Test
  public void question_mark_becomes_dot() throws Exception {
    assertEquals("gl.b", Glob.convertGlobToRegex("gl?b"));
  }

  @Test
  public void escaped_question_mark_is_unchanged() throws Exception {
    assertEquals("gl\\?b", Glob.convertGlobToRegex("gl\\?b"));
  }

  @Test
  public void character_classes_dont_need_conversion() throws Exception {
    assertEquals("gl[-o]b", Glob.convertGlobToRegex("gl[-o]b"));
  }

  @Test
  public void escaped_classes_are_unchanged() throws Exception {
    assertEquals("gl\\[-o\\]b", Glob.convertGlobToRegex("gl\\[-o\\]b"));
  }

  @Test
  public void negation_in_character_classes() throws Exception {
    assertEquals("gl[^a-n!p-z]b", Glob.convertGlobToRegex("gl[!a-n!p-z]b"));
  }

  @Test
  public void nested_negation_in_character_classes() throws Exception {
    assertEquals("gl[[^a-n]!p-z]b", Glob.convertGlobToRegex("gl[[!a-n]!p-z]b"));
  }

  @Test
  public void escape_carat_if_it_is_the_first_char_in_a_character_class() throws Exception {
    assertEquals("gl[\\^o]b", Glob.convertGlobToRegex("gl[^o]b"));
  }

  @Test
  public void metachars_are_escaped() throws Exception {
    assertEquals("gl..*\\.\\(\\)\\+\\|\\^\\$\\@\\%b", Glob.convertGlobToRegex("gl?*.()+|^$@%b"));
  }

  @Test
  public void metachars_in_character_classes_dont_need_escaping() throws Exception {
    assertEquals("gl[?*.()+|^$@%]b", Glob.convertGlobToRegex("gl[?*.()+|^$@%]b"));
  }

  @Test
  public void escaped_backslash_is_unchanged() throws Exception {
    assertEquals("gl\\\\b", Glob.convertGlobToRegex("gl\\\\b"));
  }

  @Test
  public void slashQ_and_slashE_are_escaped() throws Exception {
    assertEquals("\\\\Qglob\\\\E", Glob.convertGlobToRegex("\\Qglob\\E"));
  }

  @Test
  public void braces_are_turned_into_groups() throws Exception {
    assertEquals("(glob|regex)", Glob.convertGlobToRegex("{glob,regex}"));
  }

  @Test
  public void escaped_braces_are_unchanged() throws Exception {
    assertEquals("\\{glob\\}", Glob.convertGlobToRegex("\\{glob\\}"));
  }

  @Test
  public void commas_dont_need_escaping() throws Exception {
    assertEquals("(glob,regex),", Glob.convertGlobToRegex("{glob\\,regex},"));
  }
}