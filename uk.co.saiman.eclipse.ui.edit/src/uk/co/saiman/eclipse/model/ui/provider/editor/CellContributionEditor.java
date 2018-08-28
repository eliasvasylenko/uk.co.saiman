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
 * This file is part of uk.co.saiman.eclipse.ui.edit.
 *
 * uk.co.saiman.eclipse.ui.edit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.ui.edit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.model.ui.provider.editor;

import static uk.co.saiman.eclipse.model.ui.Package.eINSTANCE;
import static uk.co.saiman.eclipse.model.ui.provider.UISaimanEditPlugin.INSTANCE;

import javax.inject.Inject;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.e4.tools.emf.ui.common.IModelResource;
import org.eclipse.e4.tools.emf.ui.internal.ResourceProvider;
import org.eclipse.e4.tools.emf.ui.internal.common.component.ControlFactory.TextPasteHandler;
import org.eclipse.e4.ui.model.application.MContribution;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.edit.EMFEditProperties;
import org.eclipse.jface.databinding.swt.IWidgetValueProperty;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.model.ui.CellContribution;

public class CellContributionEditor extends AbstractEditor {
  @Inject
  private IModelResource resource;

  @Override
  public String getLabel(Object element) {
    return getString("_UI_CellContribution_type");
  }

  @Override
  public String getDetailLabel(Object element) {
    final MContribution contrib = (MContribution) element;
    if (contrib.getContributionURI() != null && contrib.getContributionURI().trim().length() > 0) {
      return contrib
          .getContributionURI()
          .substring(contrib.getContributionURI().lastIndexOf('/') + 1);
    }
    return null;
  }

  @Override
  public String getDescription(Object element) {
    return getString("_UI_CellContribution_editor_description");
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void createDefaultControls(
      Composite parent,
      EMFDataBindingContext context,
      IObservableValue<?> master,
      IWidgetValueProperty textProp) {
    createElementIdControl(parent, context, master, textProp);
    createContributionControl(parent, context);

    // ------------------------------------------------------------
    {
      final Label l = new Label(parent, SWT.NONE);
      l.setText(getString("_UI_Cell_type"));
      l.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

      final Text t = new Text(parent, SWT.BORDER);
      TextPasteHandler.createFor(t);
      t.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      t.setEditable(false);
      context
          .bindValue(
              textProp.observeDelayed(200, t),
              EMFEditProperties
                  .value(getEditingDomain(), eINSTANCE.getCellContribution_Parent())
                  .observeDetail(getMaster()),
              new UpdateValueStrategy(),
              new UpdateValueStrategy()
                  .setConverter(
                      new CellToStringConverter(Cell.class, () -> getString("_UI_none"))));

      final Button b = new Button(parent, SWT.PUSH | SWT.FLAT);
      b.setText(Messages.ModelTooling_Common_FindEllipsis);
      b.setImage(createImage(ResourceProvider.IMG_Obj16_zoom));
      b.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
      b.addSelectionListener(new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
          final CellContributionCellSelectionDialog dialog = new CellContributionCellSelectionDialog(
              b.getShell(),
              (CellContribution) getMaster().getValue(),
              resource,
              INSTANCE.getPluginResourceLocator());
          dialog.open();
        }
      });
    }

    createPersistedStateControl(parent);
  }

  @Override
  protected void createSupplementaryControls(Composite parent) {
    createContextVariablesControl(parent);
    createTagsControl(parent);
  }
}
