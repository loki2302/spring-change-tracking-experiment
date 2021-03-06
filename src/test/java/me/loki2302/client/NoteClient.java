package me.loki2302.client;

import me.loki2302.client.api.NoteOperations;
import me.loki2302.client.api.TransactionOperations;
import me.loki2302.changelog.ChangeLogEvent;
import me.loki2302.client.commands.ApiCommand;
import me.loki2302.client.commands.DeleteNoteCommand;
import me.loki2302.client.commands.SaveNoteCommand;
import me.loki2302.dto.ChangeLogTransactionDto;

import java.util.*;

public class NoteClient {
    private final Queue<ApiCommand> commandQueue = new LinkedList<ApiCommand>();
    private final NoteDataContext noteDataContext;
    private final EntityHandlerRegistry entityHandlerRegistry;
    private final NoteOperations noteOperations;
    private final TransactionOperations transactionOperations;

    public NoteClient(
            NoteDataContext noteDataContext,
            EntityHandlerRegistry entityHandlerRegistry,
            NoteOperations noteOperations,
            TransactionOperations transactionOperations) {

        this.noteDataContext = noteDataContext;
        this.entityHandlerRegistry = entityHandlerRegistry;
        this.noteOperations = noteOperations;
        this.transactionOperations = transactionOperations;
    }

    public void sendChanges() {
        while(!commandQueue.isEmpty()) {
            ApiCommand<?> command = commandQueue.remove();
            command.applyRemotely(noteOperations);
        }
    }

    public void retrieveChanges() {
        Long currentRevision = noteDataContext.revision;

        List<ChangeLogTransactionDto> transactions;
        if(currentRevision == null) {
            transactions = transactionOperations.getAllTransactions();
        } else {
            transactions = transactionOperations.getTransactionsAfter(currentRevision);
        }

        for (ChangeLogTransactionDto transaction : transactions) {
            for (ChangeLogEvent event : transaction.events) {
                entityHandlerRegistry.handleChangeLogEvent(noteDataContext, event);
            }

            noteDataContext.revision = transaction.id;
        }
    }

    public Long getCurrentRevision() {
        return noteDataContext.revision;
    }

    public LocalNote saveNote(String id, String text, String text2) {
        SaveNoteCommand command = new SaveNoteCommand();
        command.id = id;
        command.text = text;
        command.text2 = text2;
        commandQueue.add(command);
        return command.applyLocally(noteDataContext);
    }

    public void deleteNote(String id) {
        DeleteNoteCommand command = new DeleteNoteCommand();
        command.id = id;
        commandQueue.add(command);
        command.applyLocally(noteDataContext);
    }

    public List<LocalNote> getAllNotes() {
        return noteDataContext.noteRepository.findAll();
    }

    public LocalNote getNote(String id) {
        LocalNote note = noteDataContext.noteRepository.findOne(id);
        if(note == null) {
            throw new RuntimeException("LocalNote doesn't exist");
        }

        return note;
    }
}
