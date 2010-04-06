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
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.CompoundName;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.NotContextException;
import javax.naming.OperationNotSupportedException;
import javax.naming.spi.NamingManager;

public class JNDIReadOnlyContext implements Context {

	public static class NameParserImpl implements NameParser {

		private static final Properties _syntax = new Properties();
		static {
			_syntax.put("jndi.syntax.direction", "flat");
			_syntax.put("jndi.syntax.ignorecase", "false");
			_syntax.put("jndi.syntax.escape", "\\");
			_syntax.put("jndi.syntax.beginquote", "'");
			_syntax.put("jndi.syntax.beginquote2", "\"");
			_syntax.put("jndi.syntax.trimblanks", "true");
		}

		public Name parse(String arg0) throws NamingException {
			return new CompoundName(arg0, _syntax);
		}

	}

	public class ListOfNames implements NamingEnumeration<NameClassPair> {

		private Iterator<String> _iterator;

		public ListOfNames(Iterator<String> iterator) {
			_iterator = iterator;
		}

		public void close() throws NamingException {
			// TODO Auto-generated method stub

		}

		public boolean hasMore() throws NamingException {
			return _iterator.hasNext();
		}

		public NameClassPair next() throws NamingException {
			String next = _iterator.next();
			return new NameClassPair(next, _bindings.get(next).getClass()
					.getName());
		}

		public boolean hasMoreElements() {
			try {
				return hasMore();
			} catch (NamingException e) {
				throw new NoSuchElementException(e.toString());
			}
		}

		public NameClassPair nextElement() {
			try {
				return next();
			} catch (NamingException e) {
				throw new NoSuchElementException(e.toString());
			}
		}

	}

	public class ListOfBindings implements NamingEnumeration<Binding> {

		protected Iterator<String> _iterator;

		public ListOfBindings(Iterator<String> iterator) {
			_iterator = iterator;
		}

		public void close() throws NamingException {
			// TODO Auto-generated method stub
			
		}

		public boolean hasMore() throws NamingException {
			return _iterator.hasNext();
		}

		public Binding next() throws NamingException {
			String next = _iterator.next();
			return new Binding(next, _bindings.get(next));
		}

		public boolean hasMoreElements() {
			try {
				return hasMore();
			} catch (NamingException e) {
				throw new NoSuchElementException(e.toString());
			}
		}

		public Binding nextElement() {
			try {
				return next();
			} catch (NamingException e) {
				throw new NoSuchElementException(e.toString());
			}
		}
	}

	private Map<String, Object> _bindings;
	private Hashtable<String, Object> _environment;
	private static final NameParser _nameparser = new NameParserImpl();

	@SuppressWarnings("unchecked")
	public JNDIReadOnlyContext(Map<String, Object> bindings,
			Hashtable<String, Object> environment) {

		if (bindings != null) {
			_bindings = new HashMap<String, Object>(bindings);
		} else {
			_bindings = new HashMap<String, Object>();
		}

		if (environment != null) {
			_environment = (Hashtable<String, Object>) environment.clone();
		} else {
			_environment = new Hashtable<String, Object>();
		}
	}

	public Object addToEnvironment(String arg0, Object arg1)
			throws NamingException {
		return _environment.put(arg0, arg1);
	}

	public void bind(Name arg0, Object arg1) throws NamingException {
		throw new OperationNotSupportedException(
				"Read-only service does not support updates");
	}

	public void bind(String arg0, Object arg1) throws NamingException {
		throw new OperationNotSupportedException(
				"Read-only service does not support updates");
	}

	public void close() throws NamingException {
		// TODO Auto-generated method stub

	}

	public Name composeName(Name arg0, Name arg1) throws NamingException {
		Name result;

		// Both are compound names, compose using compound name rules
		if (!(arg0 instanceof CompositeName)
				&& !(arg1 instanceof CompositeName)) {
			result = (Name) (arg1.clone());
			result.addAll(arg0);
			return new CompositeName().add(result.toString());
		}

		// Simplistic implementation: do not support federation
		throw new OperationNotSupportedException(
				"Do not support composing composite names");
	}

	public String composeName(String arg0, String arg1) throws NamingException {
		return composeName(_nameparser.parse(arg0), _nameparser.parse(arg1))
				.toString();
	}

	public Context createSubcontext(Name arg0) throws NamingException {
		throw new OperationNotSupportedException(
				"Read-only service does not support updates");
	}

	public Context createSubcontext(String arg0) throws NamingException {
		throw new OperationNotSupportedException(
				"Read-only service does not support updates");
	}

	public void destroySubcontext(Name arg0) throws NamingException {
		throw new OperationNotSupportedException(
				"Read-only service does not support updates");
	}

	public void destroySubcontext(String arg0) throws NamingException {
		throw new OperationNotSupportedException(
				"Read-only service does not support updates");
	}

	public Hashtable<?, ?> getEnvironment() throws NamingException {
		return (Hashtable<?, ?>) _environment.clone();
	}

	public String getNameInNamespace() throws NamingException {
		return "";
	}

	public NameParser getNameParser(Name arg0) throws NamingException {
		// Do lookup to verify that the name exists
		Object obj = lookup(arg0);
		if (obj instanceof Context) {
			((Context) obj).close();
		}
		return _nameparser;
	}

	public NameParser getNameParser(String arg0) throws NamingException {
		return getNameParser(_nameparser.parse(arg0));
	}

	public NamingEnumeration<NameClassPair> list(Name arg0)
			throws NamingException {
		if (arg0.isEmpty()) {
			return new ListOfNames(_bindings.keySet().iterator());
		}

		Object target = lookup(arg0);
		if (target instanceof Context) {
			try {
				return ((Context) target).list("");
			} finally {
				((Context) target).close();
			}
		}
		throw new NotContextException(arg0 + " cannot be listed");
	}

	public NamingEnumeration<NameClassPair> list(String arg0)
			throws NamingException {
		return list(_nameparser.parse(arg0));
	}

	public NamingEnumeration<Binding> listBindings(Name arg0)
			throws NamingException {
		if (arg0.isEmpty()) {
			return new ListOfBindings(_bindings.keySet().iterator());
		}

		Object target = lookup(arg0);
		if (target instanceof Context) {
			try {
				return ((Context) target).listBindings("");
			} finally {
				((Context) target).close();
			}
		}
		throw new NotContextException(arg0 + " cannot be listed");
	}

	public NamingEnumeration<Binding> listBindings(String arg0)
			throws NamingException {
		return listBindings(_nameparser.parse(arg0));
	}

	public Object lookup(Name arg0) throws NamingException {

		if (arg0 instanceof CompositeName) {
			if (arg0.size() > 1) {
				// Don't support federation
				throw new InvalidNameException(arg0.toString()
						+ " has more components than I can handle");
			}

			return lookup(_nameparser.parse(arg0.get(0)));
		} else {
			Object obj = _bindings.get(arg0.toString());

			if (obj == null) {
				throw new NameNotFoundException(arg0 + " not found");
			}

			// Call getObjectInstance for using any object factories
			try {
				return NamingManager.getObjectInstance(obj, arg0, this,
						_environment);
			} catch (Exception e) {
				NamingException ne = new NamingException(
						"getObjectInstance failed");
				ne.setRootCause(e);
				throw ne;
			}
		}
	}

	public Object lookup(String arg0) throws NamingException {
		return lookup(_nameparser.parse(arg0));
	}

	public Object lookupLink(Name arg0) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object lookupLink(String arg0) throws NamingException {
		// TODO Auto-generated method stub
		return null;
	}

	public void rebind(Name arg0, Object arg1) throws NamingException {
		throw new OperationNotSupportedException(
				"Read-only service does not support updates");
	}

	public void rebind(String arg0, Object arg1) throws NamingException {
		throw new OperationNotSupportedException(
				"Read-only service does not support updates");
	}

	public Object removeFromEnvironment(String arg0) throws NamingException {
		return _environment.remove(arg0);
	}

	public void rename(Name arg0, Name arg1) throws NamingException {
		throw new OperationNotSupportedException(
				"Read-only service does not support updates");
	}

	public void rename(String arg0, String arg1) throws NamingException {
		throw new OperationNotSupportedException(
				"Read-only service does not support updates");
	}

	public void unbind(Name arg0) throws NamingException {
		throw new OperationNotSupportedException(
				"Read-only service does not support updates");
	}

	public void unbind(String arg0) throws NamingException {
		throw new OperationNotSupportedException(
				"Read-only service does not support updates");
	}

}
