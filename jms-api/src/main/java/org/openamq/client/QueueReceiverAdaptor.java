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

package org.openamq.client;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueReceiver;

public class QueueReceiverAdaptor implements QueueReceiver {

	private final Queue _queue;
	private final MessageConsumer _consumer;
	
	public QueueReceiverAdaptor(Queue queue, MessageConsumer consumer) {
		_queue = queue;
		_consumer = consumer;
	}
	
	public Queue getQueue() throws JMSException {
		return _queue;
	}

	public void close() throws JMSException {
		_consumer.close();
	}

	public MessageListener getMessageListener() throws JMSException {
		return _consumer.getMessageListener();
	}

	public String getMessageSelector() throws JMSException {
		return _consumer.getMessageSelector();
	}

	public Message receive() throws JMSException {
		return _consumer.receive();
	}

	public Message receive(long arg0) throws JMSException {
		return _consumer.receive(arg0);
	}

	public Message receiveNoWait() throws JMSException {
		return _consumer.receiveNoWait();
	}

	public void setMessageListener(MessageListener arg0) throws JMSException {
		_consumer.setMessageListener(arg0);
	}

}
