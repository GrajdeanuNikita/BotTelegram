import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;


public class WebScraper {

    private WebDriver driver;

    public WebScraper(WebDriver driver){
        this.driver= driver;
    }

    public List<Film> Trovafilm(String genere) throws InterruptedException {
        String url = "https://www.imdb.com/search/title/?title_type=feature&genres=" + genere;

        driver.get(url);
        System.out.println("\nFilm per genere: " + genere + "\n");

        List<Film> FILM = new ArrayList<>();

            Thread.sleep(3000);

            List<WebElement> movieElements = driver.findElements(By.cssSelector("li.ipc-metadata-list-summary-item"));
            System.out.println("Ci sono  " + movieElements.size() + " film(s).");


            if (movieElements.isEmpty()) {
                System.out.println("Nessun film trovato .");
            }else{
                for (WebElement movie : movieElements) {

                try {

                    String titolo = movie.findElement(By.cssSelector("h3.ipc-title__text")).getText();
                    String descrizione = movie.findElement(By.cssSelector("div.ipc-html-content-inner-div")).getText();
                    String anno = movie.findElement(By.cssSelector("span.sc-300a8231-7")).getText();
                    String durata = movie.findElement(By.cssSelector("span.sc-300a8231-7")).getText();
                    String immagine = movie.findElement(By.cssSelector("img.ipc-image")).getAttribute("src");
                   // String filmUrl = movie.findElement(By.cssSelector("h3.ipc-title__text")).getAttribute("href");
                   // List<String> cast = OttieniCast(filmUrl);

                    Film filmdati = new Film(titolo, anno, descrizione, durata, immagine);
                    FILM.add(filmdati);
                    System.out.println("Film trovato: " + filmdati.getTitolo());
                    System.out.println("Dati durata: " + filmdati.getDurata());
                    System.out.println("dato anno: " + filmdati.getAnno());
                } catch (Exception e) {
                    System.out.println("Errore" + e );
                }
            }
        }
        return FILM;
    }

    /*
    public List<String> OttieniCast(String urlFilm) throws InterruptedException {
        driver.get(urlFilm);
        System.out.println("Estraendo i dati del cast per il film: " + urlFilm);


        List<String> cast = new ArrayList<>();


        Thread.sleep(3000);

        try {

            List<WebElement> castElements = driver.findElements(By.cssSelector("a[data-testid='title-cast-item__actor']"));

            for (WebElement actor : castElements) {
                String actorName = actor.getText();
                cast.add(actorName);
                System.out.println("Attore: " + actorName);
            }
        } catch (Exception e) {
            System.out.println("Errore nell'estrazione del cast: " + e.getMessage());
        }

        return cast;
    }*/
}

