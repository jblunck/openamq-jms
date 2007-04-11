/**
 * Date Created: 20-Jun-2005
 *************************************************************************
 * (c) Copyright JP Morgan Chase Ltd 2005. All rights reserved. No part of
 * this program may be photocopied reproduced or translated to another
 * program language without prior written consent of JP Morgan Chase Ltd
 *************************************************************************/
package org.openamq.client;

import javax.jms.JMSException;
import javax.jms.Topic;

/**
 * @author Robert Greig (robert.j.greig@jpmorgan.com)
 */
public class AMQTopic extends AMQDestination implements Topic
{
    public AMQTopic(String name)
    {
        this(name, false);
    }

    public AMQTopic(String name, boolean temporary)
    {
        super(AMQDestination.TOPIC_EXCHANGE_NAME, AMQDestination.TOPIC_EXCHANGE_CLASS, name, temporary, temporary, null);
        _isDurable = !temporary;
    }

    /**
     * Constructor for use in creating a topic to represent a durable subscription
     * @param topic
     * @param clientId
     * @param subscriptionName
     */
    public AMQTopic(AMQTopic topic, String clientId, String subscriptionName)
    {
        super(AMQDestination.TOPIC_EXCHANGE_NAME, AMQDestination.TOPIC_EXCHANGE_CLASS, topic.getDestinationName(), false, false, clientId + ":" + subscriptionName);
    }

    public String getTopicName() throws JMSException
    {
        return super.getDestinationName();
    }

    public String getEncodedName()
    {
        return 'T' + getDestinationName();
    }

     public String getRoutingKey()
    {
        return getDestinationName();
    }

    public boolean isNameRequired()
    {
        //topics always rely on a server generated queue name (see BLZ-24)
        return false;
    }

    /**
     * Override since the queue is always private and we must ensure it remains null. If not,
     * reuse of the topic when registering consumers will make all consumers listen on the same (private) queue rather
     * than getting their own (private) queue.
     *
     * This is relatively nasty but it is difficult to come up with a more elegant solution, given
     * the requirement in the case on AMQQueue and possibly other AMQDestination subclasses to
     * use the underlying queue name even where it is server generated.
     */
    public void setQueueName(String queueName)
    {
    }
}
