package uk.co.saiman.msapex.chemistry;

import java.util.Arrays;
import java.util.List;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.menu.ItemType;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

import uk.co.saiman.msapex.chemistry.ChemicalElementTile.Size;
import uk.co.strangeskies.utilities.Enumeration;

/**
 * Toggle available size options for the periodic table
 * 
 * @author Elias N Vasylenko
 */
public class PeriodicTableResize {
	@Execute
	void execute(EPartService partService) {
		PeriodicTablePart periodicTablePart = (PeriodicTablePart) partService
				.findPart("uk.co.saiman.msapex.periodictable.part").getObject();

		Size currentSize = periodicTablePart.getPeriodicTableController().getTileSize();

		int ordinal = currentSize.ordinal() + 1;
		currentSize = Size.values().length == ordinal ? Size.values()[0] : Size.values()[ordinal];

		periodicTablePart.getPeriodicTableController().setTileSize(currentSize);
	}

	@AboutToShow
	void aboutToShow(List<MMenuElement> items, EPartService partService) {
		PeriodicTablePart periodicTablePart = (PeriodicTablePart) partService
				.findPart("uk.co.saiman.msapex.periodictable.part").getObject();

		for (Size size : Arrays.asList(Size.values())) {
			MDirectMenuItem moduleItem = MMenuFactory.INSTANCE.createDirectMenuItem();
			moduleItem.setLabel(Enumeration.readableName(size));
			moduleItem.setType(ItemType.RADIO);
			moduleItem.setSelected(periodicTablePart.getPeriodicTableController().getTileSize() == size);
			moduleItem.setObject(new Object() {
				@Execute
				public void execute() {
					if (moduleItem.isSelected()) {
						periodicTablePart.getPeriodicTableController().setTileSize(size);
					}
				}
			});

			items.add(moduleItem);
		}
	}
}
