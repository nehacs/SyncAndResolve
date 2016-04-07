package airtable.model;

public class Column {

  public enum ColumnType {TEXT, NUMBER};

  private String columnId;
  private ColumnType columnType;

  // Default empty constructor
  public Column() {}

  // Copy constructor
  public Column(Column column) {
    this.columnId = new String(column.columnId);
    this.columnType = column.columnType;
  }

  //-------------------------------
  // Getters and Setters
  //-------------------------------
  public String getColumnId() {
    return columnId;
  }

  public void setColumnId(String columnId) {
    this.columnId = columnId;
  }

  public ColumnType getColumnType() {
    return columnType;
  }

  public void setColumnType(ColumnType columnType) {
    this.columnType = columnType;
  }
}
