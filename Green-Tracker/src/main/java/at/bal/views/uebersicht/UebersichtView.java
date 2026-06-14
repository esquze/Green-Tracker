package at.bal.views.uebersicht;

import at.bal.components.MetricCard;
import at.bal.model.GreenTracker;
import at.bal.model.GreenTrackerException;
import at.bal.model.Verbrauch;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.List;

@PageTitle("Übersicht")
@Route("")
@Menu(order = 0, icon = LineAwesomeIconUrl.HOME_SOLID)
public class UebersichtView extends Composite<VerticalLayout> {

    private GreenTracker greenTracker;
    private final VerticalLayout ergebnis = new VerticalLayout();

    public UebersichtView() {
        VerticalLayout root = getContent();
        root.setWidth("100%");
        root.add(new H2("Übersicht"));

        ergebnis.setPadding(false);
        ergebnis.setWidthFull();
        root.add(ergebnis);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        try {
            aktualisieren();
        } catch (GreenTrackerException e) {
            Notification.show("Fehler beim Laden: " + e.getMessage());
        }
    }

    private void aktualisieren() throws GreenTrackerException {
        ergebnis.removeAll();

        greenTracker = new GreenTracker("Greentracker");
        greenTracker.readVerbraucheFromCsv();
        List<Verbrauch> verbrauche = greenTracker.getVerbrauche();

        if (verbrauche.isEmpty()) {
            ergebnis.add(new Paragraph("Noch keine Verbrauchsdaten erfasst."));
            return;
        }

        double gesamtCo2   = greenTracker.berechneGesamtCo2();
        double gesamtPreis = greenTracker.berechneGesamtPreis();
        String bewertung   = greenTracker.co2Bewertung(gesamtCo2);

        HorizontalLayout cards = new HorizontalLayout();
        cards.setWidthFull();
        cards.add(
                new MetricCard("CO₂-Fußabdruck", String.format("%.1f", gesamtCo2), "kg"),
                new MetricCard("Gesamtpreis", String.format("%.2f", gesamtPreis), "€"),
                new MetricCard("Verbräuche", String.valueOf(verbrauche.size()), null)
        );
        cards.getChildren().forEach(c -> cards.setFlexGrow(1, c));
        ergebnis.add(cards);

        HorizontalLayout bewertungZeile = new HorizontalLayout();
        bewertungZeile.setAlignItems(FlexComponent.Alignment.CENTER);
        bewertungZeile.add(new Span("Bewertung:"), bewertungsBadge(bewertung));
        ergebnis.add(bewertungZeile);
    }

    private Span bewertungsBadge(String bewertung) {
        Span badge = new Span(bewertung);
        String theme = switch (bewertung) {
            case "gut"              -> "badge success";
            case "durchschnittlich" -> "badge";
            default                 -> "badge error";
        };
        badge.getElement().getThemeList().add(theme);
        return badge;
    }
}