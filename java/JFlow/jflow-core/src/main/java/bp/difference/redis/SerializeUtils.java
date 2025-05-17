package bp.difference.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import java.io.*;

public class SerializeUtils<T> implements RedisSerializer<T>
{
    private static Logger logger = LoggerFactory.getLogger(SerializeUtils.class);

    public static boolean isEmpty(byte[] data)
    {
        return (data == null || data.length == 0);
    }

    /**
     * 序列化
     * 
     * @param t
     * @return
     * @throws SerializationException
     */
    @Override
    public byte[] serialize(T t) throws SerializationException
    {
        byte[] result = null;

        if (t == null)
        {
            return new byte[0];
        }
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream(128);
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteStream))
        {

            if (!(t instanceof Serializable))
            {
                throw new IllegalArgumentException(
                        SerializeUtils.class.getSimpleName() + " requires a Serializable payload "
                                + "but received an object of type [" + t.getClass().getName() + "]");
            }

            objectOutputStream.writeObject(t);
            objectOutputStream.flush();
            result = byteStream.toByteArray();
        }
        catch (Exception ex)
        {
            logger.error("Failed to serialize", ex);
        }
        return result;
    }

    /**
     * 反序列化
     * 
     * @param bytes
     * @return
     * @throws SerializationException
     */
    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(byte[] bytes) throws SerializationException
    {
        T result = null;
        if (isEmpty(bytes))
        {
            return null;
        }
        ObjectInputStream objectInputStream;
        try
        {
            objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
            result = (T) objectInputStream.readObject();
        }
        catch (Exception e)
        {
            logger.error("Failed to deserialize", e);
        }
        return result;
    }
}
