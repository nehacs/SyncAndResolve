package airtable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Representation of a message that can be passed between a server and a client.
 */
public class Messages implements Serializable {

  private static final long serialVersionUID = 1L;
  private LinkedHashMap<MessageType, List<String>> messages;

  public Messages() {
    messages = new LinkedHashMap<MessageType, List<String>>();
  }

  public LinkedHashMap<MessageType, List<String>> getMessageMap() {
    return messages;
  }

  public void addMessage(MessageType messageType, String message) {
    List<String> messageList;
    if (messages.get(messageType) == null) {
      messageList = new ArrayList<String>();
      messages.put(messageType, messageList);
    } else {
      messageList = messages.get(messageType);
    }

    messageList.add(message);
  }
}
