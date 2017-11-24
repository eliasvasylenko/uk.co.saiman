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
import static uk.co.saiman.collection.StreamUtilities.upcastStream;
import static uk.co.saiman.experiment.impl.XmlPersistedState.CONFIGURATION_ELEMENT;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import uk.co.saiman.experiment.persistence.PersistedState;
import uk.co.saiman.experiment.persistence.PersistedStateList;

public class XmlPersistedStateList implements PersistedStateList {
  private static final String CONFIGURATION_LIST_ITEM_ELEMENT = "element";

  private final List<XmlPersistedState> maps = new ArrayList<>();

  private final Runnable update;

  public XmlPersistedStateList(Runnable update) {
    this.update = update;
  }

  private void update() {
    update.run();
  }

  public XmlPersistedStateList setImpl(PersistedStateList stateList) {
    maps.forEach(XmlPersistedState::removeImpl);
    maps.clear();
    for (PersistedState state : stateList) {
      maps.add(new XmlPersistedState(update).mergeImpl(state));
    }
    return this;
  }

  boolean isEmpty() {
    return maps.stream().allMatch(XmlPersistedState::isEmpty);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Iterator<PersistedState> iterator() {
    // TODO stupid casting, wait for JEP 300
    return (Iterator<PersistedState>) (Iterator<?>) maps.iterator();
  }

  @Override
  public XmlPersistedState add() {
    return add(size());
  }

  @Override
  public XmlPersistedState add(int index) {
    XmlPersistedState element = new XmlPersistedState(update);
    maps.add(index, element);
    update();
    return element;
  }

  @Override
  public XmlPersistedState get(int index) {
    return maps.get(index);
  }

  @Override
  public XmlPersistedState remove(int index) {
    XmlPersistedState removed = maps.remove(index).removeImpl();
    update();
    return removed;
  }

  @Override
  public int size() {
    return maps.size();
  }

  @Override
  public Stream<PersistedState> stream() {
    return upcastStream(maps.stream());
  }

  protected void save(Element parent) {
    Element configuration = parent.getOwnerDocument().createElement(CONFIGURATION_ELEMENT);
    parent.appendChild(configuration);

    maps.forEach(element -> {
      if (!element.isEmpty()) {
        Element map = parent.getOwnerDocument().createElement(CONFIGURATION_LIST_ITEM_ELEMENT);
        configuration.appendChild(map);
        element.saveInline(map);
      }
    });
  }

  protected XmlPersistedStateList load(Element configuration, XPath xPath)
      throws XPathExpressionException {
    NodeList elements = (NodeList) xPath
        .evaluate(CONFIGURATION_LIST_ITEM_ELEMENT, configuration, NODESET);
    for (int i = 0; i < elements.getLength(); i++) {
      Element element = (Element) elements.item(i);
      add().loadInline(element, xPath);
    }

    return this;
  }
}
