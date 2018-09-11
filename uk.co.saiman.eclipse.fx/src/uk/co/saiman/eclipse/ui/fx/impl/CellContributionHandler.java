package uk.co.saiman.eclipse.ui.fx.impl;

import java.util.Collection;

import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.contributions.IContributionFactory;
import org.eclipse.fx.ui.workbench.base.Util;

import uk.co.saiman.eclipse.model.ui.Cell;
import uk.co.saiman.eclipse.model.ui.CellContribution;

public class CellContributionHandler {
  @Inject
  public CellContributionHandler() {}

  public void handleContributionRemove(Cell parent, Collection<CellContribution> contributions) {
    // TODO Auto-generated method stub

  }

  public void handleContributionAddition(Cell parent, Collection<CellContribution> contributions) {
    IContributionFactory contributionFactory = parent.getContext().get(IContributionFactory.class);

    for (CellContribution contribution : contributions) {
      createContext(contribution, parent.getContext());

      Object newContribution = contributionFactory
          .create(contribution.getContributionURI(), contribution.getContext());
      contribution.setObject(newContribution);
    }
  }

  private static IEclipseContext createContext(
      CellContribution model,
      IEclipseContext parentContext) {
    IEclipseContext lclContext = parentContext.createChild(getContextName(model));
    Util.setup(model, lclContext);
    return lclContext;
  }

  private static String getContextName(CellContribution element) {
    StringBuilder builder = new StringBuilder(element.getClass().getSimpleName());
    String elementId = element.getElementId();
    if (elementId != null && elementId.length() != 0) {
      builder.append(" (").append(elementId).append(") ");
    }
    builder.append("Context");
    return builder.toString();
  }

}
