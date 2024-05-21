package com.lift.simulator;

import java.util.Comparator;
import java.util.Scanner;

public class Simulation {

    //TO DO, check why changing only in the first lift

    public static LiftRepository callLift(LiftRepository repository, int floorNumber){
        var closestLift = repository.findAll().stream()
                .min(
                        Comparator.comparingLong((Lift lift) -> Math.abs(lift.getCurrentFloor() - floorNumber))
                                .thenComparingLong(Lift::getId)
                )
                .orElseThrow(() -> new IllegalArgumentException("Lista jest pusta."));
        closestLift.setTargetFloor(floorNumber);
        repository.save(closestLift);
        return repository;
    }
}
