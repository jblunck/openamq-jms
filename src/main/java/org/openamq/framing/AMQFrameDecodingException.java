package org.openamq.framing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openamq.AMQException;

public class AMQFrameDecodingException extends AMQException
{
    public AMQFrameDecodingException(String message)
    {
        super(message);
    }

    public AMQFrameDecodingException(String message, Throwable t)
    {
        super(message, t);
    }

    public AMQFrameDecodingException(Logger log, String message)
    {
        super(log, message);
    }

    public AMQFrameDecodingException(Logger log, String message, Throwable t)
    {
        super(log, message, t);
    }

}
