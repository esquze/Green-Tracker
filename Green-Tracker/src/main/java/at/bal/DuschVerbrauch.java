package at.bal;

// TODO
// toString
// CSVString

public class DuschVerbrauch extends WasserVerbrauch {

    private GasVerbrauch zugeordneterGasVerbrauch;

    public DuschVerbrauch(String taetigkeit, double dauer,
                          double aktuellerWasserPreis, double kubikmeternVerbrauchWasser,
                          GasVerbrauch zugeordneterGasVerbrauch) throws GreenTrackerException {
        super(taetigkeit, dauer, aktuellerWasserPreis, kubikmeternVerbrauchWasser);
        setZugeordneterGasVerbrauch(zugeordneterGasVerbrauch);
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
    public double ausstoss() {
        return super.ausstoss() + zugeordneterGasVerbrauch.ausstoss();
    }

    @Override
    public double preis() {
        return super.preis() + zugeordneterGasVerbrauch.preis();
    }

}
