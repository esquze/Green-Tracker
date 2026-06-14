package at.bal.components;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class MetricCard extends VerticalLayout {
    public MetricCard(String label, String value, String unit) {
        Span labelSpan = new Span(label);
        labelSpan.addClassName("metric-label");

        Span valueSpan = new Span(value);
        valueSpan.addClassName("metric-value");

        if (unit != null) {
            Span unitSpan = new Span(" " + unit);
            unitSpan.addClassName("metric-unit");
            valueSpan.add(unitSpan);
        }

        add(labelSpan, valueSpan);
        addClassName("metric-card");
        setPadding(true);
        setSpacing(false);
    }
}
