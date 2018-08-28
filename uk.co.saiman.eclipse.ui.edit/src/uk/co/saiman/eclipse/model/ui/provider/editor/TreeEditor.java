package uk.co.saiman.eclipse.model.ui.provider.editor;

import static uk.co.saiman.eclipse.model.ui.Package.eINSTANCE;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.property.list.IListProperty;
import org.eclipse.e4.ui.model.application.MContribution;
import org.eclipse.e4.ui.model.application.ui.impl.UiPackageImpl;
import org.eclipse.emf.databinding.EMFDataBindingContext;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.databinding.swt.IWidgetValueProperty;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;

import uk.co.saiman.eclipse.model.ui.provider.editor.ListComponentManager.Type;

public class TreeEditor extends AbstractEditor {
  private ListComponentManager childrenComponent;
  @SuppressWarnings("rawtypes")
  private final IListProperty CHILDREN = EMFProperties
      .list(UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN);

  @PostConstruct
  void init() {
    childrenComponent = new ListComponentManager(
        this,
        Messages,
        UiPackageImpl.Literals.ELEMENT_CONTAINER__CHILDREN,
        this::handleCreate,
        new Type(
            getString("_UI_Cell_type"),
            ImageDescriptor.createFromFile(VListEditor.class, "/icons/full/obj16/Cell.gif"),
            eINSTANCE.getCell()),
        new Type(
            getString("_UI_HandledCell_type"),
            ImageDescriptor.createFromFile(VListEditor.class, "/icons/full/obj16/HandledCell.gif"),
            eINSTANCE.getHandledCell()));
  }

  @Override
  protected void refreshEditor() {
    // listEditor.getViewer().setInput(o.getList());
  }

  private EObject handleCreate(Type type) {
    final EObject handler = EcoreUtil.create(type.eClass);
    setElementId(handler);
    return handler;
  }

  @Override
  public String getLabel(Object element) {
    return getString("_UI_Tree_type");
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
    return getString("_UI_Tree_editor_description");
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

    childrenComponent.createForm(parent);
    childrenComponent.getViewer().setInput(CHILDREN.observeDetail(getMaster()));

    createPersistedStateControl(parent);
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
  public IObservableList<?> getChildList(Object element) {
    return CHILDREN.observe(element);
  }
}
