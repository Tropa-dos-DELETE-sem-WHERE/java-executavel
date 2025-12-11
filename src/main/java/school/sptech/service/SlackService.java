package school.sptech.service;

import org.json.JSONObject;
import java.sql.*;
import java.net.http.*;
import java.net.URI;

public class SlackService {
    private static final HttpClient client = HttpClient.newHttpClient();


    public void enviarLogAcessoNaoEnviados(Connection conexao) throws Exception {
        // 1. Buscar logs não enviados
        String consultaLogs = "SELECT la.id, la.dataHora, u.nome, u.email, tu.tipo, u.escola_id " +
                "FROM logAcesso la " +
                "JOIN usuario u ON la.usuario_id = u.id " +
                "JOIN tipoUsuario tu ON u.tipoUsuario_id = tu.id " +
                "WHERE la.enviou = 0 " +
                "ORDER BY la.dataHora ASC";

        try (Statement comandoConsultaLogs = conexao.createStatement();
             ResultSet resultadoLogs = comandoConsultaLogs.executeQuery(consultaLogs)) {

            while (resultadoLogs.next()) {
                int logId = resultadoLogs.getInt("id");
                String nomeUsuario = resultadoLogs.getString("nome");
                String emailUsuario = resultadoLogs.getString("email");
                String tipoUsuario = resultadoLogs.getString("tipo").toLowerCase();
                int escolaId = resultadoLogs.getInt("escola_id");
                Timestamp dataHora = resultadoLogs.getTimestamp("dataHora");

                // 2. Só envia se for professor ou gestor
                if (!(tipoUsuario.equals("professor") || tipoUsuario.equals("gestor"))) {
                    continue;
                }

                // 3. Buscar webhook do Slack da escola
                String consultaSlack = "SELECT canal FROM slack " +
                        "WHERE escola_id = ? AND ligar_desligar = 'ligar' AND quer_logs = 1";
                try (PreparedStatement comandoConsultaSlack = conexao.prepareStatement(consultaSlack)) {
                    comandoConsultaSlack.setInt(1, escolaId);
                    try (ResultSet resultadoSlack = comandoConsultaSlack.executeQuery()) {
                        while (resultadoSlack.next()) {
                            String canalWebhook = resultadoSlack.getString("canal");

                            // 4. Montar mensagem
                            String mensagemSlack = "Log de acesso:\n" +
                                    "Usuário: " + nomeUsuario + " (" + tipoUsuario + ")\n" +
                                    "Email: " + emailUsuario + "\n" +
                                    "Data/Hora: " + dataHora + "\n" +
                                    "Escola ID: " + escolaId;

                            System.out.println("Mensagem para Slack:\n" + mensagemSlack);

                            // 5. Enviar para Slack
                            JSONObject payloadSlack = new JSONObject();
                            payloadSlack.put("text", mensagemSlack);

                            HttpRequest requisicaoSlack = HttpRequest.newBuilder()
                                    .uri(URI.create(canalWebhook))
                                    .header("Content-Type", "application/json")
                                    .POST(HttpRequest.BodyPublishers.ofString(payloadSlack.toString()))
                                    .build();

                            HttpResponse<String> respostaSlack = client.send(requisicaoSlack, HttpResponse.BodyHandlers.ofString());
                            System.out.println("Mensagem enviada para Slack: " + respostaSlack.statusCode());

                            // 6. Atualizar enviou = 1
                            if (respostaSlack.statusCode() == 200) {
                                String atualizacaoLog = "UPDATE logAcesso SET enviou = 1 WHERE id = ?";
                                try (PreparedStatement comandoAtualizacaoLog = conexao.prepareStatement(atualizacaoLog)) {
                                    comandoAtualizacaoLog.setInt(1, logId);
                                    comandoAtualizacaoLog.executeUpdate();
                                    System.out.println("Log " + logId + " marcado como enviado.");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private String trocaNome(String codigo) {
        switch (codigo) {
            case "CN": return "Ciências Naturais";
            case "CH": return "Ciências Humanas";
            case "LP": return "Língua Portuguesa";
            case "MT": return "Matemática";
            case "RED": return "Redação";
            default: return codigo;
        }
    }

    public void enviarAlertaMedianas(Connection conexao) throws Exception {
        // Buscar registros com nome e tipo da escola
        String consultaEscolas = "SELECT r.ano, e.nomeEscola, te.tipo AS tipoEscola, " +
                "r.nota_cn, r.nota_ch, r.nota_lp, r.nota_mt, r.nota_red, r.escola_id " +
                "FROM registro r " +
                "JOIN escola e ON r.escola_id = e.codigoEscola " +
                "JOIN tipoEscola te ON e.tipoEscola_id = te.id";

        System.out.println("Executando: " + consultaEscolas);

        try (Statement comandoConsultaEscolas = conexao.createStatement();
             ResultSet resultadoEscolas = comandoConsultaEscolas.executeQuery(consultaEscolas)) {

            while (resultadoEscolas.next()) {
                int ano = resultadoEscolas.getInt("ano");
                String nomeEscola = resultadoEscolas.getString("nomeEscola");
                String tipoEscola = resultadoEscolas.getString("tipoEscola").toUpperCase();
                double notaCn = resultadoEscolas.getDouble("nota_cn");
                double notaCh = resultadoEscolas.getDouble("nota_ch");
                double notaLp = resultadoEscolas.getDouble("nota_lp");
                double notaMt = resultadoEscolas.getDouble("nota_mt");
                double notaRed = resultadoEscolas.getDouble("nota_red");
                int escolaId = resultadoEscolas.getInt("escola_id");

                System.out.println("\n=== Escola encontrada ===");
                System.out.println("Ano: " + ano);
                System.out.println("Nome: " + nomeEscola);
                System.out.println("Tipo: " + tipoEscola);
                System.out.println("Notas -> CN:" + notaCn + " CH:" + notaCh + " LP:" + notaLp + " MT:" + notaMt + " RED:" + notaRed);

                // Buscar todas as medianas do ano
                String consultaCategorias = "SELECT categoria, mediana_cn, mediana_ch, mediana_lp, mediana_mt, mediana_red " +
                        "FROM estatistica_macro WHERE ano = ?";
                try (PreparedStatement comandoConsultaCategorias = conexao.prepareStatement(consultaCategorias)) {
                    comandoConsultaCategorias.setInt(1, ano);
                    try (ResultSet resultadoCategorias = comandoConsultaCategorias.executeQuery()) {
                        StringBuilder mensagemSlack = new StringBuilder("No ano " + ano + " a escola " + nomeEscola + " ficou abaixo da mediana:\n");

                        while (resultadoCategorias.next()) {
                            String categoria = resultadoCategorias.getString("categoria");
                            double medianaCn = resultadoCategorias.getDouble("mediana_cn");
                            double medianaCh = resultadoCategorias.getDouble("mediana_ch");
                            double medianaLp = resultadoCategorias.getDouble("mediana_lp");
                            double medianaMt = resultadoCategorias.getDouble("mediana_mt");
                            double medianaRed = resultadoCategorias.getDouble("mediana_red");

                            StringBuilder alertaCategoria = new StringBuilder();
                            if (notaCn < medianaCn) alertaCategoria.append(trocaNome("CN")).append(" (Escola: ").append(notaCn).append(" | ").append(categoria).append(": ").append(medianaCn).append(")\n");
                            if (notaCh < medianaCh) alertaCategoria.append(trocaNome("CH")).append(" (Escola: ").append(notaCh).append(" | ").append(categoria).append(": ").append(medianaCh).append(")\n");
                            if (notaLp < medianaLp) alertaCategoria.append(trocaNome("LP")).append(" (Escola: ").append(notaLp).append(" | ").append(categoria).append(": ").append(medianaLp).append(")\n");
                            if (notaMt < medianaMt) alertaCategoria.append(trocaNome("MT")).append(" (Escola: ").append(notaMt).append(" | ").append(categoria).append(": ").append(medianaMt).append(")\n");
                            if (notaRed < medianaRed) alertaCategoria.append(trocaNome("RED")).append(" (Escola: ").append(notaRed).append(" | ").append(categoria).append(": ").append(medianaRed).append(")\n");

                            if (alertaCategoria.length() > 0) {
                                mensagemSlack.append("\n➡ Comparação com ").append(categoria).append(":\n").append(alertaCategoria);
                            }
                        }

                        // Enviar para Slack se houver alertas
                        if (mensagemSlack.toString().contains("➡")) {
                            String consultaSlack = "SELECT canal, quer_mediana FROM slack WHERE escola_id = ? AND ligar_desligar = 'ligar'";
                            try (PreparedStatement comandoConsultaSlack = conexao.prepareStatement(consultaSlack)) {
                                comandoConsultaSlack.setInt(1, escolaId);
                                try (ResultSet resultadoSlack = comandoConsultaSlack.executeQuery()) {
                                    while (resultadoSlack.next()) {
                                        if (!resultadoSlack.getBoolean("quer_mediana")) continue;
                                        String canalWebhook = resultadoSlack.getString("canal");
                                        System.out.println("Webhook Slack encontrado: " + canalWebhook);

                                        JSONObject payloadSlack = new JSONObject();
                                        payloadSlack.put("text", mensagemSlack.toString());

                                        HttpRequest requisicaoSlack = HttpRequest.newBuilder()
                                                .uri(URI.create(canalWebhook))
                                                .header("Content-Type", "application/json")
                                                .POST(HttpRequest.BodyPublishers.ofString(payloadSlack.toString()))
                                                .build();

                                        HttpResponse<String> respostaSlack = client.send(requisicaoSlack, HttpResponse.BodyHandlers.ofString());
                                        System.out.println("Mensagem enviada para Slack: " + respostaSlack.statusCode());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
