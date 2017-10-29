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
 * This file is part of uk.co.saiman.experiment.provider.
 *
 * uk.co.saiman.experiment.provider is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.experiment.provider is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.experiment.impl;

import static javax.xml.xpath.XPathConstants.NODESET;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import uk.co.saiman.experiment.PersistedState;
import uk.co.saiman.observable.HotObservable;
import uk.co.saiman.property.Property;

public class XmlPersistedState extends HotObservable<PersistedState> implements PersistedState {
  private static final String CONFIGURATION_ELEMENT = "configuration";
  private static final String CONFIGURATION_STRING_ELEMENT = "string";
  private static final String CONFIGURATION_KEY_ATTRIBUTE = "key";
  private static final String CONFIGURATION_VALUE_ATTRIBUTE = "value";

  private final Map<String, String> strings = new HashMap<>();

  private void update() {
    next(this);
  }

  @Override
  public Stream<String> getStrings() {
    return strings.keySet().stream();
  }

  public Property<String> stringValue(String key) {
    return Property.over(() -> strings.get(key), v -> strings.put(key, v));
  }

  @Override
  public void clear() {
    strings.clear();
    update();
  }

  protected void save(Element parent) {
    Element configuration = parent.getOwnerDocument().createElement(CONFIGURATION_ELEMENT);
    parent.appendChild(configuration);
    getStrings().forEach(key -> {
      Element string = parent.getOwnerDocument().createElement(CONFIGURATION_STRING_ELEMENT);
      configuration.appendChild(string);
      string.setAttribute(CONFIGURATION_KEY_ATTRIBUTE, key);
      string.setAttribute(CONFIGURATION_VALUE_ATTRIBUTE, stringValue(key).get());
    });
  }

  protected static XmlPersistedState load(Element parent, XPath xPath)
      throws XPathExpressionException {
    XmlPersistedState persistedState = new XmlPersistedState();

    NodeList strings = (NodeList) xPath
        .evaluate(CONFIGURATION_ELEMENT + "/" + CONFIGURATION_STRING_ELEMENT, parent, NODESET);
    for (int i = 0; i < strings.getLength(); i++) {
      Element string = (Element) strings.item(i);
      persistedState.stringValue(string.getAttribute(CONFIGURATION_KEY_ATTRIBUTE)).set(
          string.getAttribute(CONFIGURATION_VALUE_ATTRIBUTE));
    }
    return persistedState;
  }
}
