/****************************************************************************
*
* Copyright (c) 2010 Novell, Inc.
*
* Permission is hereby granted, free of charge, to any person obtaining a
* copy of this software and associated documentation files (the "Software"),
* to deal in the Software without restriction, including without limitation
* the rights to use, copy, modify, merge, publish, distribute, sublicense,
* and/or sell copies of the Software, and to permit persons to whom the
* Software is furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
* FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
* DEALINGS IN THE SOFTWARE.
*
****************************************************************************/

package org.openamq.jndi;

import java.util.HashMap;
import java.util.Map;

import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.InvalidNameException;

import org.junit.Test;
import static junit.framework.Assert.*;

public class JNDIReadOnlyContextTest {

	@Test(expected = InvalidNameException.class)
	public void composeInvalidName() throws NamingException {

		JNDIReadOnlyContext context = new JNDIReadOnlyContext(null, null);
		context.composeName("myTopic", "topic");
	}

	@Test
	public void composeName() throws NamingException {

		JNDIReadOnlyContext context = new JNDIReadOnlyContext(null, null);
		context.composeName("myTopic", "");
	}

	@Test
	public void getNamespaceName() throws NamingException {

		JNDIReadOnlyContext context = new JNDIReadOnlyContext(null, null);
		assertEquals("", context.getNameInNamespace());
	}

	@Test
	public void lookup() throws NamingException {

		Map<String,Object> map = new HashMap<String,Object>();
		map.put("a", new String("This is an A"));
		map.put("b.a", new String("This is an B"));

		JNDIReadOnlyContext context = new JNDIReadOnlyContext(map, null);		
		assertEquals(map.get("a"), context.lookup("a"));
	}

	@Test(expected = NameNotFoundException.class)
	public void lookupNegative() throws NamingException {

		Map<String,Object> map = new HashMap<String,Object>();
		map.put("a", new String("This is an A"));
		map.put("b.a", new String("This is an B"));

		JNDIReadOnlyContext context = new JNDIReadOnlyContext(map, null);		
		context.lookup("b");
	}

	@Test
	public void list() throws NamingException {

		Map<String,Object> map = new HashMap<String,Object>();
		map.put("a", new String("This is an A"));
		map.put("b.a", new String("This is an B"));

		JNDIReadOnlyContext context = new JNDIReadOnlyContext(map, null);
		
		for (NamingEnumeration<NameClassPair> list = context.list(""); list.hasMore(); ) {
			NameClassPair pair = list.next();
			assertTrue(map.containsKey(pair.getName()));
			assertEquals("java.lang.String", pair.getClassName());
		}
		
	}

}
