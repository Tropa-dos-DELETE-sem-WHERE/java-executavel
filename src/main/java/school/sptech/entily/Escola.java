package school.sptech.entily;

import school.sptech.dao.RegistroDAO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public abstract class Escola {

    private Integer codigoEscola;
    private String nomeEscola;
    private Integer tipoEscola_id;
    private Integer UF_id;

    public abstract void gerarIndicadorMacro(RegistroDAO dao, int ano);

    public Double calcularMedianaLista(List<Double> notas) {
        return calcularMedianaEstatica(notas);
    }

    public static void gerarIndicadorBrasil(RegistroDAO dao, int ano) {
        System.out.println("Gerando estatísticas para: BRASIL...");

        List<Double> nCn = dao.buscarNotasParaCalculo(null, ano, "nota_cn");
        List<Double> nCh = dao.buscarNotasParaCalculo(null, ano, "nota_ch");
        List<Double> nLp = dao.buscarNotasParaCalculo(null, ano, "nota_lp");
        List<Double> nMt = dao.buscarNotasParaCalculo(null, ano, "nota_mt");
        List<Double> nRed = dao.buscarNotasParaCalculo(null, ano, "nota_red");

        double medCn = calcularMedianaEstatica(nCn);
        double medCh = calcularMedianaEstatica(nCh);
        double medLp = calcularMedianaEstatica(nLp);
        double medMt = calcularMedianaEstatica(nMt);
        double medRed = calcularMedianaEstatica(nRed);

        dao.salvarEstatisticaMacro(ano, "BRASIL", medCn, medCh, medLp, medMt, medRed);
    }

    public static Double calcularMedianaEstatica(List<Double> notas) {
        if (notas == null || notas.isEmpty()) return 0.0;

        List<Double> lista = new ArrayList<>(notas);
        Collections.sort(lista);

        int meio = lista.size() / 2;
        if (lista.size() % 2 == 1) {
            return lista.get(meio);
        } else {
            return (lista.get(meio - 1) + lista.get(meio)) / 2.0;
        }
    }

    public Integer getCodigoEscola() {
        return codigoEscola;
    }

    public void setCodigoEscola(Integer codigoEscola) {
        this.codigoEscola = codigoEscola;
    }

    public String getNomeEscola() {
        return nomeEscola;
    }

    public void setNomeEscola(String nomeEscola) {
        this.nomeEscola = nomeEscola;
    }

    public Integer getTipoEscola_id() {
        return tipoEscola_id;
    }

    public void setTipoEscola_id(Integer tipoEscola_id) {
        this.tipoEscola_id = tipoEscola_id;
    }

    public Integer getUF_id() {
        return UF_id;
    }

    public void setUF_id(Integer UF_id) {
        this.UF_id = UF_id;
    }
}