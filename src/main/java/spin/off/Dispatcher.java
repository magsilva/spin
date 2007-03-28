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
 * An dispatcher of AWT events. <br>
 * Used by {@link SpinOffEvaluator}s to keep the UI reactive while waiting for
 * a spin-off to complete.
 */
public interface Dispatcher {

	/**
	 * Start dispatching events. <br>
	 * This method is always called on the EDT. It must not return until
	 * {@link #stop()} is called.
	 * 
	 * @throws Throwable
	 *             in case of any exceptions while dispatching
	 */
	public void start() throws Throwable;

	/**
	 * Stop dispatching events. <br>
	 * This method is <bold>not</bold> called on the EDT.
	 */
	public void stop();
}