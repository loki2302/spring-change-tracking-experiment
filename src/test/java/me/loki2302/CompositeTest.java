package me.loki2302;

import me.loki2302.entities.ChangeLogTransaction;
import me.loki2302.entities.CreateEntityChangeLogEvent;
import me.loki2302.entities.UpdateEntityChangeLogEvent;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CompositeTest extends AbstractIntegrationTest {
    private NoteOperations noteOperations;
    private TransactionOperations transactionOperations;

    @Before
    public void createNoteOperations() {
        noteOperations = new NoteOperations(restTemplate);
        transactionOperations = new TransactionOperations(restTemplate);
    }

    @Test
    @DirtiesContext
    public void dummy() {
        assertTrue(transactionOperations.getAllTransactions().isEmpty());

        noteOperations.createNote("note1", "hello");

        List<ChangeLogTransaction> transactions = transactionOperations.getAllTransactions();
        assertEquals(1, transactions.size());
        assertTransactionHasSingleCreateEntityEventForNote(transactions.get(0), "note1", "hello");

        noteOperations.createNote("note2", "bye");

        transactions = transactionOperations.getAllTransactions();
        assertEquals(2, transactions.size());
        assertTransactionHasSingleCreateEntityEventForNote(transactions.get(1), "note2", "bye");

        noteOperations.updateNote("note1", "omg");

        transactions = transactionOperations.getAllTransactions();
        assertEquals(3, transactions.size());
        assertTransactionHasSingleUpdateEntityEventForNote(transactions.get(2), "note1", "hello", "omg");


        // TODO: delete note #2
    }

    private static void assertTransactionHasSingleCreateEntityEventForNote(
            ChangeLogTransaction transaction,
            String id,
            String text) {

        assertEquals(1, transaction.events.size());
        assertTrue(transaction.events.get(0) instanceof CreateEntityChangeLogEvent);

        CreateEntityChangeLogEvent changeLogEvent = (CreateEntityChangeLogEvent)transaction.events.get(0);
        assertEquals("me.loki2302.entities.Note", changeLogEvent.entityName);
        assertEquals(id, changeLogEvent.entityId);
        assertEquals("text", changeLogEvent.properties.get(0).name);
        assertEquals(text, changeLogEvent.properties.get(0).value);
    }

    private static void assertTransactionHasSingleUpdateEntityEventForNote(
            ChangeLogTransaction transaction,
            String id,
            String oldText,
            String newText) {

        assertEquals(1, transaction.events.size());
        assertTrue(transaction.events.get(0) instanceof UpdateEntityChangeLogEvent);

        UpdateEntityChangeLogEvent changeLogEvent = (UpdateEntityChangeLogEvent)transaction.events.get(0);
        assertEquals("me.loki2302.entities.Note", changeLogEvent.entityName);
        assertEquals(id, changeLogEvent.entityId);
        assertEquals("text", changeLogEvent.properties.get(0).name);
        assertEquals(newText, changeLogEvent.properties.get(0).value);
        assertEquals("text", changeLogEvent.oldProperties.get(0).name);
        assertEquals(oldText, changeLogEvent.oldProperties.get(0).value);
    }
}
