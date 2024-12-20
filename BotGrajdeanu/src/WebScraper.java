import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;


public class WebScraper {

    private WebDriver driver;

    public WebScraper(WebDriver driver){
        this.driver= driver;
    }

    public List<String> Trovafilm(String genere, int pagine) throws InterruptedException{
        String url = "https://www.imdb.com/search/title/?genres=" + genere;

        driver.get(url);
        System.out.println("\nFetching movies for genre: " + genere + "\n");

        int pageCount = 1;

        while (pageCount <= pagine) {
            System.out.println("Page " + pageCount + ":");

            Thread.sleep(3000);


            List<WebElement> movieElements = driver.findElements(By.cssSelector(".ipc-title__text"));
            System.out.println("Found " + movieElements.size() + " movie(s).");


            if (movieElements.isEmpty()) {
                System.out.println("No movies found on this page.");
                break;
            }

            for (WebElement movie : movieElements) {
                System.out.println("Found movie: " + movie.getText());
            }


            List<WebElement> nextButton = driver.findElements(By.cssSelector(".lister-page-next"));
            System.out.println("Found next button: " + nextButton.size());
            if (!nextButton.isEmpty()) {
                nextButton.get(0).click();
                pageCount++;
            } else {
                System.out.println("No more pages found.");
                break;
            }
        }
        return null;
    }
}

