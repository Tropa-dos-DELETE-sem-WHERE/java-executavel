package school.sptech.service;

import com.monitorjbl.xlsx.StreamingReader;
import org.apache.poi.ss.usermodel.*;
import school.sptech.dao.EscolaDAO;
import school.sptech.dao.LogsDAO;
import school.sptech.dao.RegistroDAO;
import school.sptech.entily.*;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LeituraArquivo {

    private final Connection connection;
    private final EscolaDAO escolaDAO;
    private final RegistroDAO registroDAO;
    private final LogsDAO logsDAO;

    public LeituraArquivo(Connection conn) {
        this.connection = conn;
        this.escolaDAO = new EscolaDAO(conn);
        this.registroDAO = new RegistroDAO(conn);
        this.logsDAO = new LogsDAO(conn);
    }

    public void processar(InputStream inputStream) {
        System.out.println("Iniciando processamento via Stream...");
        logsDAO.salvar(new Logs("Iniciando leitura via Stream", Status.AVISO));

        try {

            connection.setAutoCommit(false);

            try (Workbook workbook = StreamingReader.builder()
                    .rowCacheSize(100)
                    .bufferSize(4096)
                    .open(inputStream)) {

                List<Registro> loteRegistros = new ArrayList<>();
                int contadorLinhas = 0;

                for (Row row : workbook.getSheetAt(0)) {
                    if (row.getRowNum() == 0) continue;

                    String codigoStr = getTexto(row, 1);
                    if (codigoStr.isEmpty() || codigoStr.equals("0")) continue;

                    int codigoEscola = Integer.parseInt(codigoStr);
                    int idUf = getUfId(getTexto(row, 33));
                    int idTipo = getTipoId(getTexto(row, 38));


                    boolean escolaSalva = false;
                    Escola escola = null;

                    switch (idTipo) {
                        case 1: escola = new EscolaEstadual(); break;
                        case 2: escola = new EscolaMunicipal(); break;
                        case 3: escola = new EscolaFederal(); break;
                        case 4: escola = new EscolaPrivada(); break;
                        default: continue;
                    }

                    if (escola != null && idUf != 0) {
                        escola.setCodigoEscola(codigoEscola);
                        escola.setNomeEscola(getTexto(row, 37));
                        escola.setUF_id(idUf);
                        escola.setTipoEscola_id(idTipo);

                        escolaSalva = escolaDAO.salvar(escola);
                    }

                    if (!escolaSalva) continue;

                    // --- REGISTRO ---
                    Double nCn = getNota(row, 10);
                    Double nCh = getNota(row, 11);
                    Double nLp = getNota(row, 12);
                    Double nMt = getNota(row, 13);
                    Double nRed = getNota(row, 29);

                    if (nCn == null && nCh == null && nLp == null && nMt == null) continue;

                    Registro reg = new Registro();
                    reg.setAno(parseIntSeguro(getTexto(row, 0)));
                    reg.setEscola_id(codigoEscola);
                    reg.setNota_cn(nCn);
                    reg.setNota_ch(nCh);
                    reg.setNota_lp(nLp);
                    reg.setNota_mt(nMt);
                    reg.setNota_red(nRed);

                    loteRegistros.add(reg);
                    contadorLinhas++;

                    if (loteRegistros.size() >= 1000) {
                        registroDAO.salvarLote(loteRegistros);
                        loteRegistros.clear();
                        System.out.println("Processados: " + contadorLinhas + "...");

                        connection.commit();
                    }
                }

                if (!loteRegistros.isEmpty()) {
                    registroDAO.salvarLote(loteRegistros);
                }


                connection.commit();

                String msgFim = "Sucesso! Total inserido: " + contadorLinhas;
                System.out.println(msgFim);
                logsDAO.salvar(new Logs(msgFim, Status.SUCESSO));
                connection.commit();

            } catch (Exception e) {

                connection.rollback();
                e.printStackTrace();
                String erro = "Erro fatal: " + e.getMessage();
                System.err.println(erro);

                try {
                    logsDAO.salvar(new Logs(erro, Status.ERRO));
                    connection.commit();
                } catch (Exception logEx) { }
            } finally {
                connection.setAutoCommit(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getTexto(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) return "";
        if (cell.getCellType() == CellType.STRING) return cell.getStringCellValue().trim();
        if (cell.getCellType() == CellType.NUMERIC) return String.valueOf((long) cell.getNumericCellValue());
        return cell.toString().trim().replace(".0", "");
    }

    private Double getNota(Row row, int index) {
        String valor = getTexto(row, index);
        if (valor.isEmpty()) return null;
        try {
            return Double.parseDouble(valor.replace(",", "."));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer parseIntSeguro(String val) {
        try { return Integer.parseInt(val.replace(".0", "")); } catch (Exception e) { return null; }
    }

    private int getUfId(String uf) {
        if (uf == null) return 0;
        switch (uf.toUpperCase()) {
            case "RO": return 11; case "AC": return 12; case "AM": return 13; case "RR": return 14;
            case "PA": return 15; case "AP": return 16; case "TO": return 17; case "MA": return 21;
            case "PI": return 22; case "CE": return 23; case "RN": return 24; case "PB": return 25;
            case "PE": return 26; case "AL": return 27; case "SE": return 28; case "BA": return 29;
            case "MG": return 31; case "ES": return 32; case "RJ": return 33; case "SP": return 35;
            case "PR": return 41; case "SC": return 42; case "RS": return 43; case "MS": return 50;
            case "MT": return 51; case "GO": return 52; case "DF": return 53;
            default: return 0;
        }
    }

    private int getTipoId(String tipoStr) {
        String limpo = tipoStr.replace(".0", "").trim();
        switch (limpo) {
            case "1": return 3; case "2": return 1; case "3": return 2; case "4": return 4; default: return 0;
        }
    }
}