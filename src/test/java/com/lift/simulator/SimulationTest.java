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
    public void callLiftTest() {

        var lift1 = createLift(1l,2,12,0,5);
        var lift2 = createLift(2l,10,12,0,5);

        List<Lift> lifts = Arrays.asList(lift1, lift2);
        when(repository.findAll()).thenReturn(lifts);

        callLift(repository, 9, refreshable, ui);
        Assert.assertEquals(9, lifts.get(1).getTargetFloor());
        callLift(repository, 3, refreshable, ui);
        Assert.assertEquals(3, lifts.get(0).getTargetFloor());

    }

    @Test
    public void runningLiftTest() throws InterruptedException {
        var lift1 = createLift(1l,0,12,0,0);

        List<Lift> lifts = Arrays.asList(lift1);
        when(repository.findAll()).thenReturn(lifts);

        callLift(repository, 2, refreshable, ui);
        Thread.sleep(2 * 2000 + 1000);

        Assert.assertEquals(2, lifts.get(0).getCurrentFloor());
        Assert.assertEquals(LiftStatus.NOT_MOVING, lifts.get(0).isMoving());
    }

    private Lift createLift(Long id, int currentFloor, int maxNumberFloor, int minNumberFloor, int tergerFloor) {
        Lift lift = new Lift();
        lift.setId(id);
        lift.setCurrentFloor(currentFloor);
        lift.setMaxFloorNumber(maxNumberFloor);
        lift.setMinFloorNumber(minNumberFloor);
        lift.setTargetFloor(tergerFloor);
        lift.setMoving(LiftStatus.NOT_MOVING);
        return lift;
    }

}