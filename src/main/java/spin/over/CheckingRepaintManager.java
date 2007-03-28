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
package spin.over;

import java.awt.Component;

import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;

/**
 * A repaintManager that checks bad access - i.e. access from non EDT - to Swing
 * components. Install with:
 * 
 * <pre>
 * RepaintManager.setCurrentManager(new CheckingRepaintManager());
 * </pre>
 * 
 * Based on an idea by Scott Delap (http://www.clientjava.com).
 * 
 * @see javax.swing.RepaintManager
 */
public class CheckingRepaintManager extends RepaintManager {

	/**
	 * Overriden to check EDT rule.
	 */
	public synchronized void addInvalidComponent(JComponent component) {
		checkEDTRule(component);

		super.addInvalidComponent(component);
	}

	/**
	 * Overriden to check EDT rule.
	 */
	public synchronized void addDirtyRegion(JComponent component, int x, int y,
			int w, int h) {
		checkEDTRule(component);

		super.addDirtyRegion(component, x, y, w, h);
	}

	/**
	 * Check EDT rule on access to the given component.
	 * 
	 * @param component
	 *            component to be repainted
	 */
	protected void checkEDTRule(Component component) {
		if (violatesEDTRule(component)) {
			EDTRuleViolation violation = new EDTRuleViolation(component);

			StackTraceElement[] stackTrace = violation.getStackTrace();
			try {
				for (int e = stackTrace.length - 1; e >= 0; e--) {
					if (isLiableToEDTRule(stackTrace[e])) {
						StackTraceElement[] subStackTrace = new StackTraceElement[stackTrace.length
								- e];
						System.arraycopy(stackTrace, e, subStackTrace, 0,
								subStackTrace.length);

						violation.setStackTrace(subStackTrace);
					}
				}
			} catch (Exception ex) {
				// keep stackTrace
			}

			indicate(violation);
		}
	}

	/**
	 * Does acces to the given component violate the EDT rule.
	 * 
	 * @param component
	 *            accessed component
	 * @return <code>true</code> if EDT rule is violated
	 */
	protected boolean violatesEDTRule(Component component) {
		return !SwingUtilities.isEventDispatchThread() && component.isShowing();
	}

	/**
	 * Is the given stackTraceElement liable to the EDT rule.
	 * 
	 * @param element
	 *            element
	 * @return <code>true</code> if the className of the given element denotes
	 *         a subclass of <code>java.awt.Component</code>
	 * @throws Exception
	 *             on any problem
	 */
	protected boolean isLiableToEDTRule(StackTraceElement element)
			throws Exception {
		return Component.class.isAssignableFrom(Class.forName(element
				.getClassName()));
	}

	/**
	 * Indicate a violation of the EDT rule. This default implementation throws
	 * the given exception, subclasses may want to log the exception instead.
	 * 
	 * @param violation
	 *            violation of EDT rule
	 */
	protected void indicate(EDTRuleViolation violation) throws EDTRuleViolation {
		throw violation;
	}
}
