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

import static javax.xml.xpath.XPathConstants.NODE;
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
  static final String CONFIGURATION_ELEMENT = "configuration";
  static final String CONFIGURATION_STRING_ELEMENT = "string";
  static final String CONFIGURATION_MAP_ELEMENT = "map";
  static final String CONFIGURATION_MAP_LIST_ELEMENT = "maps";
  static final String CONFIGURATION_ID_ATTRIBUTE = "id";

  private final Map<String, String> strings = new HashMap<>();
  private final Map<String, XmlPersistedState> maps = new HashMap<>();
  private final Map<String, XmlPersistedStateList> mapLists = new HashMap<>();

  private void update() {
    next(this);
  }

  @Override
  public void clear() {
    strings.clear();
    update();
  }

  boolean isEmpty() {
    return strings.isEmpty() && maps.values().stream().allMatch(XmlPersistedState::isEmpty)
        && mapLists.values().stream().allMatch(XmlPersistedStateList::isEmpty);
  }

  @Override
  public Stream<String> getStrings() {
    return strings.keySet().stream();
  }

  @Override
  public Property<String> forString(String id) {
    return Property.over(() -> strings.get(id), v -> strings.put(id, v));
  }

  @Override
  public Stream<String> getMaps() {
    return maps.keySet().stream();
  }

  @Override
  public XmlPersistedState forMap(String id) {
    return maps.computeIfAbsent(id, i -> new XmlPersistedState());
  }

  @Override
  public Stream<String> getMapLists() {
    return mapLists.keySet().stream();
  }

  @Override
  public XmlPersistedStateList forMapList(String id) {
    return mapLists.computeIfAbsent(id, i -> new XmlPersistedStateList());
  }

  protected void save(Element parent) {
    Element configuration = parent.getOwnerDocument().createElement(CONFIGURATION_ELEMENT);
    parent.appendChild(configuration);
  }

  protected void saveInline(Element configuration) {
    getStrings().forEach(id -> {
      Element element = configuration.getOwnerDocument().createElement(
          CONFIGURATION_STRING_ELEMENT);
      configuration.appendChild(element);
      element.setAttribute(CONFIGURATION_ID_ATTRIBUTE, id);
      element.setTextContent(forString(id).get());
    });

    getMaps().forEach(id -> {
      XmlPersistedState map = maps.get(id);
      if (map != null && !map.isEmpty()) {
        Element element = configuration.getOwnerDocument().createElement(CONFIGURATION_MAP_ELEMENT);
        configuration.appendChild(element);
        element.setAttribute(CONFIGURATION_ID_ATTRIBUTE, id);
        map.saveInline(element);
      }
    });

    getMapLists().forEach(id -> {
      XmlPersistedStateList mapList = mapLists.get(id);
      if (mapList != null && !mapList.isEmpty()) {
        Element element = configuration.getOwnerDocument().createElement(
            CONFIGURATION_MAP_LIST_ELEMENT);
        configuration.appendChild(element);
        element.setAttribute(CONFIGURATION_ID_ATTRIBUTE, id);
        mapList.save(element);
      }
    });
  }

  protected XmlPersistedState load(Element parent, XPath xPath) throws XPathExpressionException {
    Element configuration = (Element) xPath.evaluate(CONFIGURATION_ELEMENT, parent, NODE);
    return loadInline(configuration, xPath);
  }

  protected XmlPersistedState loadInline(Element configuration, XPath xPath)
      throws XPathExpressionException {
    NodeList stringElements = (NodeList) xPath
        .evaluate(CONFIGURATION_STRING_ELEMENT, configuration, NODESET);
    for (int i = 0; i < stringElements.getLength(); i++) {
      Element element = (Element) stringElements.item(i);
      forString(element.getAttribute(CONFIGURATION_ID_ATTRIBUTE)).set(element.getTextContent());
    }

    NodeList mapElements = (NodeList) xPath
        .evaluate(CONFIGURATION_MAP_ELEMENT, configuration, NODESET);
    for (int i = 0; i < mapElements.getLength(); i++) {
      Element element = (Element) mapElements.item(i);
      forMap(element.getAttribute(CONFIGURATION_ID_ATTRIBUTE)).loadInline(element, xPath);
    }

    NodeList mapListElements = (NodeList) xPath
        .evaluate(CONFIGURATION_MAP_LIST_ELEMENT, configuration, NODESET);
    for (int i = 0; i < mapListElements.getLength(); i++) {
      Element element = (Element) mapListElements.item(i);
      forMapList(element.getAttribute(CONFIGURATION_ID_ATTRIBUTE)).load(element, xPath);
    }

    return this;
  }
}
