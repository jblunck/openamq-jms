package org.openamq.client;

import javax.jms.Queue;

public class AMQQueue extends AMQDestination implements Queue
{
    private String _routingKey;
    /**
     * Create a reference to a non temporary queue. Note this does not actually imply the queue exists.
     * @param name the name of the queue
     */
    public AMQQueue(String name)
    {
        this(name, false);
    }

    /**
     * Create a queue with a specified name.
     *
     * @param name the destination name (used in the routing key)
     * @param temporary if true the broker will generate a queue name, also if true then the queue is autodeleted
     * and exclusive
     */
    public AMQQueue(String name, boolean temporary)
    {
        // queue name is set to null indicating that the broker assigns a name in the case of temporary queues
        // temporary queues are typically used as response queues
        this(name, temporary?null:name, temporary, temporary);
        _isDurable = !temporary;
    }

    /**
     * Create a reference to a queue. Note this does not actually imply the queue exists.
     * @param destinationName the queue name
     * @param queueName the queue name
     * @param exclusive true if the queue should only permit a single consumer
     * @param autoDelete true if the queue should be deleted automatically when the last consumers detaches
     */
    public AMQQueue(String destinationName, String queueName, boolean exclusive, boolean autoDelete)
    {
        super(AMQDestination.QUEUE_EXCHANGE_NAME, AMQDestination.QUEUE_EXCHANGE_CLASS, destinationName, exclusive,
              autoDelete, queueName);
    }

    /**
     * Create a reference to a queue. Note this does not actually imply the queue exists.
     * @param destinationName the queue name
     * @param queueName the queue name
     * @param durable true if the queue should survive server restart
     * @param exclusive true if the queue should only permit a single consumer
     * @param autoDelete true if the queue should be deleted automatically when the last consumers detaches
     */
    public AMQQueue(String destinationName, String queueName, boolean durable, boolean exclusive, boolean autoDelete)
    {
        super(AMQDestination.QUEUE_EXCHANGE_NAME, AMQDestination.QUEUE_EXCHANGE_CLASS, destinationName, durable, exclusive, autoDelete, queueName);
    }

    /**
     * Create a reference to a queue. Note this does not actually imply the queue exists.
     * @param destinationName the queue name
     * @param queueName the queue name
     * @param exchangeName the exchange name
     * @param exchangeClass the exchange type
     * @param durable true if the queue should survive server restart
     * @param exclusive true if the queue should only permit a single consumer
     * @param autoDelete true if the queue should be deleted automatically when the last consumers detaches
     */
    public AMQQueue(String destinationName, String queueName, String exchangeName, String exchangeClass, String routingKey, boolean durable, boolean exclusive, boolean autoDelete)
    {
        super(exchangeName, exchangeClass, destinationName, durable, exclusive,
              autoDelete, queueName);
        if (exchangeClass == "fanout")
        {
            _routingKey = new String("");
        }
        else if (routingKey != null) 
        {
           _routingKey = routingKey; 
        }
    }

    /**
     * Create a reference to a queue. Note this does not actually imply the queue exists.
     * @param destinationName the queue name
     * @param queueName the queue name
     * @param exchangeName the exchange name
     * @param exchangeClass the exchange type
     * @param exchangeDurable true if the exchange should survive server restart
     * @param exchangeAutoDelete true if the exchange should be deleted automatically when unused
     * @param durable true if the queue should survive server restart
     * @param exclusive true if the queue should only permit a single consumer
     * @param autoDelete true if the queue should be deleted automatically when the last consumers detaches
     */
    public AMQQueue(String destinationName, String queueName, String exchangeName, String exchangeClass, boolean exchangeDurable, boolean exchangeAutoDelete, String routingKey, boolean durable, boolean exclusive, boolean autoDelete)
    {
        super(exchangeName, exchangeClass, exchangeDurable, exchangeAutoDelete, destinationName, durable, exclusive,
              autoDelete, queueName);
        if (exchangeClass == "fanout")
        {
            _routingKey = new String("");
        }
        else if (routingKey != null) 
        {
           _routingKey = routingKey; 
        }
    }

    /**
     * Create a reference to a queue. Note this does not actually imply the queue exists.
     * @param destinationName the queue name
     * @param queueName the queue name
     * @param exchangeName the exchange name
     * @param exchangeClass the exchange type
     * @param exclusive true if the queue should only permit a single consumer
     * @param autoDelete true if the queue should be deleted automatically when the last consumers detaches
     */
    public AMQQueue(String destinationName, String queueName, String exchangeName, String exchangeClass, String routingKey, boolean exclusive, boolean autoDelete)
    {
        super(exchangeName, exchangeClass, destinationName, exclusive,
              autoDelete, queueName);
        if (exchangeClass == "fanout")
        {
            _routingKey = new String("");
        }
        else if (routingKey != null) 
        {
           _routingKey = routingKey; 
        }
    }

    public String getEncodedName()
    {
        return 'Q' + getQueueName();
    }

    public String getRoutingKey()
    {
        if (_routingKey == null)
        {
            return getQueueName();
        }
        else
        {
            return _routingKey;
        }
    }

    public boolean isNameRequired()
    {
        //If the name is null, we require one to be generated by the client so that it will#
        //remain valid if we failover (see BLZ-24)
        return getQueueName() == null;
    }
}
