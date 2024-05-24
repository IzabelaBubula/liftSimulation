package com.lift.simulator;

import com.lift.simulator.data.Lift;
import com.lift.simulator.data.LiftRepository;
import com.lift.simulator.data.LiftStatus;
import com.lift.simulator.view.Refreshable;
import com.vaadin.flow.component.UI;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.List;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.lift.simulator.simulation.Simulation.callLift;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimulationTest{

    @Mock
    private LiftRepository repository;

    @Mock
    private Refreshable refreshable;

    @Mock
    UI ui;

    @Test
    public void runningLiftTest() throws InterruptedException {
        var lift1 = createLift(1l,0,12,0,0);

        List<Lift> lifts = Arrays.asList(lift1);
        when(repository.findAll()).thenReturn(lifts);

        callLift(repository, 2,1, refreshable, ui);
        Thread.sleep(6 * 2000 + 2000);

        Assert.assertEquals(1, lifts.get(0).getCurrentFloor());
        Assert.assertEquals(LiftStatus.NOT_MOVING, lifts.get(0).getStatus());
    }

    @Test
    void busyLiftTest() throws InterruptedException {
        var lift1 = createLift(1l,0,12,0,0);
        var lift2 = createLift(1l,0,12,0,0);

        List<Lift> lifts = Arrays.asList(lift1, lift2);
        when(repository.findAll()).thenReturn(lifts);

        callLift(repository, 2,1, refreshable, ui);
        Thread.sleep(1000);
        callLift(repository, 2,1, refreshable, ui);
        callLift(repository, 2,3, refreshable, ui);
        Thread.sleep(14000);

        Assert.assertTrue(lifts.stream().anyMatch(lift -> lift.getCurrentFloor() == 3));

    }

    private Lift createLift(Long id, int currentFloor, int maxNumberFloor, int minNumberFloor, int tergerFloor) {
        Lift lift = new Lift();
        lift.setId(id);
        lift.setCurrentFloor(currentFloor);
        lift.setMaxFloorNumber(maxNumberFloor);
        lift.setMinFloorNumber(minNumberFloor);
        lift.setTargetFloor(tergerFloor);
        lift.setStatus(LiftStatus.NOT_MOVING);
        return lift;
    }

}