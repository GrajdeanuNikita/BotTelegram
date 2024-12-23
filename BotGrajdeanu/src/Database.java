import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.PatternSyntaxException;

public class Database {
    private static final String DB_URL= "jdbc:mysql://localhost:3306/filmdb" ;
    private static final String User= "root";
    private static final String Password= "";

    public static void main(String[] args){
        try(Connection connection= DriverManager.getConnection(DB_URL, User, Password)){
            System.out.println("Connsssione del database, riuscita!");
        }catch (SQLException e){
            System.err.println("Connessione del database, rifiutata dovuta al errore:" + e.getMessage());
        }
    }

    protected void salvaFilmDatabaseUtente(String userId, String film, boolean filmVisto) {
        String colonna = filmVisto ? "FilmVisti" : "FilmSalvati";
        System.out.println("Salvataggio film: " + film + " per l'utente: " + userId);
        try (Connection conn = DriverManager.getConnection(DB_URL, User, Password)) {
            String query = "INSERT INTO UTENTE (idU, " + colonna + ") VALUES (?, ?) " +
                    "ON DUPLICATE KEY UPDATE " + colonna + " = CONCAT(IFNULL(" + colonna + ", ''), ',', ?)";


            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setLong(1, Long.parseLong(userId));
            stmt.setString(2, film);
            stmt.setString(3, film);

            System.out.println("Esecuzione query: " + query);
            System.out.println("Parametri: idU=" + userId + ", film=" + film);

            int rowsAffected = stmt.executeUpdate(); // Esegue la query
            System.out.println("Query eseguita con successo. Rows affected: " + rowsAffected);
        } catch (SQLException e) {
            System.err.println("Errore SQL: " + e.getSQLState());
            System.err.println("Messaggio: " + e.getMessage());
            System.err.println("Errore durante il salvataggio: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<String> getFilmSalvatiDaVedere(String userId) {
        List<String> filmSalvati = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection(DB_URL, User, Password);
            String query = "SELECT FilmSalvati FROM UTENTE WHERE idU = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setLong(1, Long.parseLong(userId));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String filmSalvatiString = rs.getString("FilmSalvati");
                if (filmSalvatiString != null && !filmSalvatiString.isEmpty()) {
                    String[] films = filmSalvatiString.split(",");
                    for (String film : films) {
                        filmSalvati.add(film.trim());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return filmSalvati;
    }
    protected void rimuoviFilmSalvato(String userId, String film) {
        try (Connection conn = DriverManager.getConnection(DB_URL, User, Password)) {
            String querySelect = "SELECT FilmSalvati FROM UTENTE WHERE idU = ?";
            PreparedStatement stmtSelect = conn.prepareStatement(querySelect);
            stmtSelect.setLong(1, Long.parseLong(userId));
            ResultSet rs = stmtSelect.executeQuery();

            if (rs.next()) {
                String filmSalvatiString = rs.getString("FilmSalvati");
                if (filmSalvatiString != null && !filmSalvatiString.isEmpty()) {
                    String[] films = filmSalvatiString.split(",");
                    StringBuilder newFilmList = new StringBuilder();

                    for (String f : films) {
                        if (!f.trim().equals(film)) {
                            if (newFilmList.length() > 0) {
                                newFilmList.append(",");
                            }
                            newFilmList.append(f.trim());
                        }
                    }

                    String queryUpdate = "UPDATE UTENTE SET FilmSalvati = ? WHERE idU = ?";
                    PreparedStatement stmtUpdate = conn.prepareStatement(queryUpdate);

                    if (newFilmList.length() > 0) {
                        stmtUpdate.setString(1, newFilmList.toString());
                    } else {
                        stmtUpdate.setNull(1, Types.VARCHAR);
                    }

                    stmtUpdate.setLong(2, Long.parseLong(userId));
                    stmtUpdate.executeUpdate();
                    System.out.println("Film rimosso correttamente!");
                } else {
                    System.out.println("Nessun film salvato per questo utente.");
                }
            } else {
                System.out.println("Utente non trovato.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Errore durante la rimozione del film: " + e.getMessage());
        }
    }


    public List<String> getFilmVisti(String userId) {
        List<String> filmSalvati = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection(DB_URL, User, Password);
            String query = "SELECT FilmVisti FROM UTENTE WHERE idU = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setLong(1, Long.parseLong(userId));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String filmSalvatiString = rs.getString("FilmVisti");
                if (filmSalvatiString != null && !filmSalvatiString.isEmpty()) {
                    String[] films = filmSalvatiString.split(",");
                    for (String film : films) {
                        filmSalvati.add(film.trim());
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return filmSalvati;
    }


    protected void salvaFilm(String id, String nome, int anno, String durata, int idCast, int idG) {
            try (Connection conn = DriverManager.getConnection(DB_URL, User, Password)) {
                String query = "INSERT INTO FILM (id, Nome, Anno, Durata, idCast, idG) " +
                        "VALUES (?, ?, ?, ?, ?, ?) " +
                        "ON DUPLICATE KEY UPDATE Nome = VALUES(Nome), Anno = VALUES(Anno), Durata = VALUES(Durata), " +
                        "idCast = VALUES(idCast), idG = VALUES(idG)";
                PreparedStatement stmt = conn.prepareStatement(query);

                stmt.setInt(1, Integer.parseInt(id));
                stmt.setString(2, nome);
                stmt.setInt(3, anno);
                stmt.setString(4, durata);
                stmt.setInt(5, idCast);
                stmt.setInt(6, idG);

                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }

    static boolean verificaConnessioneDatabase() {
        try (Connection connection = DriverManager.getConnection(DB_URL, User, Password)) {
            System.out.println("Connessione al database riuscita!");
            return true;
        } catch (SQLException e) {
            System.err.println("Connessione al database fallita: " + e.getMessage());
            return false;
        }
    }

}


