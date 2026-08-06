package nl.tno.netnbus.messages;
import java.io.Serializable;

public class MessageObject implements Serializable, Cloneable {

  private int msgHandle;
  private String msgString = null;

  public MessageObject(int msgHandle) {
    this.msgHandle = msgHandle;
  }

  public int getMsgHandle() {
    return msgHandle;
  }

  public String getMsgString() {
    return msgString;
  }

  public void setMsgString(String msgString) {
    this.msgString = msgString;
  }
}