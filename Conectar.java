import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conectar {
    private static final String URL = "jdbc:mysql://localhost:3006/sigu"; // Ajusta el puerto si usas otro que no sea 3306
    private static final String USER = "root";
    private static final String PASSWORD = "12345678"; // Coloca tu contraseña de phpMyAdmin si tiene

    public static Connection getConexion() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
        return con;
    }
}