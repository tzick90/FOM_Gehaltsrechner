package FOMProjektrechner;

import util.Ergebnis;

/**
 * Hauptlogik für die vereinfachte Brutto-Netto-Berechnung im Projektmodus.
 * Nutzt ProjektAbgabenberechner für die Sozialversicherungsbeiträge.
 * Keine CSVs, keine Beitragsbemessungsgrenzen, keine Jahresabhängigkeit.
 */

public class FOMGehaltsrechner {

    // Einzige Quelle der Wahrheit für gültige Steuerklassen im Projektmodus
    public static final String[] GUELTIGE_STEUERKLASSEN = {"I", "III", "IV"};

    /**
     * @param steuerklasse    "I", "III" oder "IV"
     * @param kirchenmitglied "Ja" oder "Nein"
     * @param kvZusatzbeitrag KV-Zusatzbeitrag in % (Standard 1.3, vom User überschreibbar)
     */
    public static Ergebnis berechneGehalt(
            double brutto,
            String steuerklasse,
            String kirchenmitglied,
            String bundesland,
            int kinderanzahl,
            int alter,
            double kvZusatzbeitrag
    ) {
        boolean istKirchenmitglied = kirchenmitglied.equals("Ja");

        // ===== SOZIALABGABEN (über Hilfsklasse FOMAbgabenrechner) =====

        double kvBasisBetrag  = FOMAbgabenrechner.berechneKvBasisBetrag(brutto);
        double kvZusatzBetrag = FOMAbgabenrechner.berechneKvZusatzBetrag(brutto, kvZusatzbeitrag);
        double kvBeitrag      = kvBasisBetrag + kvZusatzBetrag;

        double rvBeitrag = FOMAbgabenrechner.berechneRentenversicherung(brutto);
        double avBeitrag = FOMAbgabenrechner.berechneArbeitslosenversicherung(brutto);
        double pvBeitrag = FOMAbgabenrechner.berechnePflegeversicherung(brutto, kinderanzahl, alter);

        double sozialabgabenGesamt = kvBeitrag + rvBeitrag + avBeitrag + pvBeitrag;

        // ===== LOHNSTEUER (pauschal nach Steuerklasse) =====

        double lohnsteuerSatz;
        switch (steuerklasse) {
            case "I":   lohnsteuerSatz = 20.0; break;
            case "III": lohnsteuerSatz = 15.0; break;
            case "IV":  lohnsteuerSatz = 18.0; break;
            default:
                throw new IllegalArgumentException("Ungültige Steuerklasse für Projektmodus: " + steuerklasse);
        }

        double lohnsteuerMonat = brutto * lohnsteuerSatz / 100;
        double lohnsteuerJahr  = lohnsteuerMonat * 12;

        // ===== KIRCHENSTEUER (abhängig vom Bundesland) =====

        double kirchensteuersatz = getKirchensteuersatz(bundesland); // 8 oder 9
        double kirchensteuerMonat = istKirchenmitglied
                ? lohnsteuerMonat * kirchensteuersatz / 100
                : 0.0;
        double kirchensteuerJahr = kirchensteuerMonat * 12;

        // ===== SOLI (im Projektmodus nicht vorgesehen) =====

        double soliMonat = 0.0;
        double soliJahr  = 0.0;

        // ===== NETTO =====

        double nettoMonat = brutto - sozialabgabenGesamt - lohnsteuerMonat - kirchensteuerMonat - soliMonat;

        return new Ergebnis(
                brutto,
                nettoMonat,
                lohnsteuerJahr,
                lohnsteuerMonat,
                kirchensteuerJahr,
                kirchensteuerMonat,
                soliJahr,
                soliMonat,
                kvBeitrag,
                kvZusatzbeitrag,
                kvBasisBetrag,
                kvZusatzBetrag,
                rvBeitrag,
                avBeitrag,
                pvBeitrag
        );
    }

    /**
     * Bayern und Baden-Württemberg: 8% Kirchensteuer, alle anderen Bundesländer: 9%
     */
    private static double getKirchensteuersatz(String bundesland) {
        if (bundesland.equalsIgnoreCase("Bayern") || bundesland.equalsIgnoreCase("Baden-Württemberg")) {
            return 8.0;
        }
        return 9.0;
    }
}
