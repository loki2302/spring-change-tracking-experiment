package me.loki2302.dto;

import me.loki2302.changelog.ChangeLogEvent;

import java.util.List;

public class ChangeLogTransactionDto {
    public long id;
    public List<ChangeLogEvent> events;
}
