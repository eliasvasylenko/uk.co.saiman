/*
 * Copyright (C) 2016 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
 *
 * This file is part of uk.co.saiman.msapex.data.
 *
 * uk.co.saiman.msapex.data is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.msapex.data is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.msapex.data;

import java.util.HashMap;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import uk.co.saiman.data.Continuum;
import uk.co.strangeskies.reflection.TypeToken;

/**
 * FXML controller for an annotatable data chart over a {@link Continuum}.
 * 
 * @author Elias N Vasylenko
 */
public class DataChartController {
	@FXML
	private Pane root;

	/*
	 * Chart
	 */
	@FXML
	private LineChart<Number, Number> lineChart;

	private ObservableList<Continuum> visibleContinuums = FXCollections.observableArrayList();

	/*
	 * Annotations
	 */
	@FXML
	private AnchorPane annotationPane;

	private ObservableList<ChartAnnotation<?>> annotations = FXCollections.observableArrayList();

	private Map<TypeToken<?>, AnnotationHandler<?>> annotationHandlers = new HashMap<>();
	private Map<ChartAnnotation<?>, Node> annotationNodes = new HashMap<>();

	public Pane getRoot() {
		return root;
	}

	public NumberAxis getXAxis() {
		return (NumberAxis) lineChart.getXAxis();
	}

	public NumberAxis getYAxis() {
		return (NumberAxis) lineChart.getYAxis();
	}

	public void setTitle(String title) {
		lineChart.setTitle(title);
	}

	@FXML
	public void initialize() {
		getXAxis().upperBoundProperty().addListener(num -> update());
		getXAxis().scaleProperty().addListener(num -> update());

		getYAxis().upperBoundProperty().addListener(num -> update());
		getYAxis().scaleProperty().addListener(num -> update());

		// update listener on middle button
		lineChart.getParent().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if (event.getButton() == MouseButton.MIDDLE)
				update();
		});

		annotations.addListener((ListChangeListener<ChartAnnotation<?>>) c -> {
			boolean added = false;

			while (c.next()) {
				annotationPane.getChildren().clear();

				if (c.wasAdded()) {
					added = true;

					c.getAddedSubList().stream().forEach(a -> {
						@SuppressWarnings("unchecked")
						AnnotationHandler<Object> handler = (AnnotationHandler<Object>) annotationHandlers.get(a.getDataType());
						annotationNodes.put(a, handler.handle(a.getData()));
					});
				}

				c.getRemoved().stream().map(annotationNodes::get).forEach(annotationPane.getChildren()::remove);
			}

			if (added) {
				update();
			}
		});

		update();
	}

	private void update() {
		for (ChartAnnotation<?> annotation : annotations) {
			Node node = annotationNodes.get(annotation);

			double x = getXAxis().localToParent(getXAxis().getDisplayPosition(annotation.getX()), 0).getX()
					+ lineChart.getPadding().getLeft();
			double y = getYAxis().localToParent(0, getYAxis().getDisplayPosition(annotation.getY())).getY()
					+ lineChart.getPadding().getTop();

			node.autosize();
			node.setLayoutX(x);
			node.setLayoutY(y - node.prefHeight(Integer.MAX_VALUE));
		}
	}
}
