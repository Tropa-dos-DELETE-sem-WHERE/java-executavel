package school.sptech.entily;

import school.sptech.dao.RegistroDAO;
import java.util.List;

public class EscolaFederal extends Escola {

    public EscolaFederal() {
        this.setTipoEscola_id(3); // ID 3 = Federal
    }

    @Override
    public void gerarIndicadorMacro(RegistroDAO dao, int ano) {

        List<Double> nCn = dao.buscarNotasParaCalculo(this.getTipoEscola_id(), ano, "nota_cn");
        List<Double> nCh = dao.buscarNotasParaCalculo(this.getTipoEscola_id(), ano, "nota_ch");
        List<Double> nLp = dao.buscarNotasParaCalculo(this.getTipoEscola_id(), ano, "nota_lp");
        List<Double> nMt = dao.buscarNotasParaCalculo(this.getTipoEscola_id(), ano, "nota_mt");
        List<Double> nRed = dao.buscarNotasParaCalculo(this.getTipoEscola_id(), ano, "nota_red");

        double medCn = calcularMedianaLista(nCn);
        double medCh = calcularMedianaLista(nCh);
        double medLp = calcularMedianaLista(nLp);
        double medMt = calcularMedianaLista(nMt);
        double medRed = calcularMedianaLista(nRed);

        dao.salvarEstatisticaMacro(ano, "FEDERAL", medCn, medCh, medLp, medMt, medRed);
    }
}