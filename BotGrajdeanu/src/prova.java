import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class prova {
    public static void main(String[] args) {
        String baseUrl = "https://www.imdb.com/search/title/?title_type=feature";
        int page = 1; // Partiamo dalla prima pagina
        boolean morePages = true;

        // Iniziamo il ciclo di scraping per ogni pagina
        while (morePages) {
            try {
                // Costruiamo l'URL per la paginazione, incrementando la pagina
                String url = baseUrl + "&start=" + (page - 1) * 50 + 1;

                // Effettua la connessione alla pagina
                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36")
                        .get();

                // Estraiamo i titoli dei film, anni e generi
                Elements movies = doc.select(".lister-item"); // Ogni film è contenuto in un elemento con la classe "lister-item"

                // Se non ci sono film nella pagina, fermiamo il ciclo
                if (movies.isEmpty()) {
                    morePages = false;
                }

                // Estraiamo e stampiamo le informazioni per ogni film
                System.out.println("\n--- Pagina " + page + " ---");
                for (Element movie : movies) {
                    String title = movie.select(".lister-item-header a").text(); // Titolo del film
                    String year = movie.select(".lister-item-year").text(); // Anno del film
                    String genre = movie.select(".genre").text(); // Genere del film

                    System.out.println("Titolo: " + title);
                    System.out.println("Anno: " + year);
                    System.out.println("Genere: " + genre);
                    System.out.println("------------");
                }

                page++; // Vai alla pagina successiva
            } catch (Exception e) {
                System.out.println("Errore durante l'accesso alla pagina: " + e.getMessage());
                break; // Se c'è un errore, fermiamo il ciclo
            }
        }
    }
}
