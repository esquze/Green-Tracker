package at.bal;

// TODO
// toString
// CSVString
// Logik von Ausstoß besprechen

import java.util.Objects;

public class WasserVerbrauch extends Verbrauch {

    private double aktuellerWasserPreis;
    private double kubikmeternVerbrauchWasser;

    public WasserVerbrauch(String taetigkeit, double dauer, double aktuellerWasserPreis, double kubikmeternVerbrauchWasser) throws GreenTrackerException {
        super(taetigkeit, dauer);
        setAktuellerWasserPreis(aktuellerWasserPreis);
        setKubikmeternVerbrauchWasser(kubikmeternVerbrauchWasser);
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
}
