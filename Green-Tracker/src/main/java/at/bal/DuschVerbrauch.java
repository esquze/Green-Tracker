package at.bal;

// TODO
// Setter bei array konstruktor

import java.util.List;
import java.util.Objects;

public class DuschVerbrauch extends WasserVerbrauch {

    // FixList
    public static final List<String> vorgeschlageneTaetigkeiten = List.of(
            "Duschen", "Baden"
    );

    private GasVerbrauch zugeordneterGasVerbrauch;

    public DuschVerbrauch(String taetigkeit, double dauer,
                          double aktuellerWasserPreis, double kubikmeternVerbrauchWasser,
                          GasVerbrauch zugeordneterGasVerbrauch) throws GreenTrackerException {
        super(taetigkeit, dauer, aktuellerWasserPreis, kubikmeternVerbrauchWasser);
        setZugeordneterGasVerbrauch(zugeordneterGasVerbrauch);
    }


    public DuschVerbrauch(String[] lineParts) throws GreenTrackerException {
        super(lineParts[1], Double.parseDouble(lineParts[2]), Double.parseDouble(lineParts[3]), Double.parseDouble(lineParts[4]));
    }

    public GasVerbrauch getZugeordneterGasVerbrauch() {
        return zugeordneterGasVerbrauch;
    }

    public void setZugeordneterGasVerbrauch(GasVerbrauch zugeordneterGasVerbrauch) throws GreenTrackerException {
        if (zugeordneterGasVerbrauch == null) {
            throw new GreenTrackerException("Fehler: Gas-Verbrauch darf nicht null sein");
        }
        this.zugeordneterGasVerbrauch = zugeordneterGasVerbrauch;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DuschVerbrauch that = (DuschVerbrauch) o;
        return Objects.equals(zugeordneterGasVerbrauch, that.zugeordneterGasVerbrauch);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), zugeordneterGasVerbrauch);
    }

    @Override
    public double ausstoss() {
        return super.ausstoss() + zugeordneterGasVerbrauch.ausstoss();
    }

    @Override
    public double preis() {
        return super.preis() + zugeordneterGasVerbrauch.preis();
    }


    @Override
    public String toString() {
        return super.toString() + ", zugeordneter Gasverbrauch: " + zugeordneterGasVerbrauch ;
    }

    public String toCsvString() {
        return super.toCsvString() + ";" + zugeordneterGasVerbrauch;
    }

}
