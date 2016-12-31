/*
 * Copyright (C) 2011 Jason von Nieda <jason@vonnieda.org>
 * 
 * This file is part of OpenPnP.
 * 
 * OpenPnP is free software: you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * OpenPnP is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with OpenPnP. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 * For more information about OpenPnP visit http://openpnp.org
 */

package org.openpnp.gui.tablemodel;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.openpnp.gui.support.LengthCellValue;
import org.openpnp.gui.support.PartCellValue;
import org.openpnp.gui.support.RotationCellValue;
import org.openpnp.model.Board.Side;
import org.openpnp.model.BoardLocation;
import org.openpnp.model.Configuration;
import org.openpnp.model.Job;
import org.openpnp.model.JobPlacement;
import org.openpnp.model.Length;
import org.openpnp.model.Location;
import org.openpnp.model.Part;
import org.openpnp.model.Placement;
import org.openpnp.model.Placement.Type;
import org.openpnp.spi.Feeder;

public class PlacementsTableModel extends AbstractTableModel {
    final Configuration configuration;

    private String[] columnNames = new String[] {
        "#",            // 0 
        "Board",        // 1
        "Id",           // 2
        "Part",         // 3
        "Side",         // 4
        "X",            // 5
        "Y",            // 6
        "ø",            // 7
        "Type",         // 8
        "Enabled",      // 9
        "Status",       // 10
        "Glue",         // 11
        "Check Fids"    // 12
    };

    private Class[] columnTypes = new Class[] {
        Integer.class, 
        String.class, 
        PartCellValue.class, 
        Part.class, 
        Side.class,
        LengthCellValue.class, 
        LengthCellValue.class, 
        RotationCellValue.class, 
        Type.class,
        Boolean.class,
        Status.class, 
        Boolean.class, 
        Boolean.class
    };

    public enum Status {
        Ready,
        MissingPart,
        MissingFeeder,
        ZeroPartHeight
    }

    private Job job;
    private BoardLocation boardLocation;
    private List<JobPlacement> jobPlacements = new ArrayList<>();

    public PlacementsTableModel(Configuration configuration) {
        this.configuration = configuration;
    }

    public void setJob(Job job) {
        this.job = job;
        updateCache();
    }
    
    public void setBoardLocation(BoardLocation boardLocation) {
        this.boardLocation = boardLocation;
        updateCache();
    }
    
    private void updateCache() {
        jobPlacements.clear();
        if (job != null) {
            for (JobPlacement jobPlacement : job.getJobPlacements()) {
                if (boardLocation == null || boardLocation == jobPlacement.getBoardLocation()) {
                    System.out.println("add " + jobPlacement.getOrdinal());
                    jobPlacements.add(jobPlacement);
                }
            }
        }
        fireTableDataChanged();
    }

    public JobPlacement getJobPlacement(int index) {
        return jobPlacements.get(index);
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return (job == null) ? 0 : jobPlacements.size();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 1 && columnIndex != 2 && columnIndex != 10; 
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnTypes[columnIndex];
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        try {
            JobPlacement jobPlacement = getJobPlacement(rowIndex);
            Placement placement = jobPlacement.getPlacement();
            if (columnIndex == 0) {
                jobPlacement.setOrdinal((int) aValue);
            }
            else if (columnIndex == 3) {
                placement.setPart((Part) aValue);
            }
            else if (columnIndex == 4) {
                placement.setSide((Side) aValue);
            }
            else if (columnIndex == 5) {
                LengthCellValue value = (LengthCellValue) aValue;
                value.setDisplayNativeUnits(true);
                Length length = value.getLength();
                Location location = placement.getLocation();
                location = Length.setLocationField(configuration, location, length, Length.Field.X,
                        true);
                placement.setLocation(location);
            }
            else if (columnIndex == 6) {
                LengthCellValue value = (LengthCellValue) aValue;
                value.setDisplayNativeUnits(true);
                Length length = value.getLength();
                Location location = placement.getLocation();
                location = Length.setLocationField(configuration, location, length, Length.Field.Y,
                        true);
                placement.setLocation(location);
            }
            else if (columnIndex == 7) {
                placement.setLocation(placement.getLocation().derive(null, null, null,
                        Double.parseDouble(aValue.toString())));
            }
            else if (columnIndex == 8) {
                placement.setType((Type) aValue);
            }
            else if (columnIndex == 9) {
                jobPlacement.setEnabled((Boolean) aValue);
            }
            else if (columnIndex == 11) {
                placement.setGlue((Boolean) aValue);
            }
            else if (columnIndex == 12) {
                placement.setCheckFids((Boolean) aValue);
            }
        }
        catch (Exception e) {
            // TODO: dialog, bad input
        }
    }

    // TODO: Ideally this would all come from the JobPlanner, but this is a
    // good start for now.
    private Status getPlacementStatus(Placement placement) {
        if (placement.getPart() == null) {
            return Status.MissingPart;
        }
        if (placement.getType() == Placement.Type.Place) {
            boolean found = false;
            for (Feeder feeder : Configuration.get().getMachine().getFeeders()) {
                if (feeder.getPart() == placement.getPart() && feeder.isEnabled()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return Status.MissingFeeder;
            }

            if (placement.getPart().getHeight().getValue() == 0) {
                return Status.ZeroPartHeight;
            }
        }
        return Status.Ready;
    }

    public Object getValueAt(int row, int col) {
        JobPlacement jobPlacement = getJobPlacement(row);
        Placement placement = jobPlacement.getPlacement();
        Location loc = placement.getLocation();
        switch (col) {
            case 0:
                return jobPlacement.getOrdinal();
            case 1:
                return jobPlacement.getBoard().getName();
            case 2:
                return new PartCellValue(placement.getId());
            case 3:
                return placement.getPart();
            case 4:
                return placement.getSide();
            case 5:
                return new LengthCellValue(loc.getLengthX(), true);
            case 6:
                return new LengthCellValue(loc.getLengthY(), true);
            case 7:
                return new RotationCellValue(loc.getRotation(), true);
            case 8:
                return placement.getType();
            case 9:
                return jobPlacement.isEnabled();
            case 10:
                return getPlacementStatus(placement);
            case 11:
                return placement.getGlue();
            case 12:
                return placement.getCheckFids();
            default:
                return null;
        }
    }
}
