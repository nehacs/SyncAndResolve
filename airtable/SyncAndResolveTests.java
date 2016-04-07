package airtable;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import airtable.model.Column.ColumnType;

public class SyncAndResolveTests {

  @Test
  public void testModifySameRow() {
    Server server = new Server();
    Client client1 = new Client(server);
    Client client2 = new Client(server);

    // Add some data
    client1.createRow("ABC");
    client1.createColumn("123", ColumnType.TEXT);
    client1.updateTextCellValue("ABC", "123", "foo");

    // Both clients go offline then make some changes
    client1.goOffline();
    client2.goOffline();
    client1.updateTextCellValue("ABC", "123", "bar");
    client2.updateTextCellValue("ABC", "123", "baz");

    // After both clients come online, the server + all clients should be in sync
    client1.comeOnline();
    client2.comeOnline();

    // Assert that all 3 have the same value for rowId ABC, columnId 123
    assertEquals("baz", server.getData().getRows().get("ABC").getRowData().get("123"));
    assertEquals("baz", client1.getData().getRows().get("ABC").getRowData().get("123"));
    assertEquals("baz", client2.getData().getRows().get("ABC").getRowData().get("123"));
  }

  @Test
  public void testAddNewRows() {
    Server server = new Server();
    Client client1 = new Client(server);
    Client client2 = new Client(server);

    // Add some data
    client1.createRow("ABC");
    client1.createColumn("123", ColumnType.NUMBER);
    client1.updateNumberCellValue("ABC", "123", new Float(123.456));

    // Both clients go offline then make some changes
    client1.goOffline();
    client2.goOffline();
    client1.createRow("DEF");
    client2.createRow("GHI");

    // After both clients come online, the server + all clients should be in sync
    client1.comeOnline();
    client2.comeOnline();
    assertEquals(3, server.getData().getRows().size());
    assertEquals(3, client1.getData().getRows().size());
    assertEquals(3, client2.getData().getRows().size());
  }

  @Test
  public void testMovesRow() {
    Server server = new Server();
    Client client1 = new Client(server);
    Client client2 = new Client(server);

    // Add some data
    client1.createRow("A");
    client1.createRow("B");
    client1.createRow("C");
    client1.createColumn("123", ColumnType.NUMBER);
    client1.updateNumberCellValue("A", "123", new Float(123.456));

    client2.createRow("D");
    client2.createRow("E");
    client2.createRow("F");

    // Both clients go offline then make some changes
    client1.goOffline();
    client2.goOffline();
    client1.moveRow("A", 2);
    client2.moveRow("D", 0);

    // After both clients come online, the server + all clients should be in sync
    client1.comeOnline();
    client2.comeOnline();

    assertEquals(true, server.getData().equals(client1.getData().getRowIndex()));
    assertEquals(true, server.getData().equals(client2.getData().getRowIndex()));

    List<String> expectedIndex = new ArrayList<String>();
    expectedIndex.add("D");
    expectedIndex.add("B");
    expectedIndex.add("C");
    expectedIndex.add("A");
    expectedIndex.add("E");
    expectedIndex.add("F");
    assertEquals(true, server.getData().equals(expectedIndex));
  }
}
