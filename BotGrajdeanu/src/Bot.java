import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
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
                BottoniPrincipali(chatId,
                        "Benvenuto " + update.getMessage().getFrom().getFirstName() +
                                "!!   Che cosa vuoi fare oggi? (puoi vedere quello che hai gia visto o quello che ti sei salvato)",
                        List.of("Cerchiamo un film!", "Film Da vedere", "Film visti"));
            } else if (testoMessaggio.equalsIgnoreCase("Cerchiamo un film!")) {
                // Mostra i generi come bottoni
                rispostaBottoni(chatId, "Che genere vuoi? Puoi scegliere tra:",
                        List.of("Azione", "Avventura", "Animazione", "Biografico", "Commedia",
                                "Poliziesco", "Documentario", "Drammatico", "Per famiglie",
                                "Fantastico", "Noir", "Giochi a premi televisivo", "Storico",
                                "Horror", "Musica", "Musical", "Giallo", "Telegiornale",
                                "Reality", "Sentimentale", "Fantascienza",
                                "Sportivo", "Talk Show", "Thriller", "Guerra", "Western"));
            } else if (testoMessaggio.startsWith("Elimina")) {
                String filmDaRimuovere = testoMessaggio.replace("Elimina ", "").trim();
                if (!filmDaRimuovere.isEmpty()) {
                    db.rimuoviFilmSalvato(chatId.toString(), filmDaRimuovere);
                    RISPOSTA(chatId, "Il film '" + filmDaRimuovere + "' è stato rimosso dalla tua lista dei film salvati.");
                } else {
                    RISPOSTA(chatId, "Non hai specificato un film da rimuovere. Usa il comando: Elimina [nome del film]");
                }


            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            String userId = update.getCallbackQuery().getFrom().getId().toString();

            if (isGenre(callbackData)) {
                FilmsuTelegram(chatId, callbackData);

            } else if (callbackData.equalsIgnoreCase("Cerchiamo un film!")) {
                rispostaBottoni(chatId, "Che genere vuoi? Puoi scegliere tra:",
                        List.of("Azione", "Avventura", "Animazione", "Biografico", "Commedia",
                                "Poliziesco", "Documentario", "Drammatico", "Per famiglie",
                                "Fantastico", "Noir", "Giochi a premi televisivo", "Storico",
                                "Horror", "Musica", "Musical", "Giallo", "Telegiornale",
                                "Reality", "Sentimentale", "Fantascienza",
                                "Sportivo", "Talk Show", "Thriller", "Guerra", "Western"));

            } else if (callbackData.startsWith("guarda")) {
                String encodedFilm = update.getCallbackQuery().getData().split(":", 2)[1];
                String film = URLDecoder.decode(encodedFilm, StandardCharsets.UTF_8);
                film = film.replaceFirst("^\\d+\\.\\s*", "");
                db.salvaFilmDatabaseUtente(userId, film, true);
                RISPOSTA(chatId, film + "segnato come visto");

            } else if (callbackData.startsWith("salva")) {
                String encodedFilm = callbackData.split(":", 2)[1];
                String film = URLDecoder.decode(encodedFilm, StandardCharsets.UTF_8);
                db.salvaFilmDatabaseUtente(userId, film, false);
                RISPOSTA(chatId, film + " salvato in guarda dopo.");

            } else if (callbackData.startsWith("Film Da")) {
                List<String> filmDaVedere = db.getFilmSalvatiDaVedere(userId);
                if (filmDaVedere.isEmpty()) {
                    RISPOSTA(chatId, "Non hai ancora film salvati da vedere.");
                } else {
                    String listaFilm = "Ecco i tuoi film da vedere:\n\n";
                    for (String film : filmDaVedere) {
                        listaFilm += film + "\n";
                    }
                    RISPOSTA(chatId, listaFilm);
                }
            } else if (callbackData.startsWith("Film visti")) {
                RISPOSTA(chatId,  "Se vuoi elimane dei film usa il comando Elimina [nomefilm]");

                List<String> filmVisti = db.getFilmVisti(userId);
                if (filmVisti.isEmpty()) {
                    RISPOSTA(chatId, "Non hai ancora film visti.");
                } else {
                    String listaFilm = "Ecco i tuoi film che fai visto :\n\n";
                    for (String film : filmVisti) {
                        listaFilm += film + "\n";
                    }
                    RISPOSTA(chatId, listaFilm);
                }
            }
        }
    }

    private void BottoniPrincipali(Long chatId, String testo, List<String> opzioni) {
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
                "Telegiornale", "Reality", "Sentimentale", "Fantascienza", "Sportivo",
                "Talk Show", "Thriller", "Guerra", "Western");

        return generi.contains(text);
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
            message.setText(film.getTitolo() +
                    "        " + "ANNO:  " +film.getAnno()+
                    "   DESCRIZIONE:    " +film.getDescrizione()+ "     "+
                    "    Durata:"+ film.getDurata() +film.getImmagine());

            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            String titolo= film.getTitolo();
            String tfilm = titolo.replaceFirst("^\\d+\\.\\s*", "");

            InlineKeyboardButton guardalo = new InlineKeyboardButton();
            guardalo.setText("Guarda: " + tfilm);
            guardalo.setCallbackData("guarda:" + tfilm);

            InlineKeyboardButton salva = new InlineKeyboardButton();
            salva.setText("Salva: " + tfilm);
            salva.setCallbackData("salva:" + tfilm);


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

