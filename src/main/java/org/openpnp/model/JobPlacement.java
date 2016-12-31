package org.openpnp.model;

import org.simpleframework.xml.Attribute;

public class JobPlacement extends AbstractModelObject {
    public enum Status {
        Disabled, // Disabled for job run, will not be processed.
        Pending, // Enabled, not yet processed.
        Processing, // Currently being worked on.
        Error, // Processing failed, error is retained.
        Done // Finished without errors.
    }

    final Job job;
    final BoardLocation boardLocation;
    final Board board;
    final Placement placement;
    
    @Attribute(required=false)
    Status status;
    
    @Attribute(required=false)
    boolean enabled;
    
    public JobPlacement(Job job, BoardLocation boardLocation, Board board, Placement placement) {
        this.job = job;
        this.boardLocation = boardLocation;
        this.board = board;
        this.placement = placement;
    }
    
    public Status getStatus() {
        return status;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getOrdinal() {
        return job.getJobPlacements().indexOf(this);
    }

    public void setOrdinal(int ordinal) {
        job.setOrdinal(this, ordinal);
    }

    public Job getJob() {
        return job;
    }

    public BoardLocation getBoardLocation() {
        return boardLocation;
    }

    public Board getBoard() {
        return board;
    }

    public Placement getPlacement() {
        return placement;
    }
}
