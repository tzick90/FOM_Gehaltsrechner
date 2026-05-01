import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.ServerError;
import java.sql.SQLSyntaxErrorException;
import java.util.HashMap;
import java.util.Map;

public class CsvReader {

    /**
     * Liest eine CSV-Datei mit Jahr-Filter und gibt eine HashMap zurück
     * Format: Typ, Wert, Jahr, Beschreibung
     * Erste Zeile ist Header (wird übersprungen)
     *
     * @param dateipfad Pfad zur CSV-Datei
     * @param jahr Jahr zum Filtern (z.B. 2025)
     * @return HashMap mit spalte1 -> spalte2 (als Double)
     * @throws IOException wenn Datei nicht gefunden
     */
    public static Map<String, Double> lesenMitJahr(String dateipfad, int jahr) throws IOException {

        Map<String, Double> daten = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(dateipfad))) {

            // Header-Zeile überspringen
            String header = br.readLine();

            // Jede Zeile lesen
            String zeile;
            while ((zeile = br.readLine()) != null) {

                // Leere Zeilen überspringen
                if (zeile.trim().isEmpty()) {
                    continue;
                }

                // An Komma splitten
                String[] teile = zeile.split(",");

                // Validierung: Mindestens 3 Spalten?
                if (teile.length < 3) {
                    System.err.println("Überspringe ungültige Zeile: " + zeile);
                    continue;
                }

                // Spalte 0: Typ
                String typ = teile[0].trim();

                // Spalte 1: Wert
                String wertStr = teile[1].trim();

                // Spalte 2: Jahr
                int zeilenJahr;
                try {
                    zeilenJahr = Integer.parseInt(teile[2].trim());
                } catch (NumberFormatException e) {
                    System.err.println("Ungültiges Jahr in Zeile: " + zeile);
                    continue;
                }

                // Jahr-Filter: Nur gewähltes Jahr laden
                if (zeilenJahr != jahr) {
                    continue; // Zeile überspringen
                }

                // Wert parsen
                try {
                    double wert = Double.parseDouble(wertStr);
                    daten.put(typ, wert);
                } catch (NumberFormatException e) {
                    System.err.println("Ungültige Zahl in Zeile: " + zeile + " (Wert: " + wertStr + ")");
                }




            }
        }

        return daten;
    }

    /**
     * Liest Krankenkassen-CSV mit voller Struktur
     * Format: Krankenkasse, Beitragssatz 2026, Zusatzbeitrag 2026
     * Nutzt Spalte 1 (Name) und Spalten 2+3 (Gesamt + Zusatz)
     *
     * @param dateipfad Pfad zur CSV-Datei
     * @return HashMap mit Krankenkasse -> KrankenkassenInfo
     * @throws IOException wenn Datei nicht gefunden
     */
    public static Map<String, Abgabenrechner.KrankenkassenInfo> leseKrankenkassen(String dateipfad) throws IOException {

        Map<String, Abgabenrechner.KrankenkassenInfo> daten = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(dateipfad))) {

            // Header überspringen
            String header = br.readLine();

            // Jede Zeile lesen
            String zeile;
            while ((zeile = br.readLine()) != null) {

                // Leere Zeilen überspringen
                if (zeile.trim().isEmpty()) {
                    continue;
                }

                // An Komma oder Tab splitten (Regex: [,\t]+ = Kommas oder Tabs)
                String[] teile = zeile.split("[,\t]+");

                // Validierung: Mindestens 3 Spalten?
                if (teile.length < 3) {
                    System.err.println("Überspringe ungültige Zeile: " + zeile);
                    continue;  // Zeile überspringen, nicht abbrechen
                }

                // Spalte 1: Krankenkasse (Key)
                String name = teile[0].trim();

                // Spalte 2: Gesamtbeitrag (mit % und Leerzeichen)
                String gesamtStr = teile[1].trim().replace("%", "").trim();

                // Spalte 3: Zusatzbeitrag (mit % und Leerzeichen)
                String zusatzStr = teile[2].trim().replace("%", "").trim();

                // Parsen
                try {
                    double gesamt = Double.parseDouble(gesamtStr);
                    double zusatz = Double.parseDouble(zusatzStr);

                    // KrankenkassenInfo Objekt erstellen
                    Abgabenrechner.KrankenkassenInfo info =
                            new Abgabenrechner.KrankenkassenInfo(gesamt, zusatz);

                    // In HashMap speichern
                    daten.put(name, info);

                } catch (NumberFormatException e) {
                    System.err.println("Ungültige Zahlen in Zeile: " + zeile);
                    // Zeile überspringen, nicht abbrechen
                }
            }
        }

        return daten;
    }

}  // ← Klasse endet hier