import io.github.bonigarcia.wdm.WebDriverManager;
import org.checkerframework.checker.units.qual.C;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.Scanner;


public class Main {
   public static void main(String[] args){
       WebDriverManager.chromedriver().setup();

       ChromeOptions options= new ChromeOptions();
       WebDriver driver= new ChromeDriver(options);

       Scanner scanner = new Scanner(System.in);
       System.out.print("Inserire il genere( action, comedy, drama): ");
       String genere = scanner.nextLine().toLowerCase();
       scanner.close();

       try {
           //Uso per il scaper
           WebScraper scraper= new WebScraper(driver);
           scraper.Trovafilm(genere, 3);
           //Creazione del bot
           TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
           telegramBotsApi.registerBot(new Bot());
           System.out.println("Bot avviato!");
       } catch (TelegramApiException | InterruptedException e) {
           e.printStackTrace();
       }finally {
           driver.quit();
       }
   }

}
