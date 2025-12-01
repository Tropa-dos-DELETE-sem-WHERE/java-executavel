package school.sptech.dao;


import school.sptech.entily.Logs;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class LogsDAO {
    private Connection conn;

    public LogsDAO(Connection conn) {
        this.conn = conn;
    }

    public void salvar(Logs log) {
        String sql = "INSERT INTO logs (descricao, dataLog, tipoLog_id) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            // Trata descrição muito longa para não quebrar o banco
            String msg = log.getDescricao();
            if (msg != null && msg.length() > 255) {
                msg = msg.substring(0, 255);
            }

            ps.setString(1, msg);

            // Converte LocalDateTime para o formato do MySQL
            ps.setTimestamp(2, Timestamp.valueOf(log.getDataLog()));

            // Pega o ID (1, 2 ou 3) do Enum
            ps.setInt(3, log.getTipoLog().getId());

            ps.executeUpdate();
            System.out.println("Log registrado: " + log.getTipoLog());

        } catch (SQLException e) {
            // Caso de erro no Log
            System.err.println("Falha ao gravar log no banco: " + e.getMessage());
        }
    }
}