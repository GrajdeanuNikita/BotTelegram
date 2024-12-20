import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;


public class Bot extends TelegramLongPollingBot {


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String TestoMessaggio = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if (TestoMessaggio.equalsIgnoreCase("/start")) {
                sendReplyWithInlineButtons(chatId,
                        "Benvenuto " + update.getMessage().getFrom().getFirstName() +
                                "!!  Che cosa vuoi fare oggi?",
                        List.of("Cerchiamo un film!", "Film Da vedere", "Film visti"));
            } else{
                switch (TestoMessaggio.toLowerCase()){
                    case "cerchiamo un film!":
                        sendReplyWithInlineButtons(chatId, "Che genere vuoi? " +
                                        "puoi scegliere tra: ",
                                List.of("Azione", "Avventura", "Animazione", "Biografico", "Commedia",
                                        "Poliziesco", "Documentario", "Drammatico", "Per famiglie",
                                        "Fantastico", "Noir", "Giochi a premi televisivo", "Storico",
                                        "Horror", "Musica", "Musical", "Giallo", "Telegiornale",
                                        "Reality", "Sentimentale", "Fantascienza", "Cortometraggio",
                                        "Sportivo", "Talk Show", "Thriller", "Guerra", "Western"));

                        break;
                    case "Film Da vedere":
                        //  MOSTREA DATABASE CON I FILM VISTIù
                        break;
                    case "Film visti":
                        //dabaste dei film visti
                        break;
                }
            }
        } else if (update.hasCallbackQuery()){
            String callbackData = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();

            switch (callbackData.toLowerCase()){
                case "cerchiamo un film!":
                    sendReplyWithInlineButtons(chatId, "Che genere vuoi? " +
                                    "puoi scegliere tra: ",
                            List.of("Azione", "Avventura", "Animazione", "Biografico", "Commedia",
                                    "Poliziesco", "Documentario", "Drammatico", "Per famiglie",
                                    "Fantastico", "Noir", "Giochi a premi televisivo", "Storico",
                                    "Horror", "Musica", "Musical", "Giallo", "Telegiornale",
                                    "Reality", "Sentimentale", "Fantascienza", "Cortometraggio",
                                    "Sportivo", "Talk Show", "Thriller", "Guerra", "Western"));

                    break;
                default:
                    FilmsuTelegram(chatId, callbackData);
                    break;
            }
        }
    }

    private void sendReplyWithInlineButtons(Long chatId, String testo, List<String> buttonLabels) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(testo);


        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (String label : buttonLabels) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(label);
            button.setCallbackData(label);

            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(button);
            keyboard.add(row);
        }

        inlineKeyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(inlineKeyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    private void sendReplyWithButtons(Long chatId, String testo, List<String> buttonLabels){
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(testo);
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        for (String label : buttonLabels) {
            KeyboardRow row = new KeyboardRow();
            row.add(new KeyboardButton(label));
            keyboard.add(row);
        }

        keyboardMarkup.setKeyboard(keyboard);
        keyboardMarkup.setResizeKeyboard(true);
        message.setReplyMarkup(keyboardMarkup);

        try {

            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }



    private static final Map<String, String> TRADUZIONE = Map.of(
            "Azione", "action",
            "Avventura", "Adventure",
            "Animazione", "Animation",
            "Biografico", "Biography",
            "Commedia", "Comedy",
            "Poliziesco", "crime",
            "Guerra", "War"

    );



    private void FilmsuTelegram(Long chatId, String genere){
        WebDriverManager.chromedriver().setup();
        ChromeOptions options= new ChromeOptions();
        WebDriver driver= new ChromeDriver(options);

        WebScraper scraper=new WebScraper(driver);
        try{
            String genereInglese = TRADUZIONE.getOrDefault(genere, genere);
            List<String> TitoloFilm =scraper.Trovafilm(genereInglese, 3);
            for(String titolo: TitoloFilm){
                MessagioDiScelta(chatId, titolo);
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }finally {
            driver.quit();
        }

    }

    private void MessagioDiScelta(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }



    @Override
    public String getBotUsername() {
        return "GrajdeanuFilmBot";
    }

    @Override
    public String getBotToken() {
        return "7966978319:AAHFr0mevvfmPpWGDzDjIih6s5UFSmeaQr4";
    }

}

