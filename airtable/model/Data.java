package airtable.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import airtable.model.Column.ColumnType;

public class Data {

  private HashMap<String, Column> columns;
  private HashMap<String, Row> rows;
  private List<String> rowIndex;

  // Default empty constructor
  public Data() {
    this.columns = new HashMap<String, Column>();
    this.rows = new HashMap<String, Row>();
    this.rowIndex = new ArrayList<String>();
  }

  // Copy constructor
  public Data(Data data) {
    this.columns = new HashMap<String, Column>();
    this.rows = new HashMap<String, Row>();
    this.rowIndex = new ArrayList<String>();
    for (Map.Entry<String, Column> entry : data.columns.entrySet()) {
      String key = new String(entry.getKey());
      Column value = new Column(entry.getValue());
      columns.put(key, value);
    }

    for (String key : data.rowIndex) {
      Row value = new Row(data.rows.get(key));
      rows.put(key, value);
      rowIndex.add(key);
    }
  }

  //-------------------------------
  // Helper methods
  //-------------------------------
  public void createRow(String rowId) {
    Row row = new Row();
    row.setRowId(rowId);
    for (Column column : columns.values()) {
      switch (column.getColumnType()) {
      case NUMBER:
        row.putRowData(column.getColumnId(), new Float(0));
        break;
      case TEXT:
        row.putRowData(column.getColumnId(), new String());
        break;
      }
    }

    rows.put(rowId, row);
    rowIndex.add(rowId);
  }

  public void removeRow(String rowId) {
    rows.remove(rowId);
    rowIndex.remove(rowId);
  }

  public void changeRowTextValue(String rowId, String columnId, String value) {
    getRows().get(rowId).getRowData().put(columnId, value);
  }

  public void changeRowNumberValue(String rowId, String columnId, Float value) {
    getRows().get(rowId).getRowData().put(columnId, value);
  }

  public void moveRow(String rowId, int targetIndex) {
    int indexOfRowToBeMoved = rowIndex.indexOf(rowId);
    // Move all elems in between -- downwards
    String nextRowIdToBeSet = rowId;
    if (indexOfRowToBeMoved > targetIndex) {
      for (int i=targetIndex; i<=indexOfRowToBeMoved; i++) {
        // List.set(index, elem) returns the element that was replaced with the element we just set.
        // Use that to set the next element down the line.
        nextRowIdToBeSet = rowIndex.set(i, nextRowIdToBeSet);
      }
    }

    // Move all elems in between -- upwards
    if (indexOfRowToBeMoved < targetIndex) {
      for (int i=targetIndex; i>=indexOfRowToBeMoved; i--) {
        // List.set(index, elem) returns the element that was replaced with the element we just set.
        // Use that to set the next element down the line.
        nextRowIdToBeSet = rowIndex.set(i, nextRowIdToBeSet);
      }
    }

  }

  public void createColumn(String columnId, ColumnType columnType) {
    Column column = new Column();
    column.setColumnId(columnId);
    column.setColumnType(columnType);
    columns.put(columnId, column);
  }

  public void removeColumn(String columnId) {
    columns.remove(columnId);
  }

  public void changeColumn(String columnId, ColumnType columnType) {
    columns.get(columnId).setColumnType(columnType);
  }

  //-------------------------------
  // Getters and Setters
  //-------------------------------
  public HashMap<String, Column> getColumns() {
    return columns;
  }

  public HashMap<String, Row> getRows() {
    return rows;
  }

  public List<String> getRowIndex() {
    return rowIndex;
  }

  public int indexOf(String rowId) {
    return rowIndex.indexOf(rowId);
  }

  //-------------------------------
  // Comparison methods for tests
  //-------------------------------
  public boolean equals(List<String> otherIndex) {
    if (otherIndex.size() != rowIndex.size()) {
      return false;
    }
    for (int i=0; i<otherIndex.size(); i++){
      if (!rowIndex.get(i).equals(otherIndex.get(i))) {
        return false;
      }
    }
    return true;
  }
}
