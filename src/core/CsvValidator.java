package core;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class CsvValidator {

    /**
     * Prüft CSVs im "Typ,Jahr,Wert,Beschreibung"-Format
     * (sozialversicherung_saetze, einkommensteuer_grenzen, pauschalen, steuer_saetze)
     */
    public static String pruefeMitJahrCsv(String pfad) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(pfad), StandardCharsets.UTF_8))) {
            br.readLine(); // Header
            String zeile = br.readLine();
            if (zeile == null) return "Datei ist leer";

            String[] teile = zeile.split(",");
            if (teile.length < 3) return "Falsches Format (erwartet: Typ, Jahr, Wert, ...)";

            try {
                int jahr = Integer.parseInt(teile[1].trim());
                if (jahr < 2000 || jahr > 2100) return "Spalte 2 enthält kein gültiges Jahr";
            } catch (NumberFormatException e) {
                return "Spalte 2 enthält kein gültiges Jahr (gefunden: \"" + teile[1].trim() + "\")";
            }
            return null; // OK

        } catch (IOException e) {
            return "Datei nicht lesbar (" + e.getMessage() + ")";
        }
    }

    /**
     * Prüft Krankenkassen-CSV: "Krankenkasse, Jahr, Beitragssatz%, Zusatzbeitrag%"
     */
    public static String pruefeKrankenkassenCsv(String pfad) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(pfad), StandardCharsets.UTF_8))) {
            br.readLine(); // Header
            String zeile = br.readLine();
            if (zeile == null) return "Datei ist leer";

            String[] teile = zeile.split("[,\t]+");
            if (teile.length < 4) return "Falsches Format (erwartet: Krankenkasse, Jahr, Satz%, Zusatz%)";

            try {
                Integer.parseInt(teile[1].trim());
            } catch (NumberFormatException e) {
                return "Spalte 2 enthält kein gültiges Jahr";
            }

            if (!teile[2].contains("%") || !teile[3].contains("%")) {
                return "Keine Prozentwerte in Spalte 3/4 gefunden – falsche Datei?";
            }
            return null; // OK

        } catch (IOException e) {
            return "Datei nicht lesbar (" + e.getMessage() + ")";
        }
    }

    /**
     * Prüft Bundesländer-CSV: "Bundesland, Jahr, Kirchensteuer, Region"
     */
    public static String pruefeBundeslaenderCsv(String pfad) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(pfad), StandardCharsets.UTF_8))) {
            br.readLine(); // Header
            String zeile = br.readLine();
            if (zeile == null) return "Datei ist leer";

            String[] teile = zeile.split(",");
            if (teile.length < 4) return "Falsches Format (erwartet: Bundesland, Jahr, Kirchensteuer, Region)";

            try {
                Integer.parseInt(teile[1].trim());
            } catch (NumberFormatException e) {
                return "Spalte 2 enthält kein gültiges Jahr";
            }
            return null; // OK

        } catch (IOException e) {
            return "Datei nicht lesbar (" + e.getMessage() + ")";
        }
    }

}
