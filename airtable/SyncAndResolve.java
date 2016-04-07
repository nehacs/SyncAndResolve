package airtable;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import airtable.model.Column.ColumnType;
import airtable.model.Data;

public class SyncAndResolve {

  private static final Logger LOG = Logger.getLogger(SyncAndResolve.class.getName());

  public static boolean syncMessages(Data data, Messages messages) {
    for (Map.Entry<MessageType, List<String>> message : messages.getMessageMap().entrySet()) {
      switch (message.getKey()) {
      case ADD_ROW:
        for (String msg : message.getValue()) {
          String[] msgParts = msg.split(",");
          if (msgParts.length != 1) {
            LOG.log(Level.WARNING, "ADD_ROW message has incorrect number of args: " + msg);
          }

          data.createRow(new String(msgParts[0]));
        }
        break;
      case DELETE_ROW:
        for (String msg : message.getValue()) {
          String[] msgParts = msg.split(",");
          if (msgParts.length != 1) {
            LOG.log(Level.WARNING, "DELETE_ROW message has incorrect number of args: " + msg);
          }

          data.removeRow(msgParts[0]);
        }
        break;
      case CHANGE_ROW:
        for (String msg : message.getValue()) {
          String[] msgParts = msg.split(",");
          if (msgParts.length != 3) {
            LOG.log(Level.WARNING, "CHANGE_ROW message has incorrect number of args: " + msg);
          }

          String rowId = msgParts[0];
          String columnId = msgParts[1];
          ColumnType columnType = data.getColumns().get(columnId).getColumnType();
          if (columnType == ColumnType.TEXT) {
            data.changeRowTextValue(rowId, columnId, msgParts[2]);
          }

          if (columnType == ColumnType.NUMBER) {
            data.changeRowNumberValue(rowId, columnId, new Float(msgParts[2]));
          }
        }
        break;
      case MOVE_ROW:
        for (String msg : message.getValue()) {
          String[] msgParts = msg.split(",");
          if (msgParts.length != 2) {
            LOG.log(Level.WARNING, "MOVE_ROW message has incorrect number of args: " + msg);
          }

          data.moveRow(msgParts[0], new Integer(msgParts[1]));
        }
        break;
      case ADD_COLUMN:
        for (String msg : message.getValue()) {
          String[] msgParts = msg.split(",");
          if (msgParts.length != 2) {
            LOG.log(Level.WARNING, "ADD_COLUMN message has incorrect number of args: " + msg);
          }

          data.createColumn(msgParts[0], ColumnType.valueOf(msgParts[1]));
        }
        break;
      case DELETE_COLUMN:
        for (String msg : message.getValue()) {
          String[] msgParts = msg.split(",");
          if (msgParts.length != 1) {
            LOG.log(Level.WARNING, "ADD_COLUMN message has incorrect number of args: " + msg);
          }

          data.removeColumn(msgParts[0]);
        }
        break;
      case CHANGE_COLUMN:
        for (String msg : message.getValue()) {
          String[] msgParts = msg.split(",");
          if (msgParts.length != 2) {
            LOG.log(Level.WARNING, "ADD_COLUMN message has incorrect number of args: " + msg);
          }

          data.changeColumn(msgParts[0], ColumnType.valueOf(msgParts[1]));
        }
        break;
      case ONLINE:
        break;
      case OFFLINE:
        break;
      }
    }

    return true;
  }
}
