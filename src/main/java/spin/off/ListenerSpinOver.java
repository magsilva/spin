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

import java.lang.reflect.Method;
import java.util.EventListener;

import spin.Invocation;
import spin.Spin;
import spin.Evaluator;

/**
 * An evaluator for spin-off that automatically spins-over all arguments of a
 * {@link spin.off.SpinOffEvaluator} if their corresponding parameter types are
 * subinterfaces of <code>java.util.EventListener</code>. <br>
 * Use an instance of this class on construction of a <em>Spin</em> object or
 * install it globally by calling the static method:
 * 
 * <pre>
 * Spin.setDefaultOffEvaluator(new ListenerSpinOver());
 * </pre>
 * 
 * @see #isListenerAdditionOrRemoval(java.lang.reflect.Method)
 * @see #isListener(java.lang.Class)
 */
public class ListenerSpinOver extends Evaluator {

	private Evaluator evaluator;

	/**
	 * Constructor.
	 */
	public ListenerSpinOver() {
		this(Spin.getDefaultOffEvaluator());
	}

	/**
	 * Constructor.
	 * 
	 * @param evaluator
	 *            the evaluator to wrap
	 */
	public ListenerSpinOver(Evaluator evaluator) {
		this.evaluator = evaluator;
	}

	public void evaluate(Invocation invocation) throws Throwable {

		Method method = invocation.getMethod();
		if (isListenerAdditionOrRemoval(method)) {
			Class[] types = method.getParameterTypes();
			Object[] args = invocation.getArguments();
			for (int t = 0; t < types.length; t++) {
				if (isListener(types[t])) {
					args[t] = spinOver(args[t]);
				}
			}
		}

		evaluator.evaluate(invocation);
	}

	/**
	 * Test if the given method is a listener addition or removal. For this the
	 * methods name must obey the name pattern
	 * <code>(add|remove).*Listener</code>.
	 * 
	 * @param method
	 *            method to test
	 * @return <code>true</code> if method obeys the name pattern of listener
	 *         addition or removal
	 */
	protected boolean isListenerAdditionOrRemoval(Method method) {
		String name = method.getName();
		return name.endsWith("Listener")
				&& (name.startsWith("add") || name.startsWith("remove"));
	}

	/**
	 * Test if the given class is a listener subinterface.
	 * 
	 * @param type
	 *            class to test
	 * @return <code>true</code> if the class is an sub-interface of
	 *         <code>java.util.EventListener</code>
	 */
	protected boolean isListener(Class type) {
		return type.isInterface() && EventListener.class.isAssignableFrom(type);
	}

	/**
	 * Spin-over the given object.
	 * 
	 * @param object
	 *            object to spin-over
	 * @return <em>Spin</em> proxy
	 */
	protected Object spinOver(Object object) {
		return Spin.over(object);
	}
}
