import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;



public class Bot extends TelegramLongPollingBot {

    Database db=new Database();


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String testoMessaggio = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if (testoMessaggio.equalsIgnoreCase("/start")) {
                rispostaBottoni(chatId,
                        "Benvenuto " + update.getMessage().getFrom().getFirstName() +
                                "!! Che cosa vuoi fare oggi?",
                        List.of("Cerchiamo un film!", "Film Da vedere", "Film visti"));
            } else if (testoMessaggio.equalsIgnoreCase("Cerchiamo un film!")) {
                // Mostra i generi come bottoni
                rispostaBottoni(chatId, "Che genere vuoi? Puoi scegliere tra:",
                        List.of("Azione", "Avventura", "Animazione", "Biografico", "Commedia",
                                "Poliziesco", "Documentario", "Drammatico", "Per famiglie",
                                "Fantastico", "Noir", "Giochi a premi televisivo", "Storico",
                                "Horror", "Musica", "Musical", "Giallo", "Telegiornale",
                                "Reality", "Sentimentale", "Fantascienza", "Cortometraggio",
                                "Sportivo", "Talk Show", "Thriller", "Guerra", "Western"));
            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            String userId = update.getCallbackQuery().getFrom().getId().toString();

            System.out.println("Callback ricevuta: " + callbackData); // Debug


            if (isGenre(callbackData)) {
                // Avvia il web scraper per il genere selezionato
                FilmsuTelegram(chatId, callbackData);
            } else if (callbackData.equalsIgnoreCase("Cerchiamo un film!")) {
              rispostaBottoni(chatId, "Che genere vuoi? Puoi scegliere tra:",
                        List.of("Azione", "Avventura", "Animazione", "Biografico", "Commedia",
                                "Poliziesco", "Documentario", "Drammatico", "Per famiglie",
                                "Fantastico", "Noir", "Giochi a premi televisivo", "Storico",
                                "Horror", "Musica", "Musical", "Giallo", "Telegiornale",
                                "Reality", "Sentimentale", "Fantascienza", "Cortometraggio",
                                "Sportivo", "Talk Show", "Thriller", "Guerra", "Western"));
                
            } else if (callbackData.startsWith("guarda")) {
                String encodedFilm = callbackData.split(":", 2)[1];
                String film = URLDecoder.decode(encodedFilm, StandardCharsets.UTF_8);
                film = film.replaceFirst("^\\d+\\.\\s*", "");
                System.out.println("Film decodificato: " + film); // Debug
                System.out.println(userId);
               Boolean risultato= db.salvaFilmDatabaseUtente(userId, film, true);
                if (risultato) {
                    System.out.println(film + " è stato salvato correttamente nel database.");
                } else {
                    System.out.println( "Errore durante il salvataggio di " + film + " nel database.");
                }
                RISPOSTA(chatId, film + (risultato?  " segnato come visto " : " non è stato salvato."));
            } else if (callbackData.startsWith("salva")) {
                String encodedFilm = callbackData.split(":", 2)[1];
                String film = URLDecoder.decode(encodedFilm, StandardCharsets.UTF_8);

                db.salvaFilmDatabaseUtente(userId, film, false);
                RISPOSTA(chatId, film + " salvato in guarda dopo.");
            }
        }
    }

    private void RISPOSTA(Long chatId, String testo){
        SendMessage message= new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(testo);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private boolean isGenre(String text) {
        List<String> generi = List.of("Azione", "Avventura", "Animazione", "Biografico", "Commedia",
                "Poliziesco", "Documentario", "Drammatico", "Per famiglie", "Fantastico", "Noir",
                "Giochi a premi televisivo", "Storico", "Horror", "Musica", "Musical", "Giallo",
                "Telegiornale", "Reality", "Sentimentale", "Fantascienza", "Cortometraggio", "Sportivo",
                "Talk Show", "Thriller", "Guerra", "Western");

        return generi.contains(text); // Controlla se la callback è un genere
    }


    private void rispostaBottoni(Long chatId, String testo, List<String> opzioni) {
        SendMessage messaggio = new SendMessage();
        messaggio.setChatId(chatId.toString());
        messaggio.setText(testo);

        InlineKeyboardMarkup tastiera = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> righeTastiera = new ArrayList<>();

        for (String opzione : opzioni) {
            InlineKeyboardButton bottone = new InlineKeyboardButton();
            bottone.setText(opzione);
            bottone.setCallbackData(opzione);

            List<InlineKeyboardButton> riga = new ArrayList<>();
            riga.add(bottone);
            righeTastiera.add(riga);
        }

        tastiera.setKeyboard(righeTastiera);
        messaggio.setReplyMarkup(tastiera);

        try {
            execute(messaggio);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }





    private static final Map<String,String> TRADUZIONE;
        static {
            TRADUZIONE = new HashMap<>();
            TRADUZIONE.put("Azione", "action");
            TRADUZIONE.put("Avventura", "adventure");
            TRADUZIONE.put("Animazione", "animation");
            TRADUZIONE.put("Biografico", "biography");
            TRADUZIONE.put("Commedia", "comedy");
            TRADUZIONE.put("Poliziesco", "crime");
            TRADUZIONE.put("Drammatico", "drama");
            TRADUZIONE.put("Documentario", "documentary");
            TRADUZIONE.put("Per famiglie", "family");
            TRADUZIONE.put("Fantastico", "fantastic");
            TRADUZIONE.put("Noir", "film-noir");
            TRADUZIONE.put("Giochi a premi televisivi", "game-show");
            TRADUZIONE.put("Storico", "history");
            TRADUZIONE.put("Horror", "horror");
            TRADUZIONE.put("Musica", "music");
            TRADUZIONE.put("Musical", "musical");
            TRADUZIONE.put("Giallo", "mystery");
            TRADUZIONE.put("Telegiornale", "news");
            TRADUZIONE.put("Reality", "reality-tv");
            TRADUZIONE.put("Sentimentale", "romance");
            TRADUZIONE.put("Fantascienza", "sci-fi");
            TRADUZIONE.put("Cortometraggio", "short");
            TRADUZIONE.put("Sportivo", "sport");
            TRADUZIONE.put("Talkshow", "talk-show");
            TRADUZIONE.put("Thriller", "thriller");
            TRADUZIONE.put("Guerra", "war");
            TRADUZIONE.put("Western", "western");
        }


        private void FilmsuTelegram(Long chatId, String genere) {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            WebDriver driver = new ChromeDriver(options);

            WebScraper scraper = new WebScraper(driver);
            try {
                String genereInglese = TRADUZIONE.getOrDefault(genere, genere);
                List<Film> FILM = scraper.Trovafilm(genereInglese);

                for (Film film : FILM) {
                    MessagioDiScelta(chatId, film);

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                driver.quit();
            }

        }

    private void MessagioDiScelta(Long chatId, Film film) {
            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            message.setText("Titolo:" +film.getTitolo() +
                    "        " + "ANNO" +film.getAnno()+
                    "/n Descrizione: " +film.getDescrizione()+
                    "/n Durata:"+ film.getDurata() +film.getImmagine());

            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            String encodedfilm= URLEncoder.encode(film.getTitolo(), StandardCharsets.UTF_8);
            InlineKeyboardButton guardalo = new InlineKeyboardButton();
            guardalo.setText("Guarda: " + film.getTitolo());


            InlineKeyboardButton salva = new InlineKeyboardButton();
            salva.setText("Salva: " + film.getTitolo());

            guardalo.setCallbackData("guarda:" + encodedfilm);
            salva.setCallbackData("salva:" + encodedfilm);


            List<InlineKeyboardButton> row = new ArrayList<>();
            row.add(guardalo);
            row.add(salva);
            keyboard.add(row);

            inlineKeyboardMarkup.setKeyboard(keyboard);
            message.setReplyMarkup(inlineKeyboardMarkup);

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

