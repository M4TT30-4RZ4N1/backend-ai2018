package it.polito.ai.project.service.model.ClientInteraction;

import java.util.List;

public class SearchResult {
    public List<TimestampResult> byTimestamp;
    public List<PositionResult> byPosition;
    public List<UserResult> byUser;

    public SearchResult() {
    }

    public SearchResult(List<TimestampResult> byTimestamp, List<PositionResult> byPosition, List<UserResult> byUser) {
        this.byTimestamp = byTimestamp;
        this.byPosition = byPosition;
        this.byUser = byUser;
    }

    public List<TimestampResult> getByTimestamp() {
        return byTimestamp;
    }

    public void setByTimestamp(List<TimestampResult> byTimestamp) {
        this.byTimestamp = byTimestamp;
    }

    public List<PositionResult> getByPosition() {
        return byPosition;
    }

    public void setByPosition(List<PositionResult> byPosition) {
        this.byPosition = byPosition;
    }

    public List<UserResult> getByUser() {
        return byUser;
    }

    public void setByUser(List<UserResult> byUser) {
        this.byUser = byUser;
    }
}
