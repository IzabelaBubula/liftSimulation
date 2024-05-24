package com.lift.simulator.data;

import com.lift.simulator.view.Refreshable;
import com.vaadin.flow.component.UI;

public class LiftRequest {
    int floorNumber;
    int floorNumber2;
    Refreshable refreshable;
    UI ui;

    public int getFloorNumber() {
        return floorNumber;
    }

    public int getFloorNumber2() {
        return floorNumber2;
    }

    public Refreshable getRefreshable() {
        return refreshable;
    }

    public UI getUi() {
        return ui;
    }

    public LiftRequest(int floorNumber, int floorNumber2, Refreshable refreshable, UI ui) {
        this.floorNumber = floorNumber;
        this.floorNumber2 = floorNumber2;
        this.refreshable = refreshable;
        this.ui = ui;
    }
}
