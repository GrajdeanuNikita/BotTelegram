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
        String url = "https://www.imdb.com/search/title/?genres=" + genere;

        driver.get(url);
        System.out.println("\nFilm per genere: " + genere + "\n");

        int pageCount = 1;
        List<String> TitoloFilm= new ArrayList<>();

        while (pageCount <= pagine) {
            System.out.println("Page " + pageCount + ":");

            Thread.sleep(3000);


            List<WebElement> movieElements = driver.findElements(By.cssSelector(".ipc-title__text"));
            System.out.println("a " + movieElements.size() + " film(s).");


            if (movieElements.isEmpty()) {
                System.out.println("No film .");
                break;
            }

            for (WebElement movie : movieElements) {
                String titolo= movie.getText();
                System.out.println("Film trovato: " + movie.getText());
                TitoloFilm.add(titolo);
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
        return TitoloFilm;
    }
}

