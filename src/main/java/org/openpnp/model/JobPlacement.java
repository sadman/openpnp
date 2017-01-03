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
    
    public enum ErrorOption {
        Raise, // Error will be raised and user can choose to handle or skip
        Skip // Placement is skipped without interrupting job
    }

    final Job job;
    final BoardLocation boardLocation;
    final Board board;
    final Placement placement;
    
    @Attribute(required=false)
    Status status;
    
    @Attribute(required=false)
    boolean enabled = true;
    
    @Attribute(required=false)
    ErrorOption errorOption = ErrorOption.Raise;
    
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
    
    public ErrorOption getErrorOption() {
        return errorOption;
    }

    public void setErrorOption(ErrorOption errorOption) {
        this.errorOption = errorOption;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((board == null) ? 0 : board.hashCode());
        result = prime * result + ((boardLocation == null) ? 0 : boardLocation.hashCode());
        result = prime * result + ((job == null) ? 0 : job.hashCode());
        result = prime * result + ((placement == null) ? 0 : placement.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        JobPlacement other = (JobPlacement) obj;
        if (board == null) {
            if (other.board != null)
                return false;
        }
        else if (!board.equals(other.board))
            return false;
        if (boardLocation == null) {
            if (other.boardLocation != null)
                return false;
        }
        else if (!boardLocation.equals(other.boardLocation))
            return false;
        if (job == null) {
            if (other.job != null)
                return false;
        }
        else if (!job.equals(other.job))
            return false;
        if (placement == null) {
            if (other.placement != null)
                return false;
        }
        else if (!placement.equals(other.placement))
            return false;
        return true;
    }
}
