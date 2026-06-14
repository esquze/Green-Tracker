package at.bal.model;

import java.util.List;
import java.util.Objects;

public class WasserVerbrauch extends Verbrauch {

    // FixList
    public static final List<String> vorgeschlageneTaetigkeiten = List.of(
            "Duschen", "Hände waschen", "Geschirrspülen", "Auto waschen", "Waschmaschine"
    );

    private double aktuellerWasserPreis;
    private double kubikmeternVerbrauchWasser;
    private static final double EMISSIONSFAKTOR = 0.34;

    public WasserVerbrauch(String taetigkeit, double dauer, double aktuellerWasserPreis, double kubikmeternVerbrauchWasser) throws GreenTrackerException {
        super(taetigkeit, dauer);
        setAktuellerWasserPreis(aktuellerWasserPreis);
        setKubikmeternVerbrauchWasser(kubikmeternVerbrauchWasser);
    }

    public WasserVerbrauch(String[] lineParts) throws GreenTrackerException {
        super(lineParts);
        try {
            setAktuellerWasserPreis(Double.parseDouble(lineParts[3]));
            setKubikmeternVerbrauchWasser(Double.parseDouble(lineParts[4]));
        } catch (NumberFormatException e) {
            System.out.println("Error Wasserverbrauch import: " + e.getMessage());
        }
    }

    public double getAktuellerWasserPreis() {
        return aktuellerWasserPreis;
    }

    public void setAktuellerWasserPreis(double aktuellerWasserPreis) throws GreenTrackerException {
        if (aktuellerWasserPreis <= 0.0) {
            throw new GreenTrackerException("Fehler: Wasser-Preis muss größer 0.0 sein");
        }
        this.aktuellerWasserPreis = aktuellerWasserPreis;
    }

    public double getKubikmeternVerbrauchWasser() {
        return kubikmeternVerbrauchWasser;
    }

    public void setKubikmeternVerbrauchWasser(double kubikmeternVerbrauchWasser) throws GreenTrackerException {
        if (kubikmeternVerbrauchWasser <= 0.0) {
            throw new GreenTrackerException("Fehler: Wasser-Kubikmetern muss größer 0.0 sein");
        }
        this.kubikmeternVerbrauchWasser = kubikmeternVerbrauchWasser;
    }

    @Override
    public double preis() {
        return aktuellerWasserPreis * kubikmeternVerbrauchWasser;
    }

    @Override
    public double ausstoss() {
        return kubikmeternVerbrauchWasser * getDauerMin();
    }

    @Override
    public double co2Fussabdruck() {
        return getKubikmeternVerbrauchWasser() * EMISSIONSFAKTOR;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        WasserVerbrauch that = (WasserVerbrauch) o;
        return Double.compare(aktuellerWasserPreis, that.aktuellerWasserPreis) == 0 && Double.compare(kubikmeternVerbrauchWasser, that.kubikmeternVerbrauchWasser) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), aktuellerWasserPreis, kubikmeternVerbrauchWasser);
    }

    @Override
    public String toString() {
        return super.toString() + ", akt. Wasser Preis: " + aktuellerWasserPreis + ", m³: " + kubikmeternVerbrauchWasser;
    }

    public String toCsvString() {
        return super.toCsvString() + ";" + aktuellerWasserPreis + ";" + kubikmeternVerbrauchWasser;
    }

}
