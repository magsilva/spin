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

import javax.swing.JFrame;

import spin.proxy.JDKProxyFactory;

/**
 * Test of JDK proxies.
 */
public class JDKProxyFactoryTest extends AbstractProxyFactoryTest {

	protected ProxyFactory getFactory() {
		return new JDKProxyFactory();
	}

	/**
	 * Test handling of non-accessible interfaces.<br>
	 * Since Java 1.6 all {@link javax.swing.JComponent}s implement a package
	 * protected interface
	 * <code>javax.swing.TransferHandler.HasGetTransferHandler</code>.
	 * 
	 * @see JDKProxyFactory#createProxy(Object, Evaluator)
	 */
	public void testNonAccessibleInterface() {

		getFactory().createProxy(new JFrame() {

		}, createEvaluator());
	}
}