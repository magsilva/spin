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
package spin;

import junit.framework.TestCase;
import spin.Evaluator;
import spin.Invocation;
import spin.ProxyFactory;

/**
 * Abstract base class for tests of proxy factories.
 */
public abstract class AbstractProxyFactoryTest extends TestCase {

	protected abstract ProxyFactory getFactory();

	/**
	 * Test running through a proxy.
	 */
	public void testRun() {

		final RunnableBean runnable = new RunnableBean();

		Evaluator evaluator = new Evaluator() {
			public void evaluate(Invocation invocation) throws Throwable {
				assertEquals(false, runnable.evaluated);
				assertEquals(false, runnable.run);

				runnable.evaluated = true;

				invocation.evaluate();

				assertEquals(true, runnable.run);
			}
		};

		Runnable proxy = (Runnable) getFactory().createProxy(runnable,
				evaluator);

		proxy.run();
	}

	/**
	 * Test reflexivity.
	 * 
	 * @throws Exception
	 */
	public void testReflexive() throws Exception {

		Runnable runnable = new RunnableBean();

		Runnable proxy = (Runnable) getFactory().createProxy(runnable,
				createEvaluator());

		assertTrue("equals() is not reflexive", proxy.equals(proxy));
	}

	/**
	 * Test equality of two proxies of the same bean.
	 * 
	 * @throws Exception
	 */
	public void testTwoProxiesOfSameBeanAreEqual() throws Exception {

		Runnable runnable = new RunnableBean();

		Runnable proxy1 = (Runnable) getFactory().createProxy(runnable,
				createEvaluator());
		Runnable proxy2 = (Runnable) getFactory().createProxy(runnable,
				createEvaluator());

		assertTrue("two proxies of same bean are not equal", proxy1
				.equals(proxy2));
	}

	/**
	 * Test non-equality of two proxies of different beans.
	 * 
	 * @throws Exception
	 */
	public void testTwoProxiesOfDifferentBeansAreNotEqual() throws Exception {

		Runnable runnable1 = new RunnableBean();

		Runnable runnable2 = new RunnableBean();

		Runnable proxy1 = (Runnable) getFactory().createProxy(runnable1,
				createEvaluator());
		Runnable proxy2 = (Runnable) getFactory().createProxy(runnable2,
				createEvaluator());

		assertTrue("two proxies of different beans are equal", !proxy1
				.equals(proxy2));
	}

	/**
	 * Runnable mock.
	 */
	public static class RunnableBean implements Runnable {

		/**
		 * Was this runnable evaluated.
		 */
		public boolean evaluated = false;

		/**
		 * Was this runnable run.
		 */
		public boolean run = false;

		public void run() {
			run = true;
		}
	}

	protected Evaluator createEvaluator() {
		return new Evaluator() {
			public void evaluate(Invocation invocation) throws Throwable {
				invocation.evaluate();
			};
		};
	}
}