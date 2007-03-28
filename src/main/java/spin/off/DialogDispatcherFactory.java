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

import java.awt.Dialog;

import javax.swing.SwingUtilities;

/**
 * Abstract base class for factories that dispatch events with
 * <code>java.awt.Dialog</code>s. Shows how events can be dispatched with
 * standard AWT.
 * 
 * @see spin.off.SpinOffEvaluator#SpinOffEvaluator(DispatcherFactory)
 * @see spin.off.SpinOffEvaluator#setDefaultDispatcherFactory(DispatcherFactory)
 * @see AWTReflectDispatcherFactory
 */
public abstract class DialogDispatcherFactory implements DispatcherFactory {

	/**
	 * Create a dispatcher.
	 * 
	 * @return dispatcher that does the actual dispatching
	 */
	public Dispatcher createDispatcher() {

		return new DialogDispatcher();
	}

	/**
	 * Factory method to implement by subclasses to aquire a dialog.
	 * 
	 * @return dialog
	 */
	protected abstract Dialog aquireDialog();

	/**
	 * Factory method to implement by subclasses to release a dialog.
	 * 
	 * @param dialog
	 *            the dialog to release
	 */
	protected abstract void releaseDialog(Dialog dialog);

	/**
	 * Dispatcher with <code>Dialog</code>.
	 */
	protected class DialogDispatcher implements Dispatcher, Runnable {

		/**
		 * The dialog used to do the actual dispatching.
		 */
		private Dialog dialog;

		/**
		 * Start the dispatching.
		 * 
		 * @throws Exception
		 */
		public void start() throws Throwable {

			dialog = aquireDialog();
			dialog.setModal(true);

			dialog.setVisible(true);
		}

		/**
		 * Stop dispatching.
		 */
		public void stop() {

			SwingUtilities.invokeLater(this);
		}

		/**
		 * Called on the EDT to stop the dispatching.
		 */
		public void run() {
			dialog.setVisible(false);
			dialog.dispose();

			releaseDialog(dialog);

			dialog = null;
		}
	}
}
