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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.tools.emf.ui.common.IModelResource;
import org.eclipse.e4.tools.emf.ui.internal.common.component.ControlFactory;
import org.eclipse.e4.tools.emf.ui.internal.common.component.dialogs.SaveDialogBoundsSettingsDialog;
import org.eclipse.e4.ui.dialogs.filteredtree.PatternFilter;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import uk.co.saiman.eclipse.model.ui.Cell;

public abstract class AbstractCellSelectionDialog extends SaveDialogBoundsSettingsDialog {
  private final IModelResource resource;
  private final ResourceLocator resourceLocator;

  private TableViewer viewer;

  public AbstractCellSelectionDialog(
      Shell parentShell,
      IModelResource resource,
      ResourceLocator resourceLocator) {
    super(parentShell);
    this.resource = resource;
    this.resourceLocator = resourceLocator;
  }

  public ResourceLocator getResourceLocator() {
    return resourceLocator;
  }

  protected abstract String getShellTitle();

  protected abstract String getDialogTitle();

  protected abstract String getDialogMessage();

  @Override
  protected Control createDialogArea(Composite parent) {
    final Composite composite = (Composite) super.createDialogArea(parent);
    getShell().setText(getShellTitle());
    setTitle(getDialogTitle());
    setMessage(getDialogMessage());

    final Image titleImage = new Image(
        composite.getDisplay(),
        getClass().getClassLoader().getResourceAsStream("/icons/full/wizban/newexp_wiz.png")); //$NON-NLS-1$
    setTitleImage(titleImage);
    getShell().addDisposeListener(e -> titleImage.dispose());

    final Composite container = new Composite(composite, SWT.NONE);
    container.setLayoutData(new GridData(GridData.FILL_BOTH));
    container.setLayout(new GridLayout(2, false));

    final Label l = new Label(container, SWT.NONE);
    l.setText("%%% label cell id");

    final Text searchText = new Text(container, SWT.BORDER | SWT.SEARCH | SWT.ICON_SEARCH);
    searchText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    new Label(container, SWT.NONE);
    viewer = new TableViewer(container);
    viewer.setContentProvider(new ArrayContentProvider());
    viewer.setLabelProvider(new LabelProviderImpl());
    viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
    viewer.addDoubleClickListener(event -> okPressed());

    final List<EObject> cells = new ArrayList<>();
    final TreeIterator<EObject> it = EcoreUtil
        .getAllContents((EObject) resource.getRoot().get(0), true);
    while (it.hasNext()) {
      final EObject o = it.next();
      if (o.eClass() == eINSTANCE.getCell()) {
        cells.add(o);
      }
    }
    viewer.setInput(cells);

    final PatternFilter filter = new PatternFilter(true) {
      @Override
      protected boolean isParentMatch(Viewer viewer, Object element) {
        return viewer instanceof AbstractTreeViewer && super.isParentMatch(viewer, element);
      }
    };
    viewer.addFilter(filter);

    ControlFactory.attachFiltering(searchText, viewer, filter);

    return composite;
  }

  @Override
  protected void okPressed() {
    final IStructuredSelection s = (IStructuredSelection) viewer.getSelection();
    if (!s.isEmpty()) {
      final Command cmd = createStoreCommand(
          resource.getEditingDomain(),
          (Cell) s.getFirstElement());
      if (cmd.canExecute()) {
        resource.getEditingDomain().getCommandStack().execute(cmd);
        super.okPressed();
      }
    }
  }

  @Override
  protected boolean isResizable() {
    return true;
  }

  protected abstract Command createStoreCommand(EditingDomain editingDomain, Cell command);

  private static class LabelProviderImpl extends StyledCellLabelProvider implements ILabelProvider {

    @Override
    public void update(final ViewerCell cell) {
      final Cell cmd = (Cell) cell.getElement();

      final StyledString styledString = new StyledString();
      if (cmd.getLabel() != null) {
        styledString.append(cmd.getLabel());
      }
      if (cmd.getElementId() != null) {
        styledString.append(" - " + cmd.getElementId(), StyledString.DECORATIONS_STYLER); //$NON-NLS-1$
      }
      cell.setText(styledString.getString());
      cell.setStyleRanges(styledString.getStyleRanges());
    }

    @Override
    public Image getImage(Object element) {
      return null;
    }

    @Override
    public String getText(Object element) {
      final Cell cell = (Cell) element;
      String s = ""; //$NON-NLS-1$
      if (cell.getLabel() != null) {
        s += cell.getLabel();
      }

      if (cell.getElementId() != null) {
        s += " " + cell.getElementId(); //$NON-NLS-1$
      }

      return s;
    }
  }
}
