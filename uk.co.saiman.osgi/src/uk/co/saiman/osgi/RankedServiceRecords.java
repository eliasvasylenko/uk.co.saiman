/*
 * Copyright (C) 2019 Scientific Analysis Instruments Limited <contact@saiman.co.uk>
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
 * This file is part of uk.co.saiman.osgi.
 *
 * uk.co.saiman.osgi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.osgi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.osgi;

import static java.util.Collections.sort;
import static java.util.Collections.synchronizedList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import uk.co.saiman.observable.ObservablePropertyImpl;
import uk.co.saiman.observable.ObservableValue;

public class RankedServiceRecords<S, U, T> {
  private final List<ServiceRecord<S, U, T>> serviceRecords = synchronizedList(new ArrayList<>());
  private final ObservablePropertyImpl<ServiceRecord<S, U, T>> highestRanked = new ObservablePropertyImpl<>();
  private final U id;

  public RankedServiceRecords(U id) {
    this.id = id;
  }

  public void add(ServiceRecord<S, U, T> record) {
    serviceRecords.add(record);
    sort(serviceRecords, (a, b) -> Integer.compare(b.rank(), a.rank()));
    updateHighest();
  }

  public void remove(ServiceRecord<S, U, T> record) {
    serviceRecords.remove(record);
    updateHighest();
  }

  private void updateHighest() {
    stream().findFirst().ifPresentOrElse(highestRanked::set, highestRanked::unset);
  }

  public boolean isEmpty() {
    return serviceRecords.isEmpty();
  }

  public void dispose() {
    serviceRecords.clear();
    highestRanked.unset();
  }

  public Stream<ServiceRecord<S, U, T>> stream() {
    return serviceRecords.stream();
  }

  public U id() {
    return id;
  }

  public ObservableValue<ServiceRecord<S, U, T>> highestRankedRecord() {
    return highestRanked;
  }
}
