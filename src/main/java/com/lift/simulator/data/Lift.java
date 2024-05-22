package com.lift.simulator.data;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Lift {
    @Id
    @GeneratedValue
    private Long id;

    private int currentFloor = 0;
    private int targetFloor = 0;
    private int maxFloorNumber;
    private int minFloorNumber = 0;
    private LiftStatus status = LiftStatus.NOT_MOVING;

    public LiftStatus isMoving() { return status; }

    public void setMoving(LiftStatus moving) { status = moving; }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }

    public void setTargetFloor(int targetFloor) {
        this.targetFloor = targetFloor;
    }

    public void setMaxFloorNumber(int maxFloorNumber) {
        this.maxFloorNumber = maxFloorNumber;
    }

    public void setMinFloorNumber(int minFloorNumber) {
        this.minFloorNumber = minFloorNumber;
    }

    public int getMaxFloorNumber() {
        return maxFloorNumber;
    }

    public int getMinFloorNumber() {
        return minFloorNumber;
    }

    public Long getId() {
        return id;
    }

    public int getTargetFloor() {
        return targetFloor;
    }

    public int getCurrentFloor() {
        return currentFloor;
    }
}
