package school.sptech.dao;

import school.sptech.entily.Escola;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EscolaDAO {
    private Connection conn;

    public EscolaDAO(Connection conn) {
        this.conn = conn;
    }

    public boolean salvar(Escola escola) {
        if (escola.getCodigoEscola() == null || escola.getCodigoEscola() == 0) return false;
        if (escola.getUF_id() == null || escola.getUF_id() == 0) return false;
        if (escola.getTipoEscola_id() == null || escola.getTipoEscola_id() == 0) return false;

        String sql = "INSERT IGNORE INTO escola (codigoEscola, nomeEscola, tipoEscola_id, UF_id) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, escola.getCodigoEscola());

            String nome = escola.getNomeEscola();
            if (nome != null && nome.length() > 100) nome = nome.substring(0, 100);

            ps.setString(2, nome);
            ps.setInt(3, escola.getTipoEscola_id());
            ps.setInt(4, escola.getUF_id());

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("Erro ao salvar escola " + escola.getCodigoEscola() + ": " + e.getMessage());
            return false;
        }
    }
}