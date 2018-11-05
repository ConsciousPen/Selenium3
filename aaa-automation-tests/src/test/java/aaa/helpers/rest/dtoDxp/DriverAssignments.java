package aaa.helpers.rest.dtoDxp;

import java.util.*;

public class DriverAssignments {
    protected List<DriverAssignment> driverVehicleAssignments;
    protected Set<String> assignableDrivers;
    protected Set<String> assignableVehicles;
    protected Set<String> unassignedDrivers;
    protected Set<String> unassignedVehicles;
    protected Set<String> vehiclesWithTooManyDrivers;
    protected boolean maxOneDriverPerVehicle;

    public DriverAssignments() {
        this.unassignedDrivers = new HashSet<>();
        this.unassignedVehicles = new HashSet<>();
    }

    public DriverAssignments addAssignment(String driverOid, String vehicleOid) {
        if(driverVehicleAssignments == null) {
            driverVehicleAssignments = new ArrayList<>();
        }
        DriverAssignment driverAssignment = new DriverAssignment();
        driverAssignment.vehicleOid = vehicleOid;
        driverAssignment.driverOid = driverOid;
        driverVehicleAssignments.add(driverAssignment);
        return this;
    }

    public DriverAssignments addUnassignedDrivers(String... unassignedDriver) {
        if(unassignedDrivers == null) {
            unassignedDrivers = new HashSet<>();
        }
        unassignedDrivers.addAll(Arrays.asList(unassignedDriver));
        return this;
    }

    public DriverAssignments addUnassignedVehicles(String... unassignedVehicle) {
        if(unassignedVehicles == null) {
            unassignedVehicles = new HashSet<>();
        }
        unassignedVehicles.addAll(Arrays.asList(unassignedVehicle));
        return this;
    }

    public List<DriverAssignment> getDriverVehicleAssignments() {
        return driverVehicleAssignments;
    }

    public void setDriverVehicleAssignments(List<DriverAssignment> driverVehicleAssignments) {
        this.driverVehicleAssignments = driverVehicleAssignments;
    }

    public Set<String> getAssignableDrivers() {
        return assignableDrivers;
    }

    public void setAssignableDrivers(Set<String> assignableDrivers) {
        this.assignableDrivers = assignableDrivers;
    }

    public Set<String> getAssignableVehicles() {
        return assignableVehicles;
    }

    public void setAssignableVehicles(Set<String> assignableVehicles) {
        this.assignableVehicles = assignableVehicles;
    }

    public Set<String> getUnassignedDrivers() {
        return unassignedDrivers;
    }

    public void setUnassignedDrivers(Set<String> unassignedDrivers) {
        this.unassignedDrivers = unassignedDrivers;
    }

    public Set<String> getUnassignedVehicles() {
        return unassignedVehicles;
    }

    public void setUnassignedVehicles(Set<String> unassignedVehicles) {
        this.unassignedVehicles = unassignedVehicles;
    }

    public Set<String> getVehiclesWithTooManyDrivers() {
        return vehiclesWithTooManyDrivers;
    }

    public void setVehiclesWithTooManyDrivers(Set<String> vehiclesWithTooManyDrivers) {
        this.vehiclesWithTooManyDrivers = vehiclesWithTooManyDrivers;
    }

    public boolean isMaxOneDriverPerVehicle() {
        return maxOneDriverPerVehicle;
    }

    public void setMaxOneDriverPerVehicle(boolean maxOneDriverPerVehicle) {
        this.maxOneDriverPerVehicle = maxOneDriverPerVehicle;
    }
}
