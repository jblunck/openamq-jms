package org.openamq.transacted;

import org.openamq.vmbroker.VmPipeBroker;
import org.openamq.client.AMQConnection;
import org.openamq.client.AMQQueue;
import org.openamq.client.AMQSession;
import org.openamq.AMQException;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.jms.Session;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.MessageConsumer;
import javax.jms.Message;
import javax.jms.TextMessage;

import junit.framework.JUnit4TestAdapter;

/**
 * @author Gordon Sim (gordon.r.sim@jpmorgan.com)
 */
public class TransactedTest extends VmPipeBroker
{
    private AMQQueue queue1;
    private AMQQueue queue2;

    private AMQConnection con;
    private Session session;
    private MessageConsumer consumer;
    private MessageProducer producer;

    private AMQConnection prepCon;
    private Session prepSession;
    private MessageProducer prepProducer;

    private AMQConnection testCon;
    private Session testSession;
    private MessageConsumer testConsumer1;
    private MessageConsumer testConsumer2;

    @Before
    public void setup() throws Exception
    {
        queue1 = new AMQQueue("Q1", false);
        queue2 = new AMQQueue("Q2", false);

        con = new AMQConnection("localhost:5672", "guest", "guest", "TransactedTest", "/test");
        session = con.createSession(true, 0);
        consumer = session.createConsumer(queue1);
        producer = session.createProducer(queue2);
        con.start();

        prepCon = new AMQConnection("localhost:5672", "guest", "guest", "PrepConnection", "/test");
        prepSession = prepCon.createSession(false, AMQSession.NO_ACKNOWLEDGE);
        prepProducer = prepSession.createProducer(queue1);
        prepCon.start();


        //add some messages
        prepProducer.send(prepSession.createTextMessage("A"));
        prepProducer.send(prepSession.createTextMessage("B"));
        prepProducer.send(prepSession.createTextMessage("C"));


        testCon = new AMQConnection("localhost:5672", "guest", "guest", "TestConnection", "/test");
        testSession = testCon.createSession(false, AMQSession.NO_ACKNOWLEDGE);
        testConsumer1 = testSession.createConsumer(queue1);
        testConsumer2 = testSession.createConsumer(queue2);
        testCon.start();
    }

    @After
    public void shutdown() throws Exception
    {
        con.close();
        testCon.close();
        prepCon.close();
    }

    @Test
    public void commit() throws Exception
    {
        //send and receive some messages
        producer.send(session.createTextMessage("X"));
        producer.send(session.createTextMessage("Y"));
        producer.send(session.createTextMessage("Z"));
        expect("A", consumer.receive(1000));
        expect("B", consumer.receive(1000));
        expect("C", consumer.receive(1000));

        //commit
        session.commit();

        //ensure sent messages can be received and received messages are gone
        expect("X", testConsumer2.receive(1000));
        expect("Y", testConsumer2.receive(1000));
        expect("Z", testConsumer2.receive(1000));

        assertTrue(null == testConsumer1.receive(1000));
    }

    @Test
    public void rollback() throws Exception
    {
        producer.send(session.createTextMessage("X"));
        producer.send(session.createTextMessage("Y"));
        producer.send(session.createTextMessage("Z"));
        expect("A", consumer.receive(1000));
        expect("B", consumer.receive(1000));
        expect("C", consumer.receive(1000));

        //rollback
        session.rollback();

        //ensure sent messages are not visible and received messages are requeued
        expect("A", consumer.receive(1000));
        expect("B", consumer.receive(1000));
        expect("C", consumer.receive(1000));

        assertTrue(null == testConsumer2.receive(1000));
    }

    private void expect(String text, Message msg) throws JMSException
    {
        assertTrue(msg instanceof TextMessage);
        assertEquals(text, ((TextMessage) msg).getText());
    }

    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(TransactedTest.class);
    }

}
