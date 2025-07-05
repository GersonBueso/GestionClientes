import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL = "jdbc:mysql://localhost:3306/gestor_clientes?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = "root";       // Cambia si usas otro usuario
    private static final String CONTRASENA = "88624516";        // Cambia si tienes contraseña

    public static Connection conectar() {
        try {
            // Carga el driver JDBC de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Retorna la conexión
            return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Driver JDBC no encontrado: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("❌ Error al conectar a la base de datos: " + e.getMessage());
        }
        return null;
    }
}