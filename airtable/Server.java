package airtable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import airtable.model.Column;
import airtable.model.Data;
import airtable.model.Row;

public class Server {

  List<Client> clients;

  private Data data;

  public Server() {
    clients = new ArrayList<Client>();
    data = new Data();
  }

  public void addClient(Client client) {
    clients.add(client);
  }

  public boolean syncFromClient(Messages messages) {
    SyncAndResolve.syncMessages(data, messages);

    for (Client otherClient : clients) {
      if (otherClient.isOffline()) {
        continue;
      }
      otherClient.syncFromServer(convertDataToMessages());
    }

    return true;
  }

  public Messages convertDataToMessages() {
    Messages messages = new Messages();
    for (Column column : data.getColumns().values()) {
      String message = new StringJoiner(",")
          .add(column.getColumnId())
          .add(column.getColumnType().name())
          .toString();
      messages.addMessage(MessageType.ADD_COLUMN, message);
    }

    // While copying all the rows, use the rowIndex.
    for (int i=0; i<data.getRowIndex().size(); i++) {
      Row row = data.getRows().get(data.getRowIndex().get(i));
      String message1 = new StringJoiner(",")
          .add(row.getRowId())
          .toString();
      messages.addMessage(MessageType.ADD_ROW, message1);

      for (Map.Entry<String, Object> entry : row.getRowData().entrySet()) {
        String message2 = new StringJoiner(",")
            .add(row.getRowId())
            .add(entry.getKey()) // columnId
            .add(String.valueOf(entry.getValue()))
            .toString();
        messages.addMessage(MessageType.CHANGE_ROW, message2);
      }
    }
    return messages;
  }

  //-------------------------------
  // Getters and Setters
  //-------------------------------
  public Data getData() {
    return data;
  }
}
