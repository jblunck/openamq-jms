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
import java.util.*;

public class JNDIBindingURL {
	private URI _uri;
	private Map<String,List<String>> _map;
	
	protected void parseOptionsQuery(String query) throws URISyntaxException {
		StringTokenizer st = new StringTokenizer(query, ",&");

		while (st.hasMoreTokens()) {
			int index;
			String token = st.nextToken();

			index = token.indexOf('=');
			if (index == -1)
				throw new URISyntaxException("Invalid option ", token);

			String key = token.substring(0, index);
			String value = token.substring(index + 1);

			List<String> l = _map.get(key);
			if (l == null)
				_map.put(key, l=new ArrayList<String>());
			l.add(value);
		}
	}

	public JNDIBindingURL(URI uri) throws URISyntaxException {
		this._uri = uri;
		this._map = new HashMap<String,List<String>>();
		if (this.getQuery() != null)
			parseOptionsQuery(this.getQuery());
	}

	public static JNDIBindingURL parse(String str) throws URISyntaxException {
		URI uri = new URI(str);

		return new JNDIBindingURL(uri);
	}

	public List<String> getOption(String key) {
		return _map.get(key);
	}

	public String getScheme() {
		return _uri.getScheme();
	}

	public String getHost() {
		return _uri.getHost();
	}

	public String getPath() {
		return _uri.getPath();
	}

	public String getQuery() {
		String query = "";
		if (_uri.getQuery() != null)
			query = query.concat(_uri.getQuery());
		if (_uri.getFragment() != null)
			query = query.concat("#" + _uri.getFragment());
		return query.isEmpty() ? null : query;
	}

	public String getQueueName() {
		String path = _uri.getPath();
		String queue = path.substring(path.lastIndexOf('/') + 1);

		if (queue.length() == 0)
			return null;
		else
			return queue;
	}

	public String getTopicName() {
		if (!_uri.getScheme().equals("topic"))
			return null;

		String path = _uri.getPath();
		// If we only have one component, this is a queue
		if (path.indexOf('/') == path.lastIndexOf('/'))
			return null;
		String topic = path.substring(path.lastIndexOf('/') + 1);
		if (topic.length() == 0)
			return null;
		else
			return topic;
	}
}
