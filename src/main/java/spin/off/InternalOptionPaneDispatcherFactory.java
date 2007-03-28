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

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Window;

import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRootPane;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;

/**
 * A factory of <code>Dispatcher</code>s which uses modal internal
 * <code>JOptionPane</code>s to dispatch events.
 * 
 * @see spin.off.SpinOffEvaluator#SpinOffEvaluator(DispatcherFactory)
 * @see spin.off.SpinOffEvaluator#setDefaultDispatcherFactory(DispatcherFactory)
 * @see AWTReflectDispatcherFactory
 */
public class InternalOptionPaneDispatcherFactory implements DispatcherFactory {

	/**
	 * Create a dispatcher for events.
	 * 
	 * @return dispatcher that does the actual dispatching
	 */
	public Dispatcher createDispatcher() {

		return new InternalOptionPaneDispatcher();
	}

	/**
	 * Dispatcher with a modal internal <code>JOptionPane</code>.
	 */
	private class InternalOptionPaneDispatcher implements Dispatcher, Runnable {

		/**
		 * The lable to show in the <code>JOptionPane</code>.
		 */
		private JLabel label = new JLabel();

		/**
		 * Start the dispatching with a modal internal frame in the
		 * <code>JRootPane</code> of the currently visible
		 * <code>RootPaneContainer</code>.
		 */
		public void start() throws Throwable {
			Window window = KeyboardFocusManager
					.getCurrentKeyboardFocusManager().getActiveWindow();
			while (window != null) {
				if (window.isVisible() && window instanceof RootPaneContainer) {
					break;
				}
				window = window.getOwner();
			}
			if (window != null) {
				JRootPane rootPane = ((RootPaneContainer) window).getRootPane();

				JDesktopPane desktop = new JDesktopPane();
				rootPane.add(desktop);

				JOptionPane.showInternalConfirmDialog(desktop, label);

				rootPane.remove(desktop);
			} else {
				throw new Error("no visible RootPaneContainer");
			}
		}

		/**
		 * Stop dispatching.
		 */
		public void stop() {
			SwingUtilities.invokeLater(this);
		}

		/**
		 * Called on the EDT to stop the dispatching. Hides the internal frame.
		 */
		public void run() {
			Component component = label;
			while (!(component instanceof JOptionPane)) {
				if (component == null) {
					throw new Error("no parental JOptionPane");
				}
				component = component.getParent();
			}
			JOptionPane optionPane = (JOptionPane) component;
			optionPane.setValue(null);
		}
	}
}