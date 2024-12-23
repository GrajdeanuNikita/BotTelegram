import java.util.List;

public class Film {
    private String titolo;
    private String anno;
    private String descrizione;
    private String durata;
    private String immagine;
   // private List<String> cast;

    public Film(String titolo, String anno, String descrizione, String durata, String immagine) {
        this.titolo = titolo;
        this.anno = anno;
        this.descrizione = descrizione;
        this.durata = durata;
        this.immagine = immagine;
        //this.cast= cast;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public void setImmagine(String immagine) {
        this.immagine = immagine;
    }

    public void setDurata(String durata) {
        this.durata = durata;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public void setAnno(String anno) {
        this.anno = anno;
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

    /*
    public List<String> getCast() {
        return cast;
    }*/

    @Override
    public String toString() {
        return titolo +    "       "+
                " Anno:" + anno + '\'' + "                 "+
                " Descrizione:" + descrizione +"\n"  +
                " durata='" + durata +
                "Immagine : '" + immagine  ;
    }
}