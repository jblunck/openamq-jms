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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.spi.InitialContextFactory;

import org.openamq.client.AMQConnectionFactory;
import org.openamq.client.AMQQueue;
import org.openamq.client.AMQTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JNDIInitialContextFactory implements InitialContextFactory {
	protected final Logger _logger = LoggerFactory
			.getLogger(JNDIInitialContextFactory.class);

	private static final String[] DEFAULT_CONNECTION_FACTORY_NAMES = {
			"ConnectionFactory", "QueueConnectionFactory",
			"TopicConnectionFactory" };
	private static final String DEFAULT_CONNECTION_PROVIDER_URL = "amqp://guest:guest@127.0.0.1:5672/";

	private String CONNECTION_FACTORY_PREFIX = "connectionfactory.";
	private String QUEUE_PREFIX = "queue.";
	private String TOPIC_PREFIX = "topic.";
	private String DESTINATION_PREFIX = "destination.";

	@SuppressWarnings("unchecked")
	public Context getInitialContext(Hashtable<?, ?> environment)
			throws NamingException {

		Map<String, Object> bindings = new ConcurrentHashMap<String, Object>();
		Hashtable<String, Object> _environment;

		if (environment != null) {
			_environment = (Hashtable<String, Object>) environment.clone();
		} else {
			_environment = new Hashtable<String, Object>();
		}

		/* Check for a Provider URL */
		if (!_environment.containsKey(Context.PROVIDER_URL)) {
			String provider_url = System.getProperty(Context.PROVIDER_URL);
			if (provider_url != null) {
				_environment.put(Context.PROVIDER_URL, provider_url);
			} else {
				_environment.put(Context.PROVIDER_URL,
						DEFAULT_CONNECTION_PROVIDER_URL);
			}
		}

		createConnectionFactories(bindings, _environment);
		createQueues(bindings, _environment);
		createTopics(bindings, _environment);
		createDestinations(bindings, _environment);
		return new JNDIReadOnlyContext(bindings, _environment);
	}

	private ConnectionFactory createAMQConnectionFactory(String urlstr) {
		String uid = null;
		String passwd = null;
		String vpath = null;

		try {
			URI url = new URI(urlstr);
			if (url == null) {
				_logger.warn("Unable to parse URI: " + urlstr);
				return null;
			}

			if ("amqp".equals(url.getScheme()) != true) {
				_logger.warn("Unable to connect with scheme: "
						+ url.getScheme());
				return null;
			}

			if (url.getUserInfo() != null) {
				String info = url.getUserInfo();
				int sep = info.indexOf(':');

				if ((sep >= 0) && (sep + 1 < info.length())) {
					uid = info.substring(0, sep);
					passwd = info.substring(sep + 1);
				} else {
					uid = info;
				}
			}

			vpath = url.getPath();
			while (vpath.startsWith("/")) {
				vpath = vpath.substring(1);
			}
			vpath = "/".concat(vpath);

			_logger.debug("Trying connection with " + url.getHost() + ", port "
					+ url.getPort() + ", uid " + uid + ", path " + vpath);

			return new AMQConnectionFactory(url.getHost(), url.getPort(), uid,
					passwd, vpath);
		} catch (URISyntaxException e) {
			_logger.warn("Unable to create factory " + urlstr + " : " + e);
		}

		return null;
	}

	private void createConnectionFactories(Map<String, Object> bindings,
			Hashtable<String, Object> environment) {

		Map<String, String> m = new HashMap<String, String>();

		for (Iterator<Entry<String, Object>> iter = environment.entrySet()
				.iterator(); iter.hasNext();) {
			Entry<String, Object> entry = iter.next();
			String key = entry.getKey().toString();
			if (key.startsWith(CONNECTION_FACTORY_PREFIX)) {
				m.put(key.substring(CONNECTION_FACTORY_PREFIX.length()), entry
						.getValue().toString().trim());
			}
		}

		if (m.size() == 0) {
			_logger
					.info("No connection factory names defined. Using default ones instead!");
			for (String name : DEFAULT_CONNECTION_FACTORY_NAMES)
				m.put(name, environment.get(Context.PROVIDER_URL).toString());
		}

		for (Iterator<Entry<String, String>> iter = m.entrySet().iterator(); iter
				.hasNext();) {
			Entry<String, String> entry = iter.next();
			ConnectionFactory cf = createAMQConnectionFactory(entry.getValue()
					.toString().trim());
			if (cf != null) {
				bindings.put(entry.getKey().toString().trim(), cf);
			}
		}

	}

	private Queue createAMQQueue(String name) {
		return new AMQQueue(name);
	}

	private void createQueues(Map<String, Object> bindings,
			Hashtable<String, Object> environment) {
		for (Iterator<Entry<String, Object>> iter = environment.entrySet()
				.iterator(); iter.hasNext();) {
			Entry<String, Object> entry = iter.next();
			String key = entry.getKey().toString();
			if (key.startsWith(QUEUE_PREFIX)) {
				String jndiName = key.substring(QUEUE_PREFIX.length());
				Queue q = createAMQQueue(entry.getValue().toString().trim());
				if (q != null) {
					bindings.put(jndiName, q);
				}
			}
		}

	}

	private Topic createAMQTopic(String name, String key) {
		return new AMQTopic(key, true);
	}

	private void createTopics(Map<String, Object> bindings,
			Hashtable<String, Object> environment) {
		for (Iterator<Entry<String, Object>> iter = environment.entrySet()
				.iterator(); iter.hasNext();) {
			Entry<String, Object> entry = iter.next();
			String key = entry.getKey().toString();
			if (key.startsWith(TOPIC_PREFIX)) {
				String jndiName = key.substring(TOPIC_PREFIX.length());
				Topic t = createAMQTopic(jndiName, entry.getValue().toString().trim());
				if (t != null) {
					bindings.put(jndiName, t);
				}
			}
		}
	}

	private Destination createDestination(String bindingURL) throws OperationNotSupportedException {
		JNDIBindingURL binding;
		try {
			binding = JNDIBindingURL.parse(bindingURL);
		} catch (URISyntaxException urlse) {
			_logger.warn("Unable to create destination:" + urlse, urlse);
			return null;
		}

		if (binding.getScheme().equals("direct")) {
			_logger.debug("Creating direct exchange");
			throw new OperationNotSupportedException();
		} else if (binding.getScheme().equals("fanout")) {
			_logger.debug("Creating fanout exchange");
			throw new OperationNotSupportedException();
		} else if (binding.getScheme().equals("header")) {
			_logger.debug("Creating header exchange");
			throw new OperationNotSupportedException();
		} else if (binding.getScheme().equals("topic")) {
			_logger.debug("Creating topic exchange");
			return new AMQTopic(binding.getTopicName(), true);
		}

		_logger.warn("Binding: '" + binding + "' not supported");
		return null;
	}

	private void createDestinations(Map<String, Object> bindings,
			Hashtable<String, Object> environment) throws NamingException {
		for (Iterator<Entry<String, Object>> iter = environment.entrySet()
				.iterator(); iter.hasNext();) {
			Entry<String, Object> entry = iter.next();
			String key = entry.getKey().toString();
			if (key.startsWith(DESTINATION_PREFIX)) {
				String jndiName = key.substring(DESTINATION_PREFIX.length());
				Destination dest = createDestination(entry.getValue()
						.toString().trim());
				if (dest != null) {
					bindings.put(jndiName, dest);
				}
			}
		}
	}

}
