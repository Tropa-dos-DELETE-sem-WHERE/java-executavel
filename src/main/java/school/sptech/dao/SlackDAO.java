package school.sptech.dao;

import school.sptech.service.SlackService;
import school.sptech.conexao.DataBaseConnection;

import java.sql.*;

public class SlackDAO {
    private final SlackService service;
    public SlackDAO() {
        this.service = new SlackService();
    }

    public void enviarAlertaMedianas() {
        try (Connection conexao = DataBaseConnection.getConexao()) {
            service.enviarAlertaMedianas(conexao);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enviarLogAcessoNaoEnviados() {
        try (Connection conexao = DataBaseConnection.getConexao()) {
            service.enviarLogAcessoNaoEnviados(conexao);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

