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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.swing.SwingUtilities;

/**
 * A factory of <code>Dispatcher</code>s which uses reflection to AWT
 * internals to dispatch events - used as default by <em>Spin</em> for
 * spin-off. <br>
 * Once Swing offers an official way to start an event pump this class should be
 * replaced by a less intrusive solution.
 */
public class AWTReflectDispatcherFactory implements DispatcherFactory {

	/**
	 * The AWT conditional class.
	 */
	private static Class conditionalClass;

	/**
	 * The pumpMethod of the EDT.
	 */
	private static Method pumpMethod;

	/**
	 * Create a dispatcher for events.
	 * 
	 * @return dispatcher that does the actual dispatching
	 */
	public Dispatcher createDispatcher() {

		return new AWTReflectDispatcher();
	}

	/**
	 * Dispatcher with reflection of AWT.
	 */
	private class AWTReflectDispatcher implements Dispatcher, InvocationHandler {

		/**
		 * Flag indicating that dispatching should stop.
		 */
		private boolean stopDispatching = false;

		/**
		 * Start the dispatching.
		 * 
		 * @throws Exception
		 */
		public void start() throws Throwable {
			try {
				Object conditional = Proxy.newProxyInstance(conditionalClass
						.getClassLoader(), new Class[] { conditionalClass },
						this);
				pumpMethod.invoke(Thread.currentThread(),
						new Object[] { conditional });

				synchronized (this) {
					// if the EDT refuses to pump events (e.g. because of a
					// sun.awt.AWTAutoShutdown)
					// we can do nothing else but wait for stop() to be called
					while (!stopDispatching) {
						wait();
					}
				}
			} catch (InvocationTargetException ex) {
				throw ex.getTargetException();
			}
		}

		/**
		 * Stop dispatching.
		 */
		public void stop() {
			synchronized (this) {
				stopDispatching = true;

				// notify possibly waiting start()
				notifyAll();
			}

			// force the event queue to re-evaluate our conditional
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
				}
			});
		}

		/**
		 * Invoke <code>evaluate()</code> on the wrapped
		 * <code>Conditional</code> instance - called by the
		 * EventDispatchThread to test if pumping of events should be continued.
		 * 
		 * @return <code>true</code> if events still should be continued
		 *         pumped, <code>false</code> otherwise
		 */
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {

			if (stopDispatching) {
				return Boolean.FALSE;
			} else {
				return Boolean.TRUE;
			}
		}
	}

	/**
	 * Initialize AWT internals. <br>
	 * Get references to class <code>java.awt.Conditional</code> and method
	 * <code>java.awt.EventDispatchThread.pumpEvents()</code>.
	 */
	static {
		try {
			conditionalClass = Class.forName("java.awt.Conditional");

			pumpMethod = Class.forName("java.awt.EventDispatchThread")
					.getDeclaredMethod("pumpEvents",
							new Class[] { conditionalClass });
			pumpMethod.setAccessible(true);
		} catch (Exception ex) {
			throw new Error(ex.getMessage());
		}
	}
}
