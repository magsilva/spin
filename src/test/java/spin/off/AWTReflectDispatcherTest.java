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

import javax.swing.SwingUtilities;

import junit.framework.TestCase;

public class AWTReflectDispatcherTest extends TestCase {

	public void test() throws Exception {
		AWTReflectDispatcherFactory factory = new AWTReflectDispatcherFactory();

		final Dispatcher dispatcher = factory.createDispatcher();

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					dispatcher.start();
				} catch (Throwable throwable) {
					fail(throwable.getMessage());
				}
			}
		});

		Thread.sleep(2000);

		dispatcher.stop();
	}
}
