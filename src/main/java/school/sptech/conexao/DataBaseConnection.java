package school.sptech.conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {
    private static final String url = "jdbc:mysql://localhost:3306/educadata";
    private static final String usuario = "root";
    private static final String senha = "255225";


    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(url, usuario, senha);
    }
}
