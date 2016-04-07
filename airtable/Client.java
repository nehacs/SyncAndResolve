package airtable;

import java.util.StringJoiner;
import java.util.logging.Level;
import java.util.logging.Logger;

import airtable.model.Column.ColumnType;
import airtable.model.Data;
import airtable.model.Row;

public class Client {

  private boolean offline;
  private Data data;
  private Server server;
  private Messages messages;

  private static final Logger LOG = Logger.getLogger(Client.class.getName());

  public Client(Server server) {
    this.server = server;
    this.messages = new Messages();
    if (data == null) {
      data = new Data();
    } else {
      SyncAndResolve.syncMessages(data, server.convertDataToMessages());
    }
    server.addClient(this);
  }

  //-------------------------------
  // Data manipulation methods
  //-------------------------------
  public void createColumn(String columnId, ColumnType columnType) {
    data.createColumn(columnId, columnType);
    String message = new StringJoiner(",")
        .add(columnId)
        .add(columnType.name())
        .toString();
    addMessage(MessageType.ADD_COLUMN, message);
  }

  public void destroyColumn(String columnId) {
    data.removeColumn(columnId);
    String message = new StringJoiner(",")
        .add(columnId)
        .toString();
    addMessage(MessageType.DELETE_COLUMN, message);
  }

  public void updateColumnType(String columnId, ColumnType columnType) {
    ColumnType oldColumnType = data.getColumns().get(columnId).getColumnType();
    data.changeColumn(columnId, columnType);
    if (oldColumnType != columnType) {
      // If changing from NUMBER to TEXT, then convert all row values for that to Strings
      if (oldColumnType == ColumnType.NUMBER) {
        for (String key : data.getRows().keySet()) {
          Row row = data.getRows().get(key);
          String newValue = String.valueOf(((Float)row.getRowData().get(columnId)).floatValue());
          row.putRowData(columnId, newValue);
          String message = new StringJoiner(",")
              .add(row.getRowId())
              .add(columnId)
              .add(newValue)
              .toString();
          addMessage(MessageType.CHANGE_ROW, message);
        }
      }

      // If changing from TEXT to NUMBER, then 0 out all the row values.
      if (oldColumnType == ColumnType.TEXT) {
        for (String key : data.getRows().keySet()) {
          Row row = data.getRows().get(key);
          row.putRowData(columnId, new Float(0));
          String message = new StringJoiner(",")
              .add(row.getRowId())
              .add(columnId)
              .add("0")
              .toString();
          addMessage(MessageType.CHANGE_ROW, message);
        }
      }
    }
    String message = new StringJoiner(",")
        .add(columnId)
        .add(columnType.name())
        .toString();
    addMessage(MessageType.CHANGE_COLUMN, message);
  }

  public void createRow(String rowId) {
    data.createRow(rowId);
    String message = new StringJoiner(",")
        .add(rowId)
        .toString();
    addMessage(MessageType.ADD_ROW, message);
  }

  public void destroyRow(String rowId) {
    data.removeRow(rowId);
    String message = new StringJoiner(",")
        .add(rowId)
        .toString();
    addMessage(MessageType.DELETE_ROW, message);
  }

  public void moveRow(String rowId, int targetIndex) {
    if (targetIndex > data.getRowIndex().size()) {
      // Will only change targetIndex if it already exists in the row indices.
      LOG.log(Level.WARNING, "targetIndex for move row was greater than the list of rows we have. Ignoring.");
      return;
    }

    data.moveRow(rowId, targetIndex);
    String message = new StringJoiner(",")
        .add(rowId)
        .add(String.valueOf(targetIndex))
        .toString();
    addMessage(MessageType.MOVE_ROW, message);
  }

  public void updateTextCellValue(String rowId, String columnId, String cellValue) {
    if (data.getColumns().get(columnId) == null || data.getRows().get(rowId) == null) {
      LOG.log(Level.WARNING, "RowId or columnId passed in does not exist. Doing nothing");
      return;
    }

    ColumnType columnType = data.getColumns().get(columnId).getColumnType();
    if (columnType == ColumnType.NUMBER) {
      LOG.log(Level.WARNING, "Current column type is number, but user is changing value to string. Doing nothing");
      return;
    }

    data.changeRowTextValue(rowId, columnId, cellValue);
    String message = new StringJoiner(",")
        .add(rowId)
        .add(columnId)
        .add(cellValue)
        .toString();
    addMessage(MessageType.CHANGE_ROW, message);
  }

  public void updateNumberCellValue(String rowId, String columnId, float cellValue) {
    if (data.getColumns().get(columnId) == null || data.getRows().get(rowId) == null) {
      LOG.log(Level.WARNING, "RowId or columnId passed in does not exist. Doing nothing");
      return;
    }

    ColumnType columnType = data.getColumns().get(columnId).getColumnType();
    if (columnType == ColumnType.TEXT) {
      LOG.log(Level.WARNING, "Current column type is string, but user is changing value to number. Doing nothing");
      return;
    }

    data.changeRowNumberValue(rowId, columnId, cellValue);
    String message = new StringJoiner(",")
        .add(rowId)
        .add(columnId)
        .add(String.valueOf(cellValue))
        .toString();
    addMessage(MessageType.CHANGE_ROW, message);
  }

  //-------------------------------
  // Helper methods
  //-------------------------------
  public void addMessage(MessageType messageType, String message) {
    messages.addMessage(messageType, message);;
    // Inform the server that there are changes to be synced when online.
    if (isOnline()) {
      if (server.syncFromClient(messages)) {
        clearMessages();
      }
    }
  }

  public void syncFromServer(Messages messages) {
    data = new Data();
    SyncAndResolve.syncMessages(data, messages);
  }

  //-------------------------------
  // Getters and Setters
  //-------------------------------
  public boolean isOffline() {
    return offline;
  }

  public boolean isOnline() {
    return !offline;
  }

  public void goOffline() {
    offline = true;
  }

  public void comeOnline() {
    offline = false;
    // Once the client comes online, sync with the server
    if (server.syncFromClient(messages)) {
      clearMessages();
    }
  }

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  public void clearMessages() {
    this.messages = new Messages();
  }
}
