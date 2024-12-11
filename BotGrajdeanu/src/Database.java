import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
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

}
