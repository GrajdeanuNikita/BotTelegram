import io.github.bonigarcia.wdm.WebDriverManager;
import org.checkerframework.checker.units.qual.C;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;




public class Main {
   public static void main(String[] args){
       WebDriverManager.chromedriver().setup();
       Database db = new Database();
       if (!db.verificaConnessioneDatabase()) {
           return;
       }
       try {

           //Creazione del bot
           TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
           telegramBotsApi.registerBot(new Bot());
           System.out.println("Bot avviato!");
       } catch (TelegramApiException e) {
           e.printStackTrace();
       }
   }

}
