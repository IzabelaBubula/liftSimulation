package com.lift.simulator.view;

import com.lift.simulator.data.Lift;
import com.lift.simulator.data.LiftRepository;
import com.lift.simulator.simulation.Simulation;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Route("")
@CssImport("./styles/styles.css")
public class MainView extends VerticalLayout implements Refreshable {
    private LiftRepository liftRepository;

    private TextField numberOfLifts = new TextField("Choose number of lifts");
    private TextField maxFloorNumber = new TextField("Choose max floor");
    private Binder<Lift> binder = new Binder<>(Lift.class);
    private Grid<Lift> grid = new Grid<>(Lift.class);
    private ComboBox<Lift> lifts = new ComboBox<>("Lifts");
    private ComboBox<Integer> callComboBox = new ComboBox<>("Choose call floor");
    private Button callButton = new Button("Call");
    private Button refreshButton = new Button("Refresh");

    public MainView(LiftRepository liftRepository) {
        this.liftRepository = liftRepository;

        grid.setColumns("id", "currentFloor", "targetFloor");
        GridContextMenu<Lift> menu = grid.addContextMenu();
        menu.addItem("Refresh", event -> {
            refreshGrid();
        });
        add(getForm(), grid);
        add(callForm());

        refreshGrid();
    }

    private Component callForm() {
        var layout = new HorizontalLayout();
        layout.setAlignItems(Alignment.BASELINE);
        callButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        layout.add(callComboBox, callButton);

        callButton.addClickListener(click -> {
            Simulation.callLift(liftRepository, callComboBox.getValue(), this, UI.getCurrent());
            refreshGrid();
        });

        return layout;
    }

    private Component getForm() {
        var layout = new HorizontalLayout();
        layout.setAlignItems(Alignment.BASELINE);
        var addButton = new Button("Add");
        addButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        layout.add(numberOfLifts, maxFloorNumber, addButton);

        binder.bindInstanceFields(this);

        addButton.addClickListener(click -> {
            var number = Integer.valueOf(numberOfLifts.getValue());
            try {
                for (int i = 0; i < number; i++) {
                    var lift = new Lift();
                    binder.writeBean(lift);
                    liftRepository.save(lift);
                }
                lifts.setItems(liftRepository.findAll());
                lifts.setItemLabelGenerator(lift -> String.valueOf(lift.getId()));
                callComboBox.setItems(getFloors());
                callComboBox.setItemLabelGenerator(i -> String.valueOf(i.intValue()));
                refreshGrid();
            } catch (ValidationException e) {
                //
            }
        });

        return layout;
    }

    public List<Integer> getFloors() {
        var lifts = liftRepository.findAll();
        var max = lifts.stream()
                .mapToInt(Lift::getMaxFloorNumber)
                .max()
                .orElse(0);
        var min = lifts.stream()
                .mapToInt(Lift::getMinFloorNumber)
                .min()
                .orElse(0);

        return IntStream.rangeClosed(min, max)
                .boxed()
                .collect(Collectors.toList());

    }

    @Override
    public void refreshGrid() {
        UI ui = UI.getCurrent();
        if (ui != null) {
            ui.access(() -> {
                grid.setItems(liftRepository.findAll());
            });
        }
    }
}
