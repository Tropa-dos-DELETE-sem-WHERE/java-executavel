package school.sptech.dao;

import school.sptech.entily.Registro;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegistroDAO {

    private final Connection conn;

    public RegistroDAO(Connection conn) {
        this.conn = conn;
    }

    //  Inserção de dados
    public void salvarLote(List<Registro> registros) {
        if (registros == null || registros.isEmpty()) return;

        String sql = "INSERT INTO registro (ano, escola_id, nota_cn, nota_ch, nota_lp, nota_mt, nota_red) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            for (Registro reg : registros) {
                // Não salva se não tiver Ano ou Escola vinculada
                if (reg.getAno() == null || reg.getEscola_id() == null) continue;

                ps.setInt(1, reg.getAno());
                ps.setInt(2, reg.getEscola_id());

                // tratar valores Double que podem ser NULL
                setDecimal(ps, 3, reg.getNota_cn());
                setDecimal(ps, 4, reg.getNota_ch());
                setDecimal(ps, 5, reg.getNota_lp());
                setDecimal(ps, 6, reg.getNota_mt());
                setDecimal(ps, 7, reg.getNota_red());

                ps.addBatch(); // Adiciona ao pacote de envio
            }

            ps.executeBatch(); // Envia o pacote para o banco

        } catch (SQLException e) {
            System.err.println("Erro ao salvar lote de registros: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //  Calculo da mediana
    public List<Double> buscarNotasParaCalculo(Integer idTipoEscola, int ano, String colunaNome) {
        List<Double> notas = new ArrayList<>();
        StringBuilder sql = new StringBuilder();

        // Monta a query dinamicamente
        sql.append("SELECT r.").append(colunaNome).append(" FROM registro r ");

        // Se tem tipo, faz JOIN com escola para filtrar a modalidade
        if (idTipoEscola != null) {
            sql.append("JOIN escola e ON r.escola_id = e.codigoEscola ");
            sql.append("WHERE e.tipoEscola_id = ? AND r.ano = ? ");
        } else {
            // Se não tem tipo, é BRASIL (pega tudo daquele ano)
            sql.append("WHERE r.ano = ? ");
        }

        sql.append("AND r.").append(colunaNome).append(" IS NOT NULL"); // Ignora notas vazias

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            if (idTipoEscola != null) {
                ps.setInt(1, idTipoEscola);
                ps.setInt(2, ano);
            } else {
                ps.setInt(1, ano);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    notas.add(rs.getDouble(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notas;
    }

    //Inserção da mediana
    public void salvarEstatisticaMacro(int ano, String categoria,
                                       double cn, double ch, double lp, double mt, double red) {

        String sql = """
            INSERT INTO estatistica_macro (ano, categoria, mediana_cn, mediana_ch, mediana_lp, mediana_mt, mediana_red)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE 
                mediana_cn=VALUES(mediana_cn), mediana_ch=VALUES(mediana_ch), 
                mediana_lp=VALUES(mediana_lp), mediana_mt=VALUES(mediana_mt), mediana_red=VALUES(mediana_red)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ano);
            ps.setString(2, categoria);
            ps.setDouble(3, cn);
            ps.setDouble(4, ch);
            ps.setDouble(5, lp);
            ps.setDouble(6, mt);
            ps.setDouble(7, red);

            ps.executeUpdate();
            System.out.println("Estatística salva com sucesso: " + categoria);

        } catch (SQLException e) {
            System.err.println("Erro ao salvar estatística macro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setDecimal(PreparedStatement ps, int index, Double valor) throws SQLException {
        if (valor == null) {
            ps.setNull(index, Types.DECIMAL);
        } else {
            ps.setDouble(index, valor);
        }
    }
}