/**
 * Spin - transparent threading solution for non-freezing Swing applications.
 * Copyright (C) 2002 Sven Meier
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package spin.off;

/**
 * A <code>Starter</code> starts <code>Runnable</code>s asynchronously -
 * used by {@link SpinOffEvaluator}s to spin-off non UI computations from the
 * EDT. <br>
 * An implementation of this interface could be a sophisticated thread pool ore
 * simply use:
 * 
 * <pre>
 * new Thread(runnable).start()
 * </pre>
 */
public interface Starter {

	/**
	 * Start a <code>Runnable</code> asynchronously. <br>
	 * This method must return immediately without waiting for the
	 * <code>run()</code> method of the <code>Runnable</code> to complete.
	 * 
	 * @param runnable
	 *            runnable to start
	 */
	public void start(Runnable runnable);
}
