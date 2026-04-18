public class Abgabenrechner {




    /**
    - Methode zur Berechnung der Krankenversicherung
    - Arbeitnehmer zahl 50% von (Basis + Zusatzbeitrag)

    */

    public static double berechneKrankenversicherung(
            double bruttogehalt,
            double kvBasis,
            double kvZusatz
    ) {
        // Arbeitnehmer zahlt nur die Hälfte (Arbeitgeber zahlt andere Hälfte)
        return bruttogehalt * ((kvBasis + kvZusatz) / 2) / 100;
    }

    /**
     *Berechnet die Rentenversicherung (Arbeitnehmeranteil)
     */

    public static double berechneRentenversicherung(
            double bruttogehalt,
            double rvSatz
    ) {
        return bruttogehalt * rvSatz / 100;
    }

    /**
     * Berechnet die Arbeitslosenversicherung (AN-Anteil)
     */

    public static double berechneArbeitslosenversicherung(
            double bruttogehalt,
            double avSatz
    ) {
        return bruttogehalt * avSatz / 100;
    }

    /**
     * Berechnet die Pflegeversicherung
     * Achtung!: Höherer Satz für Kinderlose über 23 Jahre
     */

    public static double berechnePflegeversicherung(
            double bruttogehalt,
            double pvStandard,
            double pvKinderlos,
            boolean kinderlos,
            int alter
    ) {
        // Kinderlose über 23 zahlen Zuschlag
        double satz;
        if (kinderlos && alter > 23) {
            satz = pvKinderlos;
        } else {
            satz = pvStandard;
        }

        return bruttogehalt * satz / 100;
    }

    /**
     * Berechnet Summe aller Sozialabgaben
     */
    /**public static double berechneGesamtSozialabgaben(
            double bruttogehalt,
            double kvBasis,
            double kvZusatz,
            double rvSatz,
            double avSatz,
            double pvStandard,
            double pvKinderlos,
            boolean kinderlos,
            int alter
    ) {
        // TODO: Implementieren
    }
     */






}
