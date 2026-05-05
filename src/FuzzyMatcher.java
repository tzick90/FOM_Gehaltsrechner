
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

    /*
     Generischer Fuzzy-Matcher für String-basierte Eingaben

     Unterstützt:
     - Case-insensitive Matching
     - Substring-Matching
     - Abkürzungen
     - Vorschläge bei Tippfehlern

     → Quelle: https://codingtechroom.com/question/-fuzzy-string-matching-java
     */



public class FuzzyMatcher {
    /**
     * Sucht einen passenden Key in einer Map mit Fuzzy-Matching
     *
     * @param <T>         Typ der Map-Values
     * @param eingabe     User-Eingabe (z.B. "TK", "Bayern", "bw")
     * @param daten       Map mit möglichen Werten
     * @param abkürzungen Optional: Map mit Abkürzungen → Volltext
     * @return Gefundener Key oder null
     */

    public static <T> String finde(
            String eingabe,
            Map<String, T> daten,
            Map<String, String> abkürzungen
    ) {
        if (eingabe == null || eingabe.trim().isEmpty()) {
            return null;
        }

        eingabe = eingabe.toLowerCase().trim();

        // 1. Annahme: Die Eingabe vom Anwender stimmt EXAKT überein (Case-Sensitive Match)
        for (String key : daten.keySet()) {
            if (key.toLowerCase().equals(eingabe)) {
                return key;
            }
        }

        // 2. Annahme: Es müssen Abkürzungen aufgelöst werden
        if (abkürzungen != null && abkürzungen.containsKey(eingabe)) {
            String aufgelöst = abkürzungen.get(eingabe);
            // Rekursiv mit aufgelöster Abkürzung suchen
            return finde(aufgelöst, daten, null);
        }

        // 3. Annahme: Es könnte ein Sub-String-Match vorliegen (#prüfen!)
        for (String key : daten.keySet()) {
            if (key.toLowerCase().contains(eingabe)) {
                return key;
            }
        }

        // 4. Annahme: Es liegt ein Reverse-Sub-String Match vor (#prüfen!)
        for (String key : daten.keySet()) {
            if (eingabe.contains(key.toLowerCase())) {
                return key;
            }
        }

        return null;
    }
    /*
     Überladung ohne Abkürzungen
     */
    public static <T> String finde(String eingabe, Map<String, T> daten) {
        return finde(eingabe, daten, null);
    }

    /**
     * Gibt Vorschläge bei fehlgeschlagener Suche,
     * findet ähnliche Strings basierend auf Anfangsbuchstaben
     *
     * @param <T> Typ der Map-Values
     * @param eingabe User-Eingabe
     * @param daten Map mit möglichen Werten
     * @param maxVorschläge Maximale Anzahl Vorschläge
     * @return Liste ähnlicher Keys
     */
    public static <T> List<String> gibVorschläge(
            String eingabe,
            Map<String, T> daten,
            int maxVorschläge
    ) {
        List<String> vorschläge = new ArrayList<>();

        if (eingabe == null || eingabe.trim().isEmpty()) {
            return vorschläge;
        }

        eingabe = eingabe.toLowerCase().trim();
        int minLength = Math.min(2, eingabe.length());
        String prefix = eingabe.substring(0, minLength);

        // Sammle Keys mit ähnlichen Anfangsbuchstaben
        for (String key : daten.keySet()) {
            if (key.toLowerCase().startsWith(prefix)) {
                vorschläge.add(key);
                if (vorschläge.size() >= maxVorschläge) {
                    break;
                }
            }
        }

        return vorschläge;
    }
        /**
         * Generiert automatisch Abkürzungen aus Map-Keys
         *
         * Erstellt Mappings für:
         * - Lowercase-Versionen
         * - Erste Buchstaben jedes Wortes (z.B. "AOK Bayern" -> "ab")
         * - Bekannte Abkürzungen (TK, DAK, etc.)
         *
         * @param <T> Typ der Map-Values
         * @param daten Ursprüngliche Map
         * @return Map mit automatisch generierten Abkürzungen
         */
        public static <T> Map<String, String> generiereAbkürzungen(Map<String, T> daten) {
            Map<String, String> abkürzungen = new java.util.HashMap<>();

            for (String key : daten.keySet()) {
                String keyLower = key.toLowerCase();

                // 1. Lowercase-Version
                abkürzungen.put(keyLower, key);

                // 2. Erste Buchstaben jedes Wortes (z.B. "AOK Bayern" -> "ab")
                String[] wörter = key.split("\\s+");
                if (wörter.length > 1) {
                    StringBuilder initiale = new StringBuilder();
                    for (String wort : wörter) {
                        if (!wort.isEmpty()) {
                            initiale.append(wort.charAt(0));
                        }
                    }
                    abkürzungen.put(initiale.toString().toLowerCase(), key);
                }

                // 3. Erstes Wort alleine (z.B. "Techniker Krankenkasse" -> "techniker")
                if (wörter.length > 0 && !wörter[0].isEmpty()) {
                    abkürzungen.put(wörter[0].toLowerCase(), key);
                }

                // 4. Bekannte Kurzformen extrahieren
                // z.B. "TK" aus "Techniker Krankenkasse (TK)"
                if (key.contains("(") && key.contains(")")) {
                    int start = key.indexOf("(") + 1;
                    int end = key.indexOf(")");
                    if (start < end) {
                        String kurzform = key.substring(start, end).trim();
                        abkürzungen.put(kurzform.toLowerCase(), key);
                    }
                }
            }

            return abkürzungen;
        }

        // Neue Methode: Finde alle passendes Key (nicht nur den ersten)

    /**
     * @param <T> Typ der Map-Values
     * @param eingabe User-Eingabe
     * @param daten Map mit möglichen Werten
     * @param abkürzungen Optional: Map mit Abkürzungen
     * @return Liste aller gefundenen Keys (leer wenn nichts gefunden)
     */

    public static <T> List<String> findeAlle(
            String eingabe,
            Map<String, T> daten,
            Map<String, String> abkürzungen
    ) {
        List<String> treffer = new ArrayList<>();

        if (eingabe == null || eingabe.trim().isEmpty()) {
            return treffer;
        }

        eingabe = eingabe.toLowerCase().trim();

        // 1. Exakte Übereinstimmung (case-insensitive)
        for (String key : daten.keySet()) {
            if (key.toLowerCase().equals(eingabe)) {
                treffer.add(key);
                return treffer;  // Bei exakter Übereinstimmung nur diesen zurückgeben
            }
        }

        // 2. Abkürzungen auflösen
        String aufgelöst = eingabe;
        if (abkürzungen != null && abkürzungen.containsKey(eingabe)) {
            aufgelöst = abkürzungen.get(eingabe).toLowerCase();
        }

        // 3. Substring-Match (Key enthält Eingabe) - ALLE sammeln
        for (String key : daten.keySet()) {
            if (key.toLowerCase().contains(aufgelöst)) {
                treffer.add(key);
            }
        }

        // 4. Falls nichts gefunden: Reverse Substring
        if (treffer.isEmpty()) {
            for (String key : daten.keySet()) {
                if (aufgelöst.contains(key.toLowerCase())) {
                    treffer.add(key);
                }
            }
        }

        return treffer;
    }

}





