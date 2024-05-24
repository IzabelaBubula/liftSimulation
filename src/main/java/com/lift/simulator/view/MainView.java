package com.lift.simulator.view;

import com.lift.simulator.data.Lift;
import com.lift.simulator.data.LiftRepository;
import com.lift.simulator.data.LiftStatus;
import com.lift.simulator.simulation.Simulation;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private ComboBox<Integer> callComboBox = new ComboBox<>("Choose call from floor");
    private ComboBox<Integer> goToComboBox = new ComboBox<>("Choose go to floor");
    private ComboBox<Integer> goToOneComboBox = new ComboBox<>("Choose go to floor");
    private Button callButton = new Button("Call");
    private Button goToButton = new Button("Go to");


    public MainView(LiftRepository liftRepository) {
        this.liftRepository = liftRepository;

        grid.setColumns("id", "currentFloor", "targetFloor");
        grid.addColumn(lift -> lift.getStatus().toString()).setHeader("status");
        GridContextMenu<Lift> menu = grid.addContextMenu();

        menu.addItem("Refresh", event -> {
            refreshGrid();
        });
        menu.addItem("Edit", event -> {
            Optional<Lift> item = event.getItem();
            item.ifPresent(lift -> {
                openEditDialog(lift.getId());
            });
        });
        menu.addItem("Broke", event -> {
            Optional<Lift> item = event.getItem();
            item.ifPresent(lift -> {
                lift.setStatus(LiftStatus.BROKEN);
                liftRepository.save(lift);
                refreshGrid();
            });
        });
        menu.addItem("Repair", event -> {
            Optional<Lift> item = event.getItem();
            item.ifPresent(lift -> {
                lift.setStatus(LiftStatus.NOT_MOVING);
                liftRepository.save(lift);
                refreshGrid();
            });
        });

        add(getForm(), grid);
        add(callForm());
        add(goToForm());

        refreshGrid();
    }

    private Component goToForm() {
        var layout = new HorizontalLayout();
        layout.setAlignItems(Alignment.BASELINE);
        goToButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        layout.add(callComboBox, goToComboBox, goToButton);

        goToButton.addClickListener(click -> {
            Simulation.callLift(liftRepository, callComboBox.getValue(), goToComboBox.getValue(), this, UI.getCurrent());
            refreshGrid();
        });

        return layout;
    }

    private Component callForm() {
        var layout = new HorizontalLayout();
        layout.setAlignItems(Alignment.BASELINE);
        callButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
        layout.add(goToOneComboBox, callButton);

        callButton.addClickListener(click -> {
            Simulation.callLift(liftRepository, goToOneComboBox.getValue(), 0, this, UI.getCurrent());
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
                callComboBox.setItems(getFloors());
                callComboBox.setItemLabelGenerator(i -> String.valueOf(i.intValue()));
                goToComboBox.setItems(getFloors());
                goToComboBox.setItemLabelGenerator(i -> String.valueOf(i.intValue()));
                goToOneComboBox.setItems(getFloors());
                goToOneComboBox.setItemLabelGenerator(i -> String.valueOf(i.intValue()));
                refreshGrid();
            } catch (ValidationException e) {
                //
            }
        });

        return layout;
    }

    public void updateComboBoxes() {
        callComboBox.setItems(getFloors());
        callComboBox.setItemLabelGenerator(i -> String.valueOf(i.intValue()));
        goToComboBox.setItems(getFloors());
        goToComboBox.setItemLabelGenerator(i -> String.valueOf(i.intValue()));
        goToOneComboBox.setItems(getFloors());
        goToOneComboBox.setItemLabelGenerator(i -> String.valueOf(i.intValue()));
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

    public void openEditDialog(Long id) {
        Dialog dialog = new Dialog();

        dialog.setHeaderTitle("Edit lift");
        VerticalLayout dialogLayout = createDialogLayout(id);
        dialog.add(dialogLayout);
        Button cancelButton = new Button("Close", e -> dialog.close());
        dialog.getFooter().add(cancelButton);

        dialog.open();
    }

    private VerticalLayout createDialogLayout(Long id) {
        Span text = new Span("Editing for lift with id: " + id);
        TextField maxFloor = new TextField("Max Floor");
        TextField minFloor = new TextField("Min Floor");
        Button button = new Button("Save");
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        VerticalLayout dialogLayout = new VerticalLayout(text, maxFloor,
                minFloor, button);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        button.addClickListener(click -> {
            var lift = liftRepository.findById(id).get();
            if (!maxFloor.isEmpty()) {
                lift.setMaxFloorNumber(Integer.parseInt(maxFloor.getValue()));
            }
            if (!minFloor.isEmpty()) {
                lift.setMinFloorNumber(Integer.parseInt(minFloor.getValue()));
            }
            liftRepository.save(lift);
            updateComboBoxes();
        });

        return dialogLayout;
    }
}
