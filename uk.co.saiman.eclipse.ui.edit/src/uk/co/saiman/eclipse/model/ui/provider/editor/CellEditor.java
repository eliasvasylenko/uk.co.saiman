package uk.co.saiman.eclipse.model.ui.provider.editor;

import static java.util.stream.Collectors.toList;
import static uk.co.saiman.eclipse.model.ui.Package.eINSTANCE;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.list.IListProperty;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.e4.tools.emf.ui.common.Util;
import org.eclipse.e4.tools.emf.ui.internal.common.E4StringPickList;
import org.eclipse.e4.tools.emf.ui.internal.common.VirtualEntry;
import org.eclipse.e4.tools.emf.ui.internal.common.component.ControlFactory.TextPasteHandler;
import org.eclipse.e4.ui.model.application.MContribution;
import org.eclipse.e4.ui.model.application.ui.impl.UiPackageImpl;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuFactory;
import org.eclipse.e4.ui.model.application.ui.menu.MPopupMenu;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.databinding.IEMFListProperty;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.RemoveCommand;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.databinding.swt.IWidgetValueProperty;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import uk.co.saiman.data.format.MediaType;
import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.model.ui.Package;
import uk.co.saiman.eclipse.model.ui.provider.editor.ListComponentManager.Type;

public class CellEditor extends AbstractEditor {
  @SuppressWarnings("rawtypes")
  private final IListProperty CHILDREN = EMFProperties
      .list(UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN);
  private ListComponentManager childrenComponent;

  @SuppressWarnings("rawtypes")
  private final IListProperty CONTRIBUTIONS = EMFProperties
      .list(Package.eINSTANCE.getCell_Contributions());

  private static final String VIRTUAL_CELL_CONTRIBUTIONS_MENU = "uk.co.saiman.eclipse.model.ui.cell.contributions.virtual";

  private static final IValueProperty POPUP_MENU = EMFProperties
      .value(Package.eINSTANCE.getCell_PopupMenu());
  private Button createRemovePopupMenu;

  @PostConstruct
  void init() {
    childrenComponent = new ListComponentManager(
        this,
        Messages,
        UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
        this::createChild,
        new Type(
            getString("_UI_Cell_type"),
            ImageDescriptor.createFromFile(VListEditor.class, "/icons/full/obj16/Cell.gif"),
            eINSTANCE.getCell()),
        new Type(
            getString("_UI_HandledCell_type"),
            ImageDescriptor.createFromFile(VListEditor.class, "/icons/full/obj16/HandledCell.gif"),
            eINSTANCE.getHandledCell()));
  }

  private EObject createChild(Type type) {
    final EObject handler = EcoreUtil.create(type.eClass);
    setElementId(handler);
    return handler;
  }

  @Override
  public String getLabel(Object element) {
    return getString("_UI_Cell_type");
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
    return getString("_UI_Cell_editor_description");
  }

  @Override
  protected void refreshEditor() {
    if (createRemovePopupMenu != null) {
      createRemovePopupMenu.setSelection(((Cell) getMaster().getValue()).getPopupMenu() != null);
    }
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

    createPopupMenuControl(parent);

    childrenComponent.createForm(parent);
    childrenComponent.getViewer().setInput(CHILDREN.observeDetail(getMaster()));

    createMediaTypesControl(parent);

    createPersistedStateControl(parent);
  }

  private void createPopupMenuControl(Composite parent) {
    final Label l = new Label(parent, SWT.NONE);
    l.setText(getString("_UI_Cell_popupMenu_feature"));
    l.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

    createRemovePopupMenu = new Button(parent, SWT.CHECK);
    createRemovePopupMenu.addSelectionListener(new SelectionAdapter() {
      @Override
      public void widgetSelected(SelectionEvent e) {
        final Cell window = (Cell) getMaster().getValue();
        if (window.getPopupMenu() != null) {
          removePopupMenu();
        } else {
          addPopupMenu();
        }
      }
    });
    createRemovePopupMenu
        .setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, false, false, 2, 1));
  }

  private void addPopupMenu() {
    final MPopupMenu menu = MMenuFactory.INSTANCE.createPopupMenu();
    setElementId(menu);

    final Command cmd = SetCommand
        .create(getEditingDomain(), getMaster().getValue(), eINSTANCE.getCell_PopupMenu(), menu);
    if (cmd.canExecute()) {
      getEditingDomain().getCommandStack().execute(cmd);
    }
  }

  private void removePopupMenu() {
    final Command cmd = SetCommand
        .create(getEditingDomain(), getMaster().getValue(), eINSTANCE.getCell_PopupMenu(), null);
    if (cmd.canExecute()) {
      getEditingDomain().getCommandStack().execute(cmd);
    }
  }

  @SuppressWarnings("unchecked")
  private void createMediaTypesControl(Composite parent) {
    final E4StringPickList pickList = new E4StringPickList(
        parent,
        SWT.NONE,
        null,
        Messages,
        this,
        eINSTANCE.getCell_MediaTypes()) {
      @Override
      protected void addPressed() {
        handleAddMediaType(getTextWidget());
      }

      @Override
      protected void handleReplaceText() {
        handleReplaceMediaType(getTextWidget(), (StructuredSelection) viewer.getSelection());
      }
    };

    pickList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
    pickList.setText(getString("_UI_Cell_mediaTypes_feature"));
    pickList.setLabelProvider(new LabelProvider() {
      @Override
      public String getText(Object element) {
        return element.toString();
      }
    });

    final Text t = pickList.getTextWidget();
    t.addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if (e.keyCode == SWT.CR || e.keyCode == SWT.LF) {
          handleAddMediaType(t);
        }
      }
    });

    TextPasteHandler.createFor(t);

    TableViewer viewer = pickList.getList();
    viewer.setLabelProvider(new LabelProvider());
    GridData gd = (GridData) viewer.getTable().getLayoutData();
    gd.heightHint = 150;

    IEMFListProperty prop = EMFProperties.list(eINSTANCE.getCell_MediaTypes());
    viewer.setInput(prop.observeDetail(getMaster()));

    viewer.addSelectionChangedListener(event -> {
      MediaType itemSelected = (MediaType) ((StructuredSelection) event.getSelection())
          .getFirstElement();
      t.setText(itemSelected != null ? itemSelected.toString() : ""); //$NON-NLS-1$
    });
  }

  private List<MediaType> getMediaTypes(Text tagText) {
    return Stream
        .of(tagText.getText().split(","))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .map(MediaType::valueOf)
        .collect(toList());
  }

  private void handleReplaceMediaType(Text tagText, StructuredSelection selection) {
    Cell cell = (Cell) getMaster().getValue();

    @SuppressWarnings("unchecked")
    List<MediaType> itemsSelected = selection.toList();
    MediaType itemSelected = (MediaType) selection.getFirstElement();
    int selectionIndex = cell.getMediaTypes().indexOf(itemSelected);

    List<MediaType> mediaTypes = getMediaTypes(tagText);

    Command cmdRemove = RemoveCommand
        .create(getEditingDomain(), cell, eINSTANCE.getCell_MediaTypes(), itemsSelected);
    Command cmdInsert = AddCommand
        .create(
            getEditingDomain(),
            cell,
            eINSTANCE.getCell_MediaTypes(),
            mediaTypes,
            selectionIndex);
    if (cmdRemove.canExecute() && cmdInsert.canExecute()) {
      getEditingDomain().getCommandStack().execute(cmdRemove);
      getEditingDomain().getCommandStack().execute(cmdInsert);
    }
    tagText.setText(""); //$NON-NLS-1$
  }

  private void handleAddMediaType(Text tagText) {
    List<MediaType> mediaTypes = getMediaTypes(tagText);

    if (!mediaTypes.isEmpty()) {
      final Cell cell = (Cell) getMaster().getValue();
      final Command cmd = AddCommand
          .create(getEditingDomain(), cell, eINSTANCE.getCell_MediaTypes(), mediaTypes);
      if (cmd.canExecute()) {
        getEditingDomain().getCommandStack().execute(cmd);
      }
      tagText.setText(""); //$NON-NLS-1$
    }
  }

  @Override
  protected void createSupplementaryControls(Composite parent) {
    createContextVariablesControl(parent);
    createTagsControl(parent);
  }

  @Override
  public List<Action> getActions(Object element) {
    List<Action> actions = new ArrayList<>(super.getActions(element));
    actions.addAll(childrenComponent.getActions());
    return actions;
  }

  @SuppressWarnings("unchecked")
  @Override
  public IObservableList<?> getChildList(final Object element) {
    final WritableList<Object> list = new WritableList<>();

    if (getEditor().isModelFragment() && Util.isImport((EObject) element)) {
      return list;
    }

    list
        .add(
            new VirtualEntry<Object>(
                VIRTUAL_CELL_CONTRIBUTIONS_MENU,
                CONTRIBUTIONS,
                element,
                getString("_UI_contributions")) {
              @Override
              protected boolean accepted(Object o) {
                return true;
              }
            });

    list.addAll(CHILDREN.getList(element));
    CHILDREN.observe(element).addChangeListener(event -> {
      if (!event.getObservable().isDisposed()) {
        while (list.size() > 1) {
          list.remove(1);
        }
        list.addAll(CHILDREN.getList(element));
      }
    });

    POPUP_MENU.observe(element).addValueChangeListener(event -> {
      if (event.diff.getOldValue() != null) {
        list.remove(event.diff.getOldValue());
        if (getMaster().getValue() == element && !createRemovePopupMenu.isDisposed()) {
          createRemovePopupMenu.setSelection(false);
        }

      }

      if (event.diff.getNewValue() != null) {
        list.add(0, event.diff.getNewValue());
        if (getMaster().getValue() == element && !createRemovePopupMenu.isDisposed()) {
          createRemovePopupMenu.setSelection(true);
        }
      }
    });

    return list;
  }
}
