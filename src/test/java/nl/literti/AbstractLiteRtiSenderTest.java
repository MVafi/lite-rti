package nl.literti;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import nl.literti.messages.MessageObject;
import nl.literti.utils.BinaryHelper;

public class AbstractLiteRtiSenderTest {

  @Test(timeout = 20000)
  public void concurrentSendersPreserveCompleteFramesOnSharedSocket() throws Exception {
    int writerCount = 8;
    int messagesPerWriter = 100;
    RecordingSocket socket = new RecordingSocket();
    CountDownLatch start = new CountDownLatch(1);
    List<Future<?>> writers = new ArrayList<>();

    try (socket; ExecutorService executor = Executors.newFixedThreadPool(writerCount)) {
      for (int writer = 0; writer < writerCount; writer++) {
        int writerId = writer;
        // Different sender instances must still share the connection's write lock.
        AbstractLiteRtiSender sender = new AbstractLiteRtiSender();
        writers.add(executor.submit(() -> {
          start.await();
          for (int index = 0; index < messagesPerWriter; index++) {
            MessageObject message = new MessageObject(writerId * messagesPerWriter + index);
            message.setMsgString("writer " + writerId + " message " + index);
            sender.serializeSendMsg(socket, message);
          }
          return null;
        }));
      }
      start.countDown();
      for (Future<?> writer : writers) {
        writer.get(10, TimeUnit.SECONDS);
      }

      Set<Integer> received = new HashSet<>();
      try (DataInputStream input = new DataInputStream(
          new ByteArrayInputStream(socket.bytes.toByteArray()))) {
        for (int index = 0; index < writerCount * messagesPerWriter; index++) {
          int length = input.readInt();
          assertTrue("Invalid frame length: " + length, length > 0 && length <= input.available());
          byte[] payload = new byte[length];
          input.readFully(payload);
          MessageObject message = (MessageObject) BinaryHelper.deserializeBinaryMessage(payload);
          assertTrue("Duplicate response handle", received.add(message.getMsgHandle()));
          int writerId = message.getMsgHandle() / messagesPerWriter;
          int messageIndex = message.getMsgHandle() % messagesPerWriter;
          assertEquals("writer " + writerId + " message " + messageIndex, message.getMsgString());
        }
        assertEquals("Unexpected trailing bytes", 0, input.available());
      }
      assertEquals(writerCount * messagesPerWriter, received.size());
    }
  }

  private static final class RecordingSocket extends Socket {
    private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    private final OutputStream output = new OutputStream() {
      @Override
      public void write(int value) {
        assertTrue("Writes must hold the shared socket lock", Thread.holdsLock(RecordingSocket.this));
        bytes.write(value);
      }

      @Override
      public void write(byte[] data, int offset, int length) {
        assertTrue("Writes must hold the shared socket lock", Thread.holdsLock(RecordingSocket.this));
        bytes.write(data, offset, length);
      }
    };

    @Override
    public OutputStream getOutputStream() throws IOException {
      return output;
    }
  }
}