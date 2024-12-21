public class Film {
    private String titolo;
    private String anno;
    private String descrizione;
    private String durata;
    private String immagine;

    public Film(String titolo, String anno, String descrizione, String durata, String immagine) {
        this.titolo = titolo;
        this.anno = anno;
        this.descrizione = descrizione;
        this.durata = durata;
        this.immagine = immagine;
    }

    public String getTitolo() {
        return titolo;
    }

    public String getAnno() {
        return anno;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getDurata() {
        return durata;
    }

    public String getImmagine() {
        return immagine;
    }

    @Override
    public String toString() {
        return "Film{" +
                titolo +  '\'' + "       "+
                ", anno='" + anno + '\'' + "                 "+
                ", descrizione='" + descrizione + '\'' + "  "+
                ", durata='" + durata + '\'' +
                ", immagine='" + immagine + '\'' +
                '}';
    }
}