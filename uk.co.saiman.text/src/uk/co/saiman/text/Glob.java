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

import java.util.regex.Pattern;

public class Glob {
  private final String pattern;
  private final Pattern regexPattern;

  public Glob(String pattern) {
    this.pattern = pattern;
    this.regexPattern = Pattern.compile(convertGlobToRegex(pattern));
  }

  public boolean matches(String text) {
    return regexPattern.matcher(text).matches();
  }

  @Override
  public String toString() {
    return pattern;
  }

  static final String convertGlobToRegex(String pattern) {
    StringBuilder sb = new StringBuilder(pattern.length());
    int inGroup = 0;
    int inClass = 0;
    int firstIndexInClass = -1;
    char[] arr = pattern.toCharArray();
    for (int i = 0; i < arr.length; i++) {
      char ch = arr[i];
      switch (ch) {
      case '\\':
        if (++i >= arr.length) {
          sb.append('\\');
        } else {
          char next = arr[i];
          switch (next) {
          case ',':
            // escape not needed
            break;
          case 'Q':
          case 'E':
            // extra escape needed
            sb.append('\\');
          default:
            sb.append('\\');
          }
          sb.append(next);
        }
        break;
      case '*':
        if (inClass == 0)
          sb.append(".*");
        else
          sb.append('*');
        break;
      case '?':
        if (inClass == 0)
          sb.append('.');
        else
          sb.append('?');
        break;
      case '[':
        inClass++;
        firstIndexInClass = i + 1;
        sb.append('[');
        break;
      case ']':
        inClass--;
        sb.append(']');
        break;
      case '.':
      case '(':
      case ')':
      case '+':
      case '|':
      case '^':
      case '$':
      case '@':
      case '%':
        if (inClass == 0 || (firstIndexInClass == i && ch == '^'))
          sb.append('\\');
        sb.append(ch);
        break;
      case '!':
        if (firstIndexInClass == i)
          sb.append('^');
        else
          sb.append('!');
        break;
      case '{':
        inGroup++;
        sb.append('(');
        break;
      case '}':
        inGroup--;
        sb.append(')');
        break;
      case ',':
        if (inGroup > 0)
          sb.append('|');
        else
          sb.append(',');
        break;
      default:
        sb.append(ch);
      }
    }
    return sb.toString();
  }
}
