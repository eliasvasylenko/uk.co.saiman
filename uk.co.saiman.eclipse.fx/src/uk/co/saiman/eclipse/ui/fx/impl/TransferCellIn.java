/*
 * Copyright (C) 2020 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.eclipse.fx.
 *
 * uk.co.saiman.eclipse.fx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.eclipse.fx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.eclipse.ui.fx.impl;

import static java.util.stream.Collectors.toSet;
import static uk.co.saiman.eclipse.ui.fx.TransferModes.toJavaFXTransferMode;
import static uk.co.saiman.eclipse.ui.fx.impl.TransferDestination.OVER;
import static uk.co.saiman.eclipse.ui.fx.impl.UIAddon.findMethod;
import static uk.co.saiman.eclipse.ui.fx.impl.UIAddon.resolveRequestor;

import java.util.Optional;
import java.util.Set;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.internal.contexts.ContextObjectSupplier;
import org.eclipse.e4.core.internal.di.MethodRequestor;
import org.eclipse.e4.ui.model.application.MApplicationElement;

import javafx.scene.input.Dragboard;
import uk.co.saiman.eclipse.ui.CompleteTransfer;
import uk.co.saiman.eclipse.ui.TransferFormat;
import uk.co.saiman.eclipse.ui.TransferMode;
import uk.co.saiman.eclipse.ui.TransferSink;
import uk.co.saiman.eclipse.ui.fx.ClipboardService;

public class TransferCellIn {
  private final Dragboard clipboard;
  private ClipboardService clipboardService;

  private final Set<TransferMode> transferModes;
  private final TransferSink transferSink;

  public TransferCellIn(Object object, IEclipseContext context, Dragboard clipboard) {
    this(object, context, clipboard, OVER, null);
  }

  public TransferCellIn(
      Object object,
      IEclipseContext context,
      Dragboard clipboard,
      TransferDestination position,
      MApplicationElement sibling) {
    this.clipboard = clipboard;
    this.clipboardService = context.get(ClipboardService.class);

    MethodRequestor completeRequestor = resolveCompleteRequestor(
        object,
        context,
        position,
        sibling);
    if (completeRequestor == null) {
      this.transferModes = Set.of();
      this.transferSink = new TransferSink();
      return;
    }
    Object requestorResult = completeRequestor.execute();
    if (requestorResult == null || !(requestorResult instanceof TransferSink)) {
      this.transferModes = Set.of();
      this.transferSink = new TransferSink();
      return;
    }
    this.transferSink = (TransferSink) requestorResult;

    this.transferModes = transferSink
        .getTransferFormats()
        .map(TransferFormat::transferModes)
        .flatMap(Set::stream)
        .filter(
            transferMode -> toJavaFXTransferMode(transferMode)
                .filter(mode -> clipboard.getTransferModes().contains(mode))
                .isPresent())
        .collect(toSet());
  }

  private static MethodRequestor resolveCompleteRequestor(
      Object object,
      IEclipseContext context,
      TransferDestination position,
      MApplicationElement sibling) {
    IEclipseContext childContext = context.createChild();
    childContext.set(TransferDestination.class, position);
    switch (position) {
    case AFTER_CHILD:
    case BEFORE_CHILD:
      childContext.set(CompleteTransfer.SIBLING, sibling);
      break;
    default:
      break;
    }
    return resolveRequestor(
        object,
        childContext,
        findMethod(object.getClass(), CompleteTransfer.class, false),
        new ContextObjectSupplier(childContext, UIAddon.INJECTOR),
        true,
        false);
  }

  public Set<TransferMode> supportedTransferModes() {
    return transferModes;
  }

  public void handle(TransferMode transferMode) {
    transferSink
        .getTransferFormats()
        .filter(format -> format.transferModes().contains(transferMode))
        .flatMap(format -> valueFromClipboard(format).stream())
        .findFirst()
        .ifPresent(FormatValue::sink);
  }

  public <T> Optional<FormatValue<T>> valueFromClipboard(TransferFormat<T> format) {
    return clipboardService
        .getValue(clipboard, format.dataFormat())
        .map(value -> new FormatValue<>(format, value));
  }

  class FormatValue<T> {
    private TransferFormat<T> format;
    private T value;

    public FormatValue(TransferFormat<T> format, T value) {
      this.format = format;
      this.value = value;
    }

    void sink() {
      transferSink.putValue(format, value);
    }
  }
}
