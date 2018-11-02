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
 * This file is part of uk.co.saiman.observable.
 *
 * uk.co.saiman.observable is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.observable is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.observable;

import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import mockit.Injectable;
import mockit.Verifications;

@SuppressWarnings("javadoc")
public class ObservableTest {
  Observable<String> upstreamObservable = a -> null;

  @Injectable
  Observer<String> downstreamObserver;

  @Test
  public void thenTest() {
    upstreamObservable.then(m -> {});

    new Verifications() {
      {
        Observer<String> observer;
        upstreamObservable.observe(observer = withCapture());
        assertThat(observer, instanceOf(MultiplePassthroughObserver.class));
      }
    };
  }

  @Test
  public void thenAfterTest() {
    upstreamObservable.thenAfter(m -> {});

    new Verifications() {
      {
        Observer<String> observer;
        upstreamObservable.observe(observer = withCapture());
        assertThat(observer, instanceOf(MultiplePassthroughObserver.class));
      }
    };
  }

  @Test
  public void retryingTest() {
    upstreamObservable.retrying();

    new Verifications() {
      {
        Observer<String> observer;
        upstreamObservable.observe(observer = withCapture());
        assertThat(observer, instanceOf(RetryingObserver.class));
      }
    };
  }

  @Test
  public void softReferenceOwnedTest() {
    upstreamObservable.softReference(new Object());

    new Verifications() {
      {
        Observer<String> observer;
        upstreamObservable.observe(observer = withCapture());
        assertThat(observer, instanceOf(ReferenceOwnedObserver.class));
      }
    };
  }

  @Test
  public void softReferenceTest() {
    upstreamObservable.softReference();

    new Verifications() {
      {
        Observer<String> observer;
        upstreamObservable.observe(observer = withCapture());
        assertThat(observer, instanceOf(ReferenceOwnedObserver.class));
      }
    };
  }

  @Test
  public void weakReferenceOwnedTest() {
    upstreamObservable.weakReference(new Object());

    new Verifications() {
      {
        Observer<String> observer;
        upstreamObservable.observe(observer = withCapture());
        assertThat(observer, instanceOf(ReferenceOwnedObserver.class));
      }
    };
  }

  @Test
  public void weakReferenceTest() {
    upstreamObservable.weakReference();

    new Verifications() {
      {
        Observer<String> observer;
        upstreamObservable.observe(observer = withCapture());
        assertThat(observer, instanceOf(ReferenceOwnedObserver.class));
      }
    };
  }

  @Test
  public void executeOnTest() {
    upstreamObservable.executeOn(r -> {});

    new Verifications() {
      {
        Observer<String> observer;
        upstreamObservable.observe(observer = withCapture());
        assertThat(observer, instanceOf(ExecutorObserver.class));
      }
    };
  }

  @Test
  public void mapTest() {
    upstreamObservable.map(s -> s);

    new Verifications() {
      {
        Observer<String> observer;
        upstreamObservable.observe(observer = withCapture());
        assertThat(observer, instanceOf(MappingObserver.class));
      }
    };
  }

  @Test
  public void filterTest() {
    upstreamObservable.filter(s -> true);

    new Verifications() {
      {
        Observer<String> observer;
        upstreamObservable.observe(observer = withCapture());
        assertThat(observer, instanceOf(FilteringObserver.class));
      }
    };
  }

  @Test
  public void takeWhileTest() {
    upstreamObservable.takeWhile(s -> true);

    new Verifications() {
      {
        Observer<String> observer;
        upstreamObservable.observe(observer = withCapture());
        assertThat(observer, instanceOf(TakeWhileObserver.class));
      }
    };
  }

  @Test
  public void dropWhileTest() {
    upstreamObservable.dropWhile(s -> true);

    new Verifications() {
      {
        Observer<String> observer;
        upstreamObservable.observe(observer = withCapture());
        assertThat(observer, instanceOf(DropWhileObserver.class));
      }
    };
  }
}
