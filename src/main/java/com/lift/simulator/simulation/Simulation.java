package com.lift.simulator.simulation;

import com.lift.simulator.data.Lift;
import com.lift.simulator.data.LiftRepository;
import com.lift.simulator.data.LiftStatus;
import com.lift.simulator.view.Refreshable;
import com.vaadin.flow.component.UI;

import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

public class Simulation {

    public static void callLift(LiftRepository repository, int floorNumber, Refreshable refreshable, UI ui){
        var closestLift = repository.findAll().stream()
                .filter(lift -> lift.isMoving() != LiftStatus.GOING_UP)
                .min(
                        Comparator.comparingInt((Lift lift) -> Math.abs(lift.getCurrentFloor() - floorNumber))
                )
                .orElseThrow(() -> new IllegalArgumentException("Lista jest pusta."));
        closestLift.setTargetFloor(floorNumber);
        closestLift.setMoving(getDirection(closestLift.getCurrentFloor(), floorNumber));
        repository.save(closestLift);
        liftMovingSimulator(repository, closestLift, floorNumber, refreshable, ui);
    }

    private static LiftStatus getDirection(int currentFloor, int floorNumber) {
        if(currentFloor - floorNumber < 0){
            return LiftStatus.GOING_UP;
        } else if (currentFloor - floorNumber > 0){
            return LiftStatus.GOING_DOWN;
        } else {
            return LiftStatus.OPENING;
        }
    }

    private static void liftMovingSimulator(LiftRepository repository, Lift lift, int newFloor, Refreshable refreshable, UI ui){
        int delay = Math.abs(lift.getCurrentFloor() - newFloor) * 2000;
        Timer simulationTimer = new Timer();
        simulationTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                lift.setCurrentFloor(newFloor);
                lift.setMoving(LiftStatus.NOT_MOVING);
                repository.save(lift);
                ui.access(() -> {
                    refreshable.refreshGrid();
                });
            }
        }, delay);
    }
}
