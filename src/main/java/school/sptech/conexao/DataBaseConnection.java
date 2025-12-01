package school.sptech.conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnection {
    private static final String url = "jdbc:mysql://54.173.243.6:3306/educadata";
    private static final String usuario = "Caramico";
    private static final String senha = "urubu100";


    public static Connection getConexao() throws SQLException {
        return DriverManager.getConnection(url, usuario, senha);
    }
}
