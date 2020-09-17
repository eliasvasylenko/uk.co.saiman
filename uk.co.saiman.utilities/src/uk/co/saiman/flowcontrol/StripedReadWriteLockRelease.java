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
 * This file is part of uk.co.saiman.utilities.
 *
 * uk.co.saiman.utilities is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * uk.co.saiman.utilities is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package uk.co.saiman.flowcontrol;

import java.util.Collection;
import java.util.Set;

/**
 * An interface exposing part of the functionality of a
 * {@link StripedReadWriteLock}. This interface allows clients to release locks,
 * but not to obtain them or wait for them. Isolating the authority to obtain
 * locks in this way can help protect against deadlock.
 * 
 * @author Elias N Vasylenko
 * @param <K>
 *          The type of the keys used to index into the locks.
 */
public interface StripedReadWriteLockRelease<K> {
	public Set<K> readLocksHeldByCurrentThread();

	public Set<K> writeLocksHeldByCurrentThread();

	public boolean releaseLocks(Collection<? extends K> readKeys, Collection<? extends K> writeKeys);

	public boolean releaseLocks(Collection<? extends K> keys);

	public boolean releaseLock(K key);

	public boolean downgradeLock(K key);

	public boolean isLockHeldByCurrentThread(K key);

	public boolean releaseReadLocks(Collection<? extends K> readKeys);

	public boolean releaseReadLock(K key);

	public boolean isReadLockHeldByCurrentThread(K key);

	public boolean releaseWriteLocks(Collection<? extends K> writeKeys);

	public boolean releaseWriteLock(K key);

	public boolean isWriteLockHeldByCurrentThread(K key);
}
