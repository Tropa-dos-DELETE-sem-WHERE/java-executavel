package school.sptech;

import school.sptech.conexao.AwsConnection;
import school.sptech.conexao.DataBaseConnection;
import school.sptech.dao.RegistroDAO;
import school.sptech.dao.SlackDAO;
import school.sptech.entily.*;
import school.sptech.service.LeituraArquivo;

import java.io.InputStream;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        // Variaveis para S3
        String bucket = "s3-bucket-educadata";
        String arquivo = "enem_completo_parte1.xlsx";

        try (Connection conn = DataBaseConnection.getConexao()) {
            System.out.println("Banco conectado!");

            // conectar e procurar os arquivos
            InputStream s3Stream = AwsConnection.getArquivo(bucket, arquivo);

            LeituraArquivo service = new LeituraArquivo(conn);
            service.processar(s3Stream);

            // Calcular Medianas
            RegistroDAO dao = new RegistroDAO(conn);
            int ano = 2024;

            Escola.gerarIndicadorBrasil(dao, ano);

            List<Escola> geradores = Arrays.asList(
                    new EscolaEstadual(), new EscolaMunicipal(),
                    new EscolaFederal(), new EscolaPrivada()
            );

            for (Escola gerador : geradores) {
                gerador.gerarIndicadorMacro(dao, ano);
            }

            System.out.println(">>> Processo Finalizado <<<");

        } catch (Exception e) {
            e.printStackTrace();
        }

        SlackDAO slack = new SlackDAO();

        slack.enviarAlertaMedianas();
        slack.enviarLogAcessoNaoEnviados();
    }
}