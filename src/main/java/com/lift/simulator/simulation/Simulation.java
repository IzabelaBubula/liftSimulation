package com.lift.simulator.simulation;

import com.lift.simulator.data.Lift;
import com.lift.simulator.data.LiftRepository;
import com.lift.simulator.data.LiftRequest;
import com.lift.simulator.data.LiftStatus;
import com.lift.simulator.view.Refreshable;
import com.vaadin.flow.component.UI;

import java.util.*;

public class Simulation {

    private static final Queue<LiftRequest> requestQueue = new LinkedList<>();

    public static void callLift(LiftRepository repository, int floorNumber, int floorNumber2, Refreshable refreshable, UI ui) {
        Lift closestLift = findClosestLift(repository, floorNumber);
        if (closestLift != null) {
            processLiftRequest(repository, closestLift, floorNumber, floorNumber2, refreshable, ui);
        } else {
            requestQueue.offer(new LiftRequest(floorNumber, floorNumber2, refreshable, ui));
            retryRequestAfterDelay(repository, 4000);
        }
    }

    private static Lift findClosestLift(LiftRepository repository, int floorNumber) {
        return repository.findAll().stream()
                .filter(lift -> lift.getStatus() != LiftStatus.GOING_UP && lift.getStatus() != LiftStatus.CONTINUING_DOWN && lift.getStatus() != LiftStatus.BROKEN)
                .min(Comparator.comparingInt(lift -> Math.abs(lift.getCurrentFloor() - floorNumber)))
                .orElse(null);
    }

    private static void processLiftRequest(LiftRepository repository, Lift lift, int floorNumber, int floorNumber2, Refreshable refreshable, UI ui) {
        lift.setTargetFloor(floorNumber);
        lift.setStatus(getDirection(lift.getCurrentFloor(), floorNumber));
        repository.save(lift);

        if (floorNumber2 == 0) {
            moveLiftToFloor(repository, lift, floorNumber, () -> {
                lift.setStatus(LiftStatus.NOT_MOVING);
                repository.save(lift);
                ui.access(refreshable::refreshGrid);
            }, refreshable, ui);
        } else {
            liftMovingSimulator(repository, lift, floorNumber, floorNumber2, refreshable, ui);
        }
    }

    private static LiftStatus getDirection(int currentFloor, int targetFloor) {
        return currentFloor < targetFloor ? LiftStatus.GOING_UP : LiftStatus.GOING_DOWN;
    }

    private static LiftStatus getContinuingDirection(int currentFloor, int targetFloor) {
        if (currentFloor < targetFloor) {
            return LiftStatus.GOING_UP;
        } else if (currentFloor > targetFloor) {
            return LiftStatus.GOING_DOWN;
        } else {
            return LiftStatus.WAITING;
        }
    }

    private static void liftMovingSimulator(LiftRepository repository, Lift lift, int firstFloor, int secondFloor, Refreshable refreshable, UI ui) {
        moveLiftToFloor(repository, lift, firstFloor, () -> {
            moveLiftToFloor(repository, lift, secondFloor, () -> {
                lift.setStatus(LiftStatus.NOT_MOVING);
                lift.setTargetFloor(secondFloor);
                repository.save(lift);
                ui.access(refreshable::refreshGrid);
            }, refreshable, ui);
        }, refreshable, ui);
    }

    private static void moveLiftToFloor(LiftRepository repository, Lift lift, int targetFloor, Runnable onFinish, Refreshable refreshable, UI ui) {
        int currentFloor = lift.getCurrentFloor();
        int step = (targetFloor > currentFloor) ? 1 : -1;

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                int nextFloor = currentFloor + step;
                lift.setCurrentFloor(nextFloor);
                lift.setStatus(getContinuingDirection(nextFloor, targetFloor));
                repository.save(lift);
                ui.access(refreshable::refreshGrid);

                if (nextFloor == targetFloor) {
                    onFinish.run();
                } else {
                    moveLiftToFloor(repository, lift, targetFloor, onFinish, refreshable, ui);
                }
            }
        }, 2000);
    }

    private static void retryRequestAfterDelay(LiftRepository repository, long delay) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                LiftRequest request = requestQueue.poll();
                if (request != null) {
                    callLift(repository, request.getFloorNumber(), request.getFloorNumber2(), request.getRefreshable(), request.getUi());
                }
            }
        }, delay);
    }
}
