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

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Topic;
import javax.jms.TopicPublisher;

public class TopicPublisherAdaptor implements TopicPublisher {

	private final Topic _topic;
    private final MessageProducer _producer;

    public TopicPublisherAdaptor(Topic topic, MessageProducer producer) {
		_topic = topic;
		_producer = producer;
	}

	public Topic getTopic() throws JMSException {
		return _topic;
	}

	public void publish(Message arg0) throws JMSException {
		_producer.send(arg0);
	}

	public void publish(Topic arg0, Message arg1) throws JMSException {
		_producer.send(arg0, arg1);
	}

	public void publish(Message arg0, int arg1, int arg2, long arg3)
			throws JMSException {
		_producer.send(arg0, arg1, arg2, arg3);
	}

	public void publish(Topic arg0, Message arg1, int arg2, int arg3, long arg4)
			throws JMSException {
		_producer.send(arg0, arg1, arg2, arg3, arg4);
	}

	public void close() throws JMSException {
		_producer.close();
	}

	public int getDeliveryMode() throws JMSException {
		return _producer.getDeliveryMode();
	}

	public Destination getDestination() throws JMSException {
		return _producer.getDestination();
	}

	public boolean getDisableMessageID() throws JMSException {
		return _producer.getDisableMessageID();
	}

	public boolean getDisableMessageTimestamp() throws JMSException {
		return _producer.getDisableMessageTimestamp();
	}

	public int getPriority() throws JMSException {
		return _producer.getPriority();
	}

	public long getTimeToLive() throws JMSException {
		return _producer.getTimeToLive();
	}

	public void send(Message arg0) throws JMSException {
		_producer.send(arg0);
	}

	public void send(Destination arg0, Message arg1) throws JMSException {
		_producer.send(arg0, arg1);
	}

	public void send(Message arg0, int arg1, int arg2, long arg3)
			throws JMSException {
		_producer.send(arg0, arg1, arg2, arg3);
	}

	public void send(Destination arg0, Message arg1, int arg2, int arg3,
			long arg4) throws JMSException {
		_producer.send(arg0, arg1, arg2, arg3, arg4);
	}

	public void setDeliveryMode(int arg0) throws JMSException {
		_producer.setDeliveryMode(arg0);
	}

	public void setDisableMessageID(boolean arg0) throws JMSException {
		_producer.setDisableMessageID(arg0);
	}

	public void setDisableMessageTimestamp(boolean arg0) throws JMSException {
		_producer.setDisableMessageTimestamp(arg0);
	}

	public void setPriority(int arg0) throws JMSException {
		_producer.setPriority(arg0);
	}

	public void setTimeToLive(long arg0) throws JMSException {
		_producer.setTimeToLive(arg0);
	}

}
