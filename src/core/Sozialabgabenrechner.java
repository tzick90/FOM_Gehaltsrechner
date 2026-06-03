package core;

public class Sozialabgabenrechner {

    /**
     * Innere Klasse: Speichert Beitragsinformationen einer Krankenkasse
     */
    public static class KrankenkassenInfo {

        private double gesamtbeitrag;
        private double zusatzbeitrag;

        public KrankenkassenInfo(double gesamtbeitrag, double zusatzbeitrag) {
            this.gesamtbeitrag = gesamtbeitrag;
            this.zusatzbeitrag = zusatzbeitrag;
        }

        public double getGesamtbeitrag() {
            return gesamtbeitrag;
        }

        public double getZusatzbeitrag() {
            return zusatzbeitrag;
        }

        @Override
        public String toString() {
            // ← MUSS INNERHALB von KrankenkassenInfo sein!
            return "Gesamt: " + gesamtbeitrag + "%, Zusatz: " + zusatzbeitrag + "%";
        }
    }

    /**
     * Berechnet den Arbeitnehmeranteil der Krankenversicherung
     * Arbeitnehmer zahlt 50% des Gesamtbeitragssatzes
     *
     * @param bruttogehalt Bruttogehalt, monatlich
     * @param kvBasis KV Basis-Beitragssatz (14,6%)
     * @param kvZusatz KV Zusatzbeitrag der Krankenkasse (z.B. 2,69%)
     * @param bbgKV Beitragsbemessungsgrenze KV
     * @return KV-Beitrag Arbeitnehmeranteil in EUR
     */

    public static double berechneKrankenversicherung(
            double bruttogehalt,
            double kvBasis,
            double kvZusatz,
            double bbgKV
    ) {
        double bemessungsgrundlage = Math.min(bruttogehalt, bbgKV);

        // Arbeitnehmer zahlt 50% von (Basis + Zusatz)
        double kvSatzAN = (kvBasis + kvZusatz) / 2;

        return bemessungsgrundlage * kvSatzAN / 100;
    }
    public static double berechneKvBasisAnteil(double bruttogehalt, double kvBasis, double bbgKV) {
        return Math.min(bruttogehalt, bbgKV) * (kvBasis / 2) / 100;
    }

    public static double berechneKvZusatzAnteil(double bruttogehalt, double kvZusatz, double bbgKV) {
        return Math.min(bruttogehalt, bbgKV) * (kvZusatz / 2) / 100;
    }

    /**
     *Berechnet die Rentenversicherung (Arbeitnehmeranteil)
     * AN zahlt 50% des Gesamtbeitragssatzes
     *
     * @param bruttogehalt Bruttogehalt monatlich
     * @param rvSatzGesamt RF Gesamtbeitragssatz (z.B. 18,6%)
     * @param bbgRV Beitragsbemessungsgrenze Rentenversicherung
     * @return RV-Beitrag Arbeitnehmeranteil in EUR
     */

    public static double berechneRentenversicherung(
            double bruttogehalt,
            double rvSatzGesamt,  // 18.6%
            double bbgRV
    ) {
        double bemessungsgrundlage = Math.min(bruttogehalt, bbgRV);

        // Arbeitnehmer zahlt die Hälfte
        double rvSatzAN = rvSatzGesamt / 2;  // 18.6 / 2 = 9.3%

        return bemessungsgrundlage * rvSatzAN / 100;
    }

    /**
     * Berechnet die Arbeitslosenversicherung (AN-Anteil)
     * Arbeitnehmer zahlt 50% des Gesamtbeitragssatzes
     *
     * @param bruttogehalt Bruttogehalt monatlich
     * @param avSatzGesamt AV Gesamtbeitragssatz (z.B. 2.6%)
     * @param bbgAV Beitragsbemessungsgrenze AV
     * @return AV-Beitrag Arbeitnehmeranteil in Euro
     */

    public static double berechneArbeitslosenversicherung(
            double bruttogehalt,
            double avSatzGesamt, // Aktuell 2,6%
            double bbgAV
    ) {
        double bemessungsgrundlage = Math.min(bruttogehalt, bbgAV);

        // Arbeitnehmer zahlt die Hälfte
        double avSatzAN = avSatzGesamt / 2; //2,6% / 2 = 1,3%

        return bruttogehalt * avSatzAN / 100;
    }

    /**
     * Berechnet Pflegeversicherung mit Kinderanzahl-Staffelung (2025)
     *
     * Regelung 2025:
     * - Basis: 1.8% (mit 0 oder 1 Kind)
     * - Kinderlos >23: 1.8% + 0.6% Zuschlag = 2.4%
     * - Ab 2 Kindern: 1.8% - 0.25% pro Kind (bis max 5 Kinder = -1.0%)
     *
     * Beispiele:
     * - 0 Kinder, 30 Jahre: 2.4%
     * - 1 Kind: 1.8%
     * - 2 Kinder: 1.55%
     * - 3 Kinder: 1.3%
     * - 5 Kinder: 0.8%
     *
     * @param bruttogehalt Bruttogehalt in Euro
     * @param pvSatzGesamt Basis-Satz (1.8%)
     * @param pvZuschlagKinderlos Zuschlag für Kinderlose (0.6%)
     * @param pvAbschlagProKind Abschlag pro Kind ab dem 2. Kind (0.25%)
     * @param anzahlKinder Anzahl Kinder unter 25 Jahren
     * @param alter Alter des Versicherten
     * @param bbgPV Beitragsbemessungsgrenze PV
     * @return PV-Beitrag in EUR
     */


     public static double berechnePflegeversicherung(
            double bruttogehalt,
            double pvSatzGesamt,          // 3.6%
            double pvZuschlagKinderlos, // 0.6
            double pvAbschlagProKind,   // 0.25
            int anzahlKinder,
            int alter,
            double bbgPV
    ) {
         double bemessungsgrundlage = Math.min(bruttogehalt, bbgPV);

         // Arbeitnehmeranteil Basis = Hälfte des Gesamtsatzes
         double satz = pvSatzGesamt / 2;  // 3.6 / 2 = 1.8%

        // Kinderlose über 23 zahlen Zuschlag

         if (anzahlKinder == 0 && alter > 23) {
             // Kinderlos über 23: Zuschlag
             satz += pvZuschlagKinderlos;  // 1.8 + 0.6 = 2.4%

         } else if (anzahlKinder >= 2) {
             // 2 oder mehr Kinder: Abschlag (max 5 Kinder berücksichtigt)
             int kinderFürAbschlag = Math.min(anzahlKinder, 5);

             // Abschlag gilt ab dem 2. Kind
             // 2 Kinder: 1x 0.25% = 0.25%
             // 3 Kinder: 2x 0.25% = 0.50%
             // 5 Kinder: 4x 0.25% = 1.00%
             double abschlag = (kinderFürAbschlag - 1) * pvAbschlagProKind;
             satz -= abschlag;
         }
         // 1 Kind oder ≤23 Jahre: Bleibt bei Basis-Satz 1.8%

         return bemessungsgrundlage * satz / 100;
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
