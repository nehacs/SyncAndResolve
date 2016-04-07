package airtable;

public enum MessageType {
  // TODO: Handle offline, online messages
  OFFLINE, // Inform the server that client is going offline
  ONLINE, // Inform the server that client is coming online
  ADD_ROW, // Inform the server of a row that was added
  DELETE_ROW, // Inform the server of a row that was deleted
  MOVE_ROW, // Inform the server of a row that was moved
  CHANGE_ROW, // Inform the server of a row that was changed
  ADD_COLUMN, // Inform the server of a column that was added
  DELETE_COLUMN, // Inform the server of a column that was deleted
  CHANGE_COLUMN, // Inform the server of a column that was changed
  ;
}
