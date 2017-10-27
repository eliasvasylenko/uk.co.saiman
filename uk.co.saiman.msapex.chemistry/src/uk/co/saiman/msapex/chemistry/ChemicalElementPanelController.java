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
 * This file is part of uk.co.saiman.msapex.chemistry.
 *
 * uk.co.saiman.msapex.chemistry is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.chemistry is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.chemistry;

import static java.util.Collections.emptySet;
import static javafx.collections.FXCollections.observableArrayList;
import static uk.co.saiman.chemistry.Element.Group.NONE;

import javax.annotation.PostConstruct;
import javax.measure.Unit;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Mass;

import org.eclipse.fx.core.di.Service;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import uk.co.saiman.chemistry.Element;
import uk.co.saiman.chemistry.Element.Group;
import uk.co.saiman.chemistry.Isotope;
import uk.co.saiman.chemistry.PeriodicTable;
import uk.co.saiman.data.ContinuousFunctionExpression;
import uk.co.saiman.data.GaussianFunctionFactory;
import uk.co.saiman.data.PeakShapeFunctionFactory;
import uk.co.saiman.data.PeakShapeImpulseConvolutionFunction;
import uk.co.saiman.measurement.Units;
import uk.co.saiman.msapex.chart.ContinuousFunctionChartController;

/**
 * A JavaFX UI component for display of a {@link PeriodicTable}.
 * 
 * @author Elias N Vasylenko
 */
public class ChemicalElementPanelController {
  private static final String ISOTOPES = "Isotopes";
  private static final double VARIANCE = 0.05;

  @FXML
  private ChemicalElementTile elementTile;
  @FXML
  private Label elementName;
  @FXML
  private Label elementGroup;

  @FXML
  private Node isotopeChart;
  @FXML
  private ContinuousFunctionChartController isotopeChartController;

  @FXML
  private TableView<Isotope> isotopeTable;
  @FXML
  private TableColumn<Isotope, Double> massColumn;
  @FXML
  private TableColumn<Isotope, Double> abundanceColumn;

  private PeakShapeFunctionFactory peakFunction;
  private ContinuousFunctionExpression<Mass, Dimensionless> isotopeFunction;
  private Unit<Dimensionless> abundanceUnit;
  private Unit<Mass> massUnit;

  @FXML
  void initialize() {
    peakFunction = new GaussianFunctionFactory(VARIANCE);
    isotopeChartController.setTitle(ISOTOPES);
    isotopeChartController.getContinuousFunctions().add(isotopeFunction);

    setElement(null);
  }

  @PostConstruct
  void postInitialize(@Service Units units) {
    abundanceUnit = units.percent().get();
    massUnit = units.dalton().get();

    isotopeFunction = new ContinuousFunctionExpression<>(massUnit, abundanceUnit);
  }

  /**
   * @param element
   *          the element to display the information of in this panel
   */
  public void setElement(Element element) {
    elementTile.setElement(element);

    if (element != null) {
      boolean naturallyOccuring = element.isNaturallyOccurring();

      double[] values = new double[element.getIsotopes().size()];
      double[] intensities = new double[element.getIsotopes().size()];

      int isotopeCount = 0;
      for (Isotope isotope : element.getIsotopes()) {
        if (!naturallyOccuring || isotope.getAbundance() > 0) {
          values[isotopeCount] = isotope.getMass();
          intensities[isotopeCount] = naturallyOccuring
              ? isotope.getAbundance() * 10
              : 10d / element.getIsotopes().size();
          isotopeCount++;
        }
      }

      isotopeFunction.setComponent(
          new PeakShapeImpulseConvolutionFunction<>(
              massUnit,
              abundanceUnit,
              isotopeCount,
              values,
              intensities,
              peakFunction));
    }

    isotopeTable
        .setItems(observableArrayList(element != null ? element.getIsotopes() : emptySet()));

    elementName.setText(element != null ? element.getName() : "No Selection");

    setGroup(element != null ? element.getGroup() : NONE);
  }

  /**
   * @return the currently displayed element in the information of in this panel
   */
  public Element getElement() {
    return elementTile.getElement();
  }

  private void setGroup(Group group) {
    elementGroup.setText("(" + group + ")");
  }
}
