package me.loki2302.client;

import me.loki2302.changelog.CreateEntityChangeLogEvent;
import me.loki2302.changelog.DeleteEntityChangeLogEvent;
import me.loki2302.changelog.UpdateEntityChangeLogEvent;

public class NoteEntityHandler implements EntityHandler {
    @Override
    public void handleCreateEntity(NoteDataContext dataContext, CreateEntityChangeLogEvent event) {
        LocalNote note = new LocalNote();
        note.id = event.entityId;
        note.text = (String)event.properties.get("text");
        note.text2 = (String)event.properties.get("text2");
        dataContext.noteRepository.save(note);
    }

    @Override
    public void handleUpdateEntity(NoteDataContext dataContext, UpdateEntityChangeLogEvent event) {
        LocalNote note = new LocalNote();
        note.id = event.entityId;
        note.text = (String)event.properties.get("text");
        note.text2 = (String)event.properties.get("text2");
        dataContext.noteRepository.save(note);
    }

    @Override
    public void handleDeleteEntity(NoteDataContext dataContext, DeleteEntityChangeLogEvent event) {
        dataContext.noteRepository.delete(event.entityId);
    }
}
