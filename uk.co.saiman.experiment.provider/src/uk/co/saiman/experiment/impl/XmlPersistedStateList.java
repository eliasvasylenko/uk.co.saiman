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

import uk.co.saiman.experiment.PersistedState;
import uk.co.saiman.experiment.PersistedStateList;

public class XmlPersistedStateList implements PersistedStateList {
  private static final String CONFIGURATION_LIST_ITEM_ELEMENT = "element";

  private final List<XmlPersistedState> maps = new ArrayList<>();

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
    XmlPersistedState element = new XmlPersistedState();
    maps.add(index, element);
    return element;
  }

  @Override
  public XmlPersistedState get(int index) {
    return maps.get(index);
  }

  @Override
  public XmlPersistedState remove(int index) {
    return maps.remove(index);
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
