package FOMProjektrechner;

// Hilfsklasse für die Sozialabgabenberechnung des Projektrechners

public class FOMAbgabenrechner {

    private static final double KV_BASIS_GESAMT   = 14.6;  // wird halbiert -> 7,3% AN-Anteil
    private static final double RV_SATZ_AN        = 9.3;   // bereits AN-Anteil
    private static final double AV_SATZ_AN        = 1.3;   // bereits AN-Anteil
    private static final double PV_SATZ_NORMAL    = 1.525; // mit Kind(ern) oder <=23 Jahre
    private static final double PV_SATZ_KINDERLOS = 2.05;  // kinderlos und >23 Jahre


    //Basisanteil der Krankenversicherung (AN-Anteil, 7,3%)
    public static double berechneKvBasisBetrag(double brutto) {
        return brutto * (KV_BASIS_GESAMT / 2) / 100;
    }


    /**
     * Zusatzbeitrag-Anteil der Krankenversicherung (AN-Anteil, halbiert)
     * @param kvZusatzbeitrag KV-Zusatzbeitrag in % (Standard 1,3, vom User überschreibbar)
     */

    public static double berechneKvZusatzBetrag(double brutto, double kvZusatzbeitrag) {
        return brutto * (kvZusatzbeitrag / 2) / 100;
    }


    //Rentenversicherung (AN-Anteil, 9,3%)
    public static double berechneRentenversicherung(double brutto) {
        return brutto * RV_SATZ_AN / 100;
    }

    //Arbeitslosenversicherung (AN-Anteil, 1,3%)
    public static double berechneArbeitslosenversicherung(double brutto) {
        return brutto * AV_SATZ_AN / 100;
    }

    /**
     * Pflegeversicherung.
     * Kinderlos und älter als 23 Jahre: 2,05%
     * Sonst (mit Kind(ern) oder <=23 Jahre): 1,525%
     */
    public static double berechnePflegeversicherung(double brutto, int kinderanzahl, int alter) {
        double satz = (kinderanzahl == 0 && alter > 23) ? PV_SATZ_KINDERLOS : PV_SATZ_NORMAL;
        return brutto * satz / 100;
    }


}
