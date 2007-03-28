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
package spin.proxy;

import java.lang.reflect.Method;

import spin.Evaluator;
import spin.Invocation;
import spin.ProxyFactory;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * A factory of proxies utilizing CGLib.
 */
public class CGLibProxyFactory extends ProxyFactory {

	public Object createProxy(Object object, Evaluator evaluator) {
		return Enhancer.create(object.getClass(), new SpinMethodInterceptor(
				object, evaluator));
	}

	public boolean isProxy(Object object) {

		if (object == null) {
			return false;
		}

		if (!(object instanceof Factory)) {
			return false;
		}

		Factory factory = (Factory) object;
		return (factory.getCallback(0) instanceof SpinMethodInterceptor);
	}

	protected boolean areProxyEqual(Object proxy1, Object proxy2) {
		SpinMethodInterceptor methodInterceptor1 = (SpinMethodInterceptor) ((Factory) proxy1)
				.getCallback(0);
		SpinMethodInterceptor methodInterceptor2 = (SpinMethodInterceptor) ((Factory) proxy2)
				.getCallback(0);

		return methodInterceptor1.object == methodInterceptor2.object;
	}

	/**
	 * Method interceptor for the <em>Spin</em> proxy.
	 */
	private class SpinMethodInterceptor implements MethodInterceptor {

		private Object object;

		private Evaluator evaluator;

		/**
		 * Create a new handler of invocations.
		 * 
		 * @param object
		 *            the object to invoke methods on
		 * @param evaluator
		 *            the evaluator of methods
		 */
		public SpinMethodInterceptor(Object object, Evaluator evaluator) {
			this.object = object;
			this.evaluator = evaluator;
		}

		/**
		 * Handle the invocation of a method on the <em>Spin</em> proxy.
		 * 
		 * @param proxy
		 *            the proxy instance
		 * @param method
		 *            the method to invoke
		 * @param args
		 *            the arguments for the method
		 * @param methodProxy
		 *            non-intercepted proxy for the method
		 * @return the result of the invocation on the wrapped object
		 * @throws Throwable
		 *             if the wrapped method throws a <code>Throwable</code>
		 */
		public Object intercept(Object proxy, Method method, Object[] args,
				MethodProxy methodProxy) throws Throwable {
			return evaluteInvocation(evaluator, proxy, new Invocation(
					this.object, method, args));
		}
	}
}