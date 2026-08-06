package nl.tno.netnbus.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class BinaryHelper {

    static public byte[] serializeRequestObject(Object request) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();     // Create an in-memory buffer to hold the serialized data
        ObjectOutputStream oos = new ObjectOutputStream(baos);        // Wrap the buffer with an ObjectOutputStream to enable object serialization
        oos.writeObject(request);                                     // Serialize the CreateFederationRequest object (includes name + FOM) to the buffer
        byte[] requestBytes = baos.toByteArray();                     // Extract the serialized bytes from the buffer to send over the network
        return requestBytes;
    }

    static public Object deserializeBinaryMessage(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);     // Create an in-memory stream from the received bytes
        ObjectInputStream ois = new ObjectInputStream(bais);            // Wrap the stream with ObjectInputStream to deserialize Java objects
        Object obj = null;
        try{
            obj = ois.readObject();                                     // Read and reconstruct the object from the byte stream
        } catch (ClassNotFoundException e) {
            throw new IOException("Class not found during deserialization", e);
        }
        return obj;
    }
}
