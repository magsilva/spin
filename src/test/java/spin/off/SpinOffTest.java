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

import spin.Spin;

public class SpinOffTest extends TestCase {

	private static final int DELAY = 3000;

	private class Timer {
		private long start;

		public Timer() {
			start = System.currentTimeMillis();
		}

		public long elapsed() {
			return System.currentTimeMillis() - start;
		}
	}

	public static interface OneIntProperty {
		int getInt();
	}

	public void testEDTNotBlockedDuringInvocation() throws Exception {
		class Flag {
			public boolean flag1, flag2;

			Throwable exception;
		}
		final Flag flag = new Flag();

		OneIntProperty target = new OneIntProperty() {
			public int getInt() {
				try {
					Thread.sleep(DELAY);
				} catch (InterruptedException e) {
				}
				return 0;
			}
		};
		final OneIntProperty proxy = (OneIntProperty) Spin.off(target);
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					proxy.getInt();
				} catch (Throwable t) {
					flag.exception = t;
				} finally {
					flag.flag1 = true;
				}
			}
		});
		Timer timer = new Timer();
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				flag.flag2 = true;
			}
		});
		assertTrue("Second runnable took too long: " + timer.elapsed(), timer
				.elapsed() < DELAY);
		assertTrue("Second runnable didn't run", flag.flag2);
		while (!flag.flag1) {
			if (timer.elapsed() > DELAY * 2) {
				fail("Original invocation timed out");
			}
			Thread.sleep(10);
		}
		assertNull("Unexpected exception in original invocation "
				+ flag.exception, flag.exception);
	}
}
