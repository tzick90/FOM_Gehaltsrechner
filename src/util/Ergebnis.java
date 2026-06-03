package util;

public class Ergebnis {
    private double brutto;
    private double nettoMonat;
    private double lohnsteuerJahr;
    private double lohnsteuerMonat;
    private double kirchensteuerJahr;
    private double kirchensteuerMonat;
    private double soliJahr;
    private double soliMonat;
    private double kvBeitrag;
    private double kvZusatz;
    private double kvBasisBetrag;
    private double kvZusatzBetrag;
    private double rvBeitrag;
    private double avBeitrag;
    private double pvBeitrag;
    // Konstruktor – exakt 12 Parameter, exakt diese Reihenfolge
    public Ergebnis(
            double brutto,             // 1.
            double nettoMonat,         // 2.
            double lohnsteuerJahr,     // 3.
            double lohnsteuerMonat,    // 4.
            double kirchensteuerJahr,  // 5.
            double kirchensteuerMonat, // 6.
            double soliJahr,           // 7.
            double soliMonat,          // 8.
            double kvBeitrag,
            double kvZusatz,
            double kvBasisBetrag,      // 9.
            double kvZusatzBetrag,  // 9.
            double rvBeitrag,          // 10.
            double avBeitrag,          // 11.
            double pvBeitrag           // 12.
    ) {
        this.brutto             = brutto;
        this.nettoMonat         = nettoMonat;
        this.lohnsteuerJahr     = lohnsteuerJahr;
        this.lohnsteuerMonat    = lohnsteuerMonat;
        this.kirchensteuerJahr  = kirchensteuerJahr;
        this.kirchensteuerMonat = kirchensteuerMonat;
        this.soliJahr           = soliJahr;
        this.soliMonat          = soliMonat;
        this.kvBeitrag          = kvBeitrag;
        this.kvZusatz           = kvZusatz;
        this.kvBasisBetrag      = kvBasisBetrag;
        this.kvZusatzBetrag     = kvZusatzBetrag;
        this.rvBeitrag          = rvBeitrag;
        this.avBeitrag          = avBeitrag;
        this.pvBeitrag          = pvBeitrag;
    }
    // Getter für jeden Wert
    public double getBrutto()             { return brutto; }
    public double getNettoMonat()         { return nettoMonat; }
    public double getLohnsteuerJahr()     { return lohnsteuerJahr; }
    public double getLohnsteuerMonat()    { return lohnsteuerMonat; }
    public double getKirchensteuerJahr()  { return kirchensteuerJahr; }
    public double getKirchensteuerMonat() { return kirchensteuerMonat; }
    public double getSoliJahr()           { return soliJahr; }
    public double getSoliMonat()          { return soliMonat; }
    public double getKvBeitrag()          { return kvBeitrag; }
    public double getKvZusatz()           { return kvZusatz; }
    public double getKvBasisBetrag()      { return kvBasisBetrag; }
    public double getKvZusatzBetrag()     { return kvZusatzBetrag; }
    public double getRvBeitrag()          { return rvBeitrag; }
    public double getAvBeitrag()          { return avBeitrag; }
    public double getPvBeitrag()          { return pvBeitrag; }

}
