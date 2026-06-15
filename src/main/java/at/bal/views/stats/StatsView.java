package at.bal.views.stats;

import at.bal.model.*;
import at.bal.components.MetricCard;
import com.github.appreciated.apexcharts.ApexCharts;
import com.github.appreciated.apexcharts.ApexChartsBuilder;
import com.github.appreciated.apexcharts.config.builder.ChartBuilder;
import com.github.appreciated.apexcharts.config.builder.TitleSubtitleBuilder;
import com.github.appreciated.apexcharts.config.builder.XAxisBuilder;
import com.github.appreciated.apexcharts.config.chart.Type;
import com.github.appreciated.apexcharts.helper.Series;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.Comparator;
import java.util.List;

@PageTitle("Stats")
@Route("stats")
@Menu(order = 2, icon = LineAwesomeIconUrl.CHART_BAR_SOLID)
public class StatsView extends Composite<VerticalLayout> {

    private Grid<Verbrauch> verbrauchTable;
    private VerticalLayout mylayout;
    private GreenTracker greenTracker;

    private HorizontalLayout cardsLayout;
    private HorizontalLayout chartsLayout;
    private Paragraph leerHinweis;

    public StatsView() throws GreenTrackerException {
        initComponents();
        initVerbrauchTable();
        addComponents();

        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().add(mylayout);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        try {
            updateTable();
        } catch (GreenTrackerException e) {
            Notification.show("Fehler beim Laden: " + e.getMessage());
        }
    }

    private void initComponents() throws GreenTrackerException {
        verbrauchTable = new Grid<>(Verbrauch.class, false);

        verbrauchTable.addColumn(this::typBezeichnung)
                .setHeader("Typ").setSortable(true).setAutoWidth(true);
        verbrauchTable.addColumn(Verbrauch::getTaetigkeit)
                .setHeader("Tätigkeit").setSortable(true);
        verbrauchTable.addColumn(v -> String.format("%.1f", v.co2Fussabdruck()))
                .setHeader("CO₂ (kg)").setSortable(true)
                .setComparator(Comparator.comparingDouble(Verbrauch::co2Fussabdruck));
        verbrauchTable.addColumn(v -> String.format("%.2f", v.preis()))
                .setHeader("Preis (€)").setSortable(true)
                .setComparator(Comparator.comparingDouble(Verbrauch::preis));

        mylayout = new VerticalLayout();
        greenTracker = new GreenTracker("Greentracker");
    }

    private void initVerbrauchTable() throws GreenTrackerException {
        verbrauchTable.addColumn(verbrauch -> switch (verbrauch) {
            case StromVerbrauch s -> s.ausstoss() + " kWh";
            case GasVerbrauch g -> g.ausstoss() + " m³";
            case WasserVerbrauch w -> w.ausstoss() + " m³";
            case null, default -> null;
        }).setHeader("Menge");

        verbrauchTable.addColumn(verbrauch -> switch (verbrauch) {
            case DuschVerbrauch d -> d.getZugeordneterGasVerbrauch().ausstoss();
            case null, default -> null;
        }).setHeader("Zusatz-Gas");

        verbrauchTable.addComponentColumn(verbrauch -> {
            Button delButton = new Button(new Icon(VaadinIcon.TRASH));
            delButton.setThemeName("icon error");
            delButton.addClickListener(event -> {
                greenTracker.entfernen(verbrauch);
                try {
                    greenTracker.writeVerbraucheToCsv();
                    Notification.show("Entfernt: " + verbrauch.getTaetigkeit());
                    updateTable();
                } catch (GreenTrackerException e) {
                    Notification.show("Fehler: " + e.getMessage());
                }
            });
            return delButton;
        });
    }

    private void updateTable() throws GreenTrackerException {
        // Aus CSV laden
        greenTracker = new GreenTracker("Greentracker");
        greenTracker.readVerbraucheFromCsv();
        List<Verbrauch> verbrauche = greenTracker.getVerbrauche();

        verbrauchTable.setItems(verbrauche);
        rebuildAggregations(verbrauche);
    }

    private void addComponents() {
        mylayout.add(new H2("Statistik"));
        mylayout.add(verbrauchTable);
    }

    // Charts
    private void rebuildAggregations(List<Verbrauch> verbrauche) {
        if (cardsLayout != null) mylayout.remove(cardsLayout);
        if (chartsLayout != null) mylayout.remove(chartsLayout);
        if (leerHinweis != null) mylayout.remove(leerHinweis);

        if (verbrauche.isEmpty()) {
            leerHinweis = new Paragraph("Keine Verbrauchsdaten gefunden.");
            mylayout.addComponentAtIndex(0, leerHinweis);
            return;
        }

        double gesamtCo2 = verbrauche.stream().mapToDouble(Verbrauch::co2Fussabdruck).sum();
        double gesamtPreis = verbrauche.stream().mapToDouble(Verbrauch::preis).sum();
        int anzahl = verbrauche.size();
        double schnitt = gesamtCo2 / anzahl;

        double co2Strom = summe(verbrauche, "Strom",  Verbrauch::co2Fussabdruck);
        double co2Gas = summe(verbrauche, "Gas",    Verbrauch::co2Fussabdruck);
        double co2Wasser = summe(verbrauche, "Wasser", Verbrauch::co2Fussabdruck);

        double prStrom = summe(verbrauche, "Strom",  Verbrauch::preis);
        double prGas = summe(verbrauche, "Gas",    Verbrauch::preis);
        double prWasser = summe(verbrauche, "Wasser", Verbrauch::preis);

        cardsLayout = buildMetricCards(gesamtCo2, gesamtPreis, anzahl, schnitt);
        chartsLayout = new HorizontalLayout();
        chartsLayout.setWidthFull();
        chartsLayout.add(
                buildBarChart(co2Strom, co2Gas, co2Wasser),
                buildPieChart(prStrom, prGas, prWasser));
        chartsLayout.getChildren().forEach(c -> chartsLayout.setFlexGrow(1, c));

        mylayout.addComponentAtIndex(0, chartsLayout);
        mylayout.addComponentAtIndex(0, cardsLayout);
    }

    private double summe(List<Verbrauch> liste, String typ,
                         java.util.function.ToDoubleFunction<Verbrauch> kennzahl) {
        return liste.stream()
                .filter(v -> typBezeichnung(v).equals(typ)
                        || (typ.equals("Wasser") && v instanceof DuschVerbrauch))
                .mapToDouble(kennzahl)
                .sum();
    }

    private String typBezeichnung(Verbrauch v) {
        return switch (v) {
            case DuschVerbrauch d -> "Dusche";
            case StromVerbrauch s -> "Strom";
            case GasVerbrauch g -> "Gas";
            case WasserVerbrauch w -> "Wasser";
            case null, default -> "Unbekannt";
        };
    }

    private HorizontalLayout buildMetricCards(double gesamtCo2, double gesamtPreis,
                                              int anzahl, double schnitt) {
        HorizontalLayout cards = new HorizontalLayout();
        cards.setWidthFull();
        cards.add(
                new MetricCard("Gesamt CO₂", String.format("%.1f", gesamtCo2), "kg"),
                new MetricCard("Gesamtpreis", String.format("%.2f", gesamtPreis), "€"),
                new MetricCard("Verbräuche", String.valueOf(anzahl), null),
                new MetricCard("Ø pro Verbrauch", String.format("%.1f", schnitt), "kg")
        );
        cards.getChildren().forEach(c -> cards.setFlexGrow(1, c));
        return cards;
    }

    private ApexCharts buildBarChart(double strom, double gas, double wasser) {
        ApexCharts chart = ApexChartsBuilder.get()
                .withChart(ChartBuilder.get().withType(Type.BAR).build())
                .withSeries(new Series<>("CO₂ (kg)", round(strom), round(gas), round(wasser)))
                .withXaxis(XAxisBuilder.get().withCategories("Strom", "Gas", "Wasser").build())
                .withTitle(TitleSubtitleBuilder.get().withText("CO₂ nach Typ").build())
                .build();
        chart.setWidthFull();
        chart.setHeight("320px");
        return chart;
    }

    private ApexCharts buildPieChart(double strom, double gas, double wasser) {
        ApexCharts chart = ApexChartsBuilder.get()
                .withChart(ChartBuilder.get().withType(Type.DONUT).build())
                .withSeries(round(strom), round(gas), round(wasser))
                .withLabels("Strom", "Gas", "Wasser")
                .withTitle(TitleSubtitleBuilder.get().withText("Preis-Anteil").build())
                .build();
        chart.setWidthFull();
        chart.setHeight("320px");
        return chart;
    }

    private double round(double d) {
        return Math.round(d * 100.0) / 100.0;
    }
}