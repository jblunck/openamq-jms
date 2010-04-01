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
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.*;

public class JNDIBindingURLTest {
	private static final String[] _urls = new String[] {
		"exchclass://exchname/destination/queue?option='value1',option='value2'",
		"direct://amq.direct//message_queue?routingkey='routing_key'",
		"fanout://amq.fanout//message_queue",
		"topic://amq.topic//example.MyTopic?routingkey='stocks.#'",
		"topic://amq.topic/message_queue?bindingkey='usa.*'&bindingkey='control',exclusive='true'",
		"topic://amq.topic//?bindingKey='usa.*',bindingkey='control',exclusive='true'",
		"direct://amq.direct/dummyDest/myQueue?routingkey='abc.*'",
		"exchange.Class://exchangeName/Destination/Queue",
		"exchangeClass://exchangeName/Destination/?option='value',option2='value2'",
		"IBMPerfQueue1?durable='true'",
		"exchangeClass://exchangeName/Destination/?bindingkey='key1',bindingkey='key2'",
		"exchangeClass://exchangeName/Destination/?bindingkey='key1'&routingkey='key2'",
	};

	@Test
	public void construct() throws URISyntaxException {
		URI uri = new URI(_urls[0]);
		new JNDIBindingURL(uri);
	}

	@Test
	public void parseURI() throws URISyntaxException {
		JNDIBindingURL url = JNDIBindingURL.parse(_urls[0]);
		assertEquals("exchclass", url.getScheme());
		assertEquals("exchname", url.getHost());
		assertEquals("/destination/queue", url.getPath());
		assertEquals("option='value1',option='value2'", url.getQuery());
	}
	
	@Test
	public void parseOptions() throws URISyntaxException {
		JNDIBindingURL url = JNDIBindingURL.parse(_urls[0]);
		List<String> options = url.getOption("option");
		assertTrue(options.size() == 2);
		assertTrue(options.contains("'value1'"));
		assertTrue(options.contains("'value2'"));
	}

	@Test
	public void parseQueue1() throws URISyntaxException {
		JNDIBindingURL url = JNDIBindingURL.parse(_urls[0]);
		assertEquals("queue", url.getQueueName());
	}
		
	@Test
	public void parseQueue2() throws URISyntaxException {
		JNDIBindingURL url = JNDIBindingURL.parse(_urls[9]);
		assertEquals("IBMPerfQueue1", url.getQueueName());
	}

	@Test
	public void parseQueue3() throws URISyntaxException {
		JNDIBindingURL url = JNDIBindingURL.parse(_urls[5]);
		assertEquals(null, url.getQueueName());
	}

	@Test
	public void parseTopic1() throws URISyntaxException {
		JNDIBindingURL url = JNDIBindingURL.parse(_urls[3]);
		assertEquals("example.MyTopic", url.getTopicName());
	}

	@Test
	public void parseTopic2() throws URISyntaxException {
		JNDIBindingURL url = JNDIBindingURL.parse(_urls[4]);
		assertEquals(null, url.getTopicName());
	}

	@Test(expected = URISyntaxException.class)
	public void illegalURI() throws URISyntaxException {
		JNDIBindingURL.parse("exch_class://exchname/");
	}

	@Test
	public void parseURLS() throws URISyntaxException {
		for (String url: _urls) {
			JNDIBindingURL.parse(url);
		}
	}

	@Test
	public void missingExchangeClass() throws URISyntaxException {
		JNDIBindingURL url = JNDIBindingURL.parse(_urls[9]);
		assertEquals(null, url.getScheme());
		assertEquals("'true'", url.getOption("durable").get(0));
	}

	@Test
	public void multipleBindingKeys() throws URISyntaxException {
		JNDIBindingURL url = JNDIBindingURL.parse(_urls[4]);
		assertEquals("'true'", url.getOption("exclusive").get(0));
		assertTrue(url.getOption("bindingkey").contains("'usa.*'"));
		assertTrue(url.getOption("bindingkey").contains("'control'"));
	}
}
