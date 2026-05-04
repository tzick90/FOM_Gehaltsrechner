public class SteuerKonstanten {
    /*
    - Zentrale Verwaltung der steuerlichen Konstanzen
    - Diese Werte sind kurzfristig stabil (2024-2026), ändern sich aber
      mittelfristig und sollten später über ein Settings-Tab in der GUI konfigurierbar sein.

    - Quellen: EStG, BMF
     */

    public class Steuerkonstanten {

        /*
        Entlastungsbetrag für Alleinerziehende (1. Kind)
        Stand: 2023, gültig 2024-2026
        Quelle: §24b Abs. 1 EStG
         */

        public static final double ENTLASTUNGSBETRAG_BASIS = 4260;

        /*
        Entlastungsbetrag je weiteres Kind
        Stand: 2023, gültig 2024-2026
        Quelle: §24 Abs. 2 EStG
         */

        public static final double ENTLASTUNGSBETRAG_WEITERES_KIND = 240;

        //PAUSCHAL-BETRÄGE

        /*
        Werbekostenpauschale für Arbeitnehmer
        Stand: 2022, gültig 2022-2026
        Quelle: §9a Satz 1 Nr. 1a EStG
         */

        public static final double WERBUNGSKOSTEN_PAUSCHALE = 1230;

        /*
         Sonderausgaben-Pauschbetrag
         Stand: seit Jahren unverändert
         Quelle: §10c Abs. 1 EStG
         */

        public static final double SONDERAUSGABEN_PAUSCHALE = 36;


        // SOLIDARITÄTSZUSCHLAG Später implementiert


        /*
         Solidaritätszuschlag-Satz
         Stand: seit 1998 unverändert
         Quelle: §4 SolZG
         */

        public static final double SOLI_SATZ = 5.5; // Soli-Steuer muss woanders rein!

        /*
         Soli Freigrenze (Jahres-Lohnsteuer)
         Stand: seit 2021
         Quelle: §3 SolZG
         */

        public static final double SOLI_FREIGRENZE_JAHR = 17543;

        /*
         Soli Obergrenze Gleitzone (Jahres-Lohnsteuer)
         Stand: seit 2021
         Quelle: §3 SolZG
         */

        public static final double SOLI_OBERGRENZE_JAHR = 33183;


        // ====================================
        // SPÄTER: SETTINGS-INTERFACE
        // ====================================

        // TODO: Diese Klasse später durch Settings-System ersetzen:
        // - JSON-Konfigurationsdatei
        // - Admin-GUI zum Anpassen
        // - Versionierung der Konstanten
        // - Validierung der Werte

    }
}
