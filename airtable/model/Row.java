package airtable.model;

import java.util.HashMap;
import java.util.Map;

public class Row {

  private String rowId;
  private HashMap<String, Object> rowData;

  // Default empty constructor
  public Row() {
    this.rowData = new HashMap<String, Object>();
  }

  // Copy constructor
  public Row(Row row) {
    this.rowId = new String(row.rowId);
    this.rowData = new HashMap<String, Object>();
    for (Map.Entry<String, Object> entry : row.rowData.entrySet()) {
      String key = new String(entry.getKey());
      if (entry.getValue() instanceof Float) {
        Float value = new Float((Float)entry.getValue());
        rowData.put(key, value);
      }
      if (entry.getValue() instanceof String) {
        String value = new String((String)entry.getValue());
        rowData.put(key, value);
      }
    }
  }

  //-------------------------------
  // Helper methods
  //-------------------------------
  public void putRowData(String columnId, Object value) {
    rowData.put(columnId, value);
  }
  //-------------------------------
  // Getters and Setters
  //-------------------------------
  public String getRowId() {
    return rowId;
  }

  public void setRowId(String rowId) {
    this.rowId = rowId;
  }

  public HashMap<String, Object> getRowData() {
    return rowData;
  }
}
