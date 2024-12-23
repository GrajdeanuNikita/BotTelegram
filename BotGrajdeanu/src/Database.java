import java.sql.*;
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

    protected boolean salvaFilmDatabaseUtente(String userId, String film, boolean visto) {
        String colonna = visto ? "FilmVisti" : "FilmSalvati";
        boolean giusto= false;
        try (Connection conn = DriverManager.getConnection(DB_URL, User, Password)) {
            String query = "INSERT INTO UTENTE (idU, " + colonna + ") VALUES (?, ?) " +
                    "ON DUPLICATE KEY UPDATE " + colonna + " = CONCAT(IFNULL(" + colonna + ", ''), ',', ?)";
            PreparedStatement stmt = conn.prepareStatement(query);

            stmt.setInt(1, Integer.parseInt(userId));
            stmt.setString(2, film);
            stmt.setString(3, film);

            int rowsAffected = stmt.executeUpdate(); // Esegue la query
            giusto = rowsAffected > 0;
            System.out.println("Query eseguita con successo. Rows affected: " + rowsAffected);

        } catch (SQLException e) {
            System.err.println("Errore durante il salvataggio: " + e.getMessage());

            e.printStackTrace();
            giusto=false;

        }
        return giusto;
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


