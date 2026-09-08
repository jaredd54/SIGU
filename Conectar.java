import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conectar {
    
    // Parámetros de conexión optimizados (UTF-8 y TimeZone de México)
    private static final String URL = "jdbc:mysql://localhost:3306/sigu"
            + "?useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&useUnicode=true"
            + "&characterEncoding=UTF-8"
            + "&serverTimezone=America/Mexico_City";
            
    private static final String USER = "root";
    private static final String PASSWORD = "12345678"; 

    /**
     * Obtiene una nueva conexión con la base de datos minuciosamente configurada.
     * @return Connection objeto de conexión activo o null en caso de error.
     */
    public static Connection getConexion() {
        Connection conexion = null;
        try {
            // Carga explícita del Driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            conexion = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Driver de MySQL no encontrado. Revisa tu archivo .jar (Connector/J)");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error de conexión SQL: " + e.getMessage());
            System.err.println("Código de error SQL: " + e.getErrorCode());
        }
        return conexion;
    }

    /**
     * Método utilitario para cerrar la conexión de forma segura.
     * @param conexion Objeto Connection que se va a cerrar.
     */
    public static void cerrarConexion(Connection conexion) {
        if (conexion != null) {
            try {
                if (!conexion.isClosed()) {
                    conexion.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}