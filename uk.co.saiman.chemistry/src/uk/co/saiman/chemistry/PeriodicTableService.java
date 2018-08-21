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
 * This file is part of uk.co.saiman.chemistry.
 *
 * uk.co.saiman.chemistry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.chemistry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.chemistry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import uk.co.saiman.chemistry.Element.Group;

/**
 * Loads the default periodic table resource and registers as a service.
 * 
 * @author Elias N Vasylenko
 */
@Component
public class PeriodicTableService {
  private static final String DEFAULT_PERIODIC_TABLE = "Default Periodic Table";

  private static final String PERIODIC_TABLE = "periodicTable";

  private static final String ELEMENT = "element";
  private static final String NAME = "name";
  private static final String DEFAULT_NAME = "Unnamed Element";
  private static final String ATOMIC_NUMBER = "atomicNumber";
  private static final String SYMBOL = "symbol";
  private static final String GROUP = "group";

  private static final String ISOTOPE = "isotope";
  private static final String MASS_NUMBER = "massNumber";
  private static final String MASS = "mass";
  private static final String ABUNDANCE = "abundance";

  // @Reference
  // protected SchemaManager manager;

  /**
   * Activation registers the periodic table service.
   * 
   * @param context
   *          The bundle context in which to register our service
   */
  @SuppressWarnings("javadoc")
  @Activate
  public void activate(BundleContext context)
      throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
    Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
        getClass().getResourceAsStream("PeriodicTable.xml"));
    XPath xPath = XPathFactory.newInstance().newXPath();

    List<Element> elements = new ArrayList<>();

    NodeList elementNodes = (NodeList) xPath
        .evaluate("/" + PERIODIC_TABLE + "/" + ELEMENT, document, XPathConstants.NODESET);

    for (int i = 0; i < elementNodes.getLength(); i++) {
      Node elementNode = elementNodes.item(i);

      Element element = new Element()
          .withName(getString(elementNode, NAME, DEFAULT_NAME))
          .withSymbol(getString(elementNode, SYMBOL))
          .withAtomicNumber(getInt(elementNode, ATOMIC_NUMBER))
          .withGroup(Group.valueOf(getString(elementNode, GROUP)));

      NodeList isotopeNodes = (NodeList) xPath
          .evaluate(ISOTOPE, elementNode, XPathConstants.NODESET);

      for (int j = 0; j < isotopeNodes.getLength(); j++) {
        Node isotopeNode = isotopeNodes.item(j);
        element = element.withIsotope(
            getInt(isotopeNode, MASS_NUMBER),
            getDouble(isotopeNode, MASS),
            getDouble(isotopeNode, ABUNDANCE));
      }

      elements.add(element);
    }

    PeriodicTable periodicTable = new PeriodicTable(DEFAULT_PERIODIC_TABLE, elements);
    context.registerService(PeriodicTable.class, periodicTable, new Hashtable<>());
  }

  private String getString(Node node, String attribute) {
    return node.getAttributes().getNamedItem(attribute).getNodeValue();
  }

  private String getString(Node node, String attribute, String defaultValue) {
    Node attributeNode = node.getAttributes().getNamedItem(attribute);
    if (attributeNode != null)
      return attributeNode.getNodeValue();
    else
      return defaultValue;
  }

  private int getInt(Node node, String attribute) {
    return Integer.parseInt(getString(node, attribute));
  }

  private double getDouble(Node node, String attribute) {
    return Double.parseDouble(getString(node, attribute));
  }
}
