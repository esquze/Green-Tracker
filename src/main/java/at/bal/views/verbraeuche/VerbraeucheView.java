package at.bal.views.verbraeuche;

import at.bal.model.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Verbräuche")
@Route("verbraeuche")
@Menu(order = 1, icon = LineAwesomeIconUrl.LIST_SOLID)
public class VerbraeucheView extends VerticalLayout {

    private enum Typ { STROM, GAS, WASSER, DUSCHE }

    private final ComboBox<Typ> typBox = new ComboBox<>("Typ");
    private final ComboBox<String> taetigkeitBox = new ComboBox<>("Tätigkeit");
    private final NumberField dauerField = new NumberField("Dauer (Min.)");
    private final NumberField preisField = new NumberField("Preis pro Einheit (€)");
    private final NumberField mengeField = new NumberField("Menge");

    private final NumberField gasPreisField = new NumberField("Gas-Preis (€/m³)");
    private final NumberField gasMengeField = new NumberField("Gas-Menge (m³)");
    private final VerticalLayout gasBlock = new VerticalLayout();

    public VerbraeucheView() {
        H2 header = new H2("Verbrauch hinzufügen");

        typBox.setItems(Typ.values());
        typBox.setItemLabelGenerator(this::typLabel);
        typBox.setValue(Typ.STROM);

        taetigkeitBox.setAllowCustomValue(true);
        taetigkeitBox.addCustomValueSetListener(e -> taetigkeitBox.setValue(e.getDetail()));

        dauerField.setMin(0.01); dauerField.setStep(1);
        preisField.setMin(0.01); preisField.setStep(0.01);
        mengeField.setMin(0.01); mengeField.setStep(0.1);
        gasPreisField.setMin(0.01); gasPreisField.setStep(0.01);
        gasMengeField.setMin(0.01); gasMengeField.setStep(0.1);

        gasBlock.setPadding(false);
        gasBlock.setSpacing(false);
        gasBlock.add(new H4("Zusätzlich für Dusche:"),
                new HorizontalLayout(gasPreisField, gasMengeField));
        gasBlock.setVisible(false);

        typBox.addValueChangeListener(e -> {
            updateForTyp(e.getValue());
            gasBlock.setVisible(e.getValue() == Typ.DUSCHE);
        });
        updateForTyp(Typ.STROM);

        HorizontalLayout row1 = new HorizontalLayout(typBox, taetigkeitBox);
        HorizontalLayout row2 = new HorizontalLayout(dauerField, preisField, mengeField);
        row1.setWidthFull();
        row2.setWidthFull();
        taetigkeitBox.setWidthFull();

        Button hinzufuegenBtn = new Button("Hinzufügen", e -> hinzufuegen());
        hinzufuegenBtn.getElement().setAttribute("theme", "primary");
        Button resetBtn = new Button("Zurücksetzen", e -> clearForm());

        add(header, row1, row2, gasBlock, new HorizontalLayout(hinzufuegenBtn, resetBtn));
        setMaxWidth("800px");
        setPadding(true);
    }

    private String typLabel(Typ t) {
        return switch (t) {
            case STROM -> "Strom";
            case GAS -> "Gas";
            case WASSER -> "Wasser";
            case DUSCHE -> "Dusche";
        };
    }

    private void updateForTyp(Typ t) {
        if (t == null) return;
        switch (t) {
            case STROM  -> taetigkeitBox.setItems(StromVerbrauch.vorgeschlageneTaetigkeiten);
            case GAS    -> taetigkeitBox.setItems(GasVerbrauch.vorgeschlageneTaetigkeiten);
            case WASSER -> taetigkeitBox.setItems(WasserVerbrauch.vorgeschlageneTaetigkeiten);
            case DUSCHE -> taetigkeitBox.setItems(DuschVerbrauch.vorgeschlageneTaetigkeiten);
        }
        switch (t) {
            case STROM -> {
                mengeField.setLabel("Verbrauch (kWh)");
                preisField.setLabel("Strom-Preis (€/kWh)");
            }
            case GAS -> {
                mengeField.setLabel("Verbrauch (m³)");
                preisField.setLabel("Gas-Preis (€/m³)");
            }
            case WASSER, DUSCHE -> {
                mengeField.setLabel("Verbrauch (m³)");
                preisField.setLabel("Wasser-Preis (€/m³)");
            }
        }
    }

    private void hinzufuegen() {
        try {
            Typ t = typBox.getValue();
            String taetigkeit = taetigkeitBox.getValue();
            Double dauer = dauerField.getValue();
            Double preis = preisField.getValue();
            Double menge = mengeField.getValue();

            if (t == null || taetigkeit == null || taetigkeit.isBlank()
                    || dauer == null || preis == null || menge == null) {
                fehler("Bitte alle Felder ausfüllen");
                return;
            }

            Verbrauch v = switch (t) {
                case STROM -> new StromVerbrauch(taetigkeit, dauer, preis, menge);
                case GAS -> new GasVerbrauch(taetigkeit, dauer, preis, menge);
                case WASSER -> new WasserVerbrauch(taetigkeit, dauer, preis, menge);
                case DUSCHE -> {
                    Double gp = gasPreisField.getValue();
                    Double gm = gasMengeField.getValue();
                    if (gp == null || gm == null) {
                        throw new GreenTrackerException("Bitte auch Gas-Felder ausfüllen");
                    }
                    yield new DuschVerbrauch(taetigkeit, dauer, preis, menge,
                            new GasVerbrauch("Warmwasser", dauer, gp, gm));
                }
            };

            GreenTracker greenTracker = new GreenTracker("Greentracker");
            greenTracker.readVerbraucheFromCsv();
            greenTracker.hinzufuegen(v);
            greenTracker.writeVerbraucheToCsv();

            erfolg("Hinzugefügt: " + taetigkeit + String.format("  (CO₂: %.2f, Preis: %.2f €)", v.ausstoss(), v.preis()));
            clearForm();

        } catch (GreenTrackerException ex) {
            fehler(ex.getMessage());
        }
    }

    private void clearForm() {
        taetigkeitBox.clear();
        dauerField.clear();
        preisField.clear();
        mengeField.clear();
        gasPreisField.clear();
        gasMengeField.clear();
    }

    private void erfolg(String msg) {
        Notification n = Notification.show(msg, 3000, Notification.Position.TOP_CENTER);
        n.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private void fehler(String msg) {
        Notification n = Notification.show(msg, 4000, Notification.Position.TOP_CENTER);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}