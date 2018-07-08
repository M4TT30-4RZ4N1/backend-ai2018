package it.polito.ai.project.service.model.ClientInteraction;

import javafx.geometry.Pos;

import java.util.ArrayList;
import java.util.List;
/**
 * This class is related to SearchResult and the available operations.
 */
public class SearchResult {
    public List<TimestampResult> byTimestamp;
    public List<PositionResult> byPosition;
    public List<UserResult> byUser;

    public SearchResult() {
        this.byPosition= new ArrayList<>();
        this.byTimestamp= new ArrayList<>();
        this.byUser= new ArrayList<>();
    }
    /**
     * This method allows to set the SearchResult info.
     * @param byTimestamp
     * @param byPosition
     * @param byUser
     */
    public SearchResult(List<TimestampResult> byTimestamp, List<PositionResult> byPosition, List<UserResult> byUser) {
        this.byTimestamp = byTimestamp;
        this.byPosition = byPosition;
        this.byUser = byUser;
    }
    /**
     * This method allows to retrieve the list of TimestampResult based on Timestamp.
     * @return list of TimestampResult
     */
    public List<TimestampResult> getByTimestamp() {
        return byTimestamp;
    }

    public void setByTimestamp(List<TimestampResult> byTimestamp) {
        this.byTimestamp = byTimestamp;
    }
    /**
     * This method allows to retrieve the list of PositionResult based on Position.
     * @return list of PositionResult
     */
    public List<PositionResult> getByPosition() {
        return byPosition;
    }

    public void setByPosition(List<PositionResult> byPosition) {
        this.byPosition = byPosition;
    }
    /**
     * This method allows to retrieve the list of UserResult based on User.
     * @return list of UserResult
     */
    public List<UserResult> getByUser() {
        return byUser;
    }

    public void setByUser(List<UserResult> byUser) {
        this.byUser = byUser;
    }
}
