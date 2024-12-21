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

    public List<String> Trovafilm(String genere, int pagine) throws InterruptedException{
        String url = "https://www.imdb.com/search/title/?title_type=feature&genres=" + genere;

        driver.get(url);
        System.out.println("\nFilm per genere: " + genere + "\n");

        int pageCount = 1;
        List<String> FILM= new ArrayList<>();

        while (pageCount <= pagine) {
            System.out.println("Page " + pageCount + ":");

            Thread.sleep(3000);


            List<WebElement> movieElements = driver.findElements(By.cssSelector("li.ipc-metadata-list-summary-item"));
            System.out.println("Ci sono  " + movieElements.size() + " film(s).");


            if (movieElements.isEmpty()) {
                System.out.println("No film .");
                break;
            }

            for (WebElement movie : movieElements) {

                try{

                    String titolo = movie.findElement(By.cssSelector("h3.ipc-title__text")).getText();

                    // Estrai la descrizione del film
                    String descrizione = movie.findElement(By.cssSelector("div.ipc-html-content-inner-div")).getText();

                    // Estrai l'anno di uscita
                    String anno = movie.findElement(By.cssSelector("span.sc-300a8231-7")).getText();

                    // Estrai la durata
                    String durata = movie.findElement(By.cssSelector("span.sc-300a8231-7")).getText();

                    // Recupera l'immagine (URL)
                    String immagine = movie.findElement(By.cssSelector("img.ipc-image")).getAttribute("src");


                    Film filmdati = new Film(titolo,anno,descrizione,durata,immagine);
                    FILM.add(filmdati.toString());

                }catch (Exception e){
                    System.out.println("Errore" );
                }
            }

            List<WebElement> nextButton = driver.findElements(By.cssSelector(".lister-page-next"));
            System.out.println("Found next button: " + nextButton.size());
            if (!nextButton.isEmpty()) {
                nextButton.get(0).click();
                pageCount++;
            } else {
                System.out.println("No pagine.");
                break;
            }
        }
        return FILM;
    }
}

