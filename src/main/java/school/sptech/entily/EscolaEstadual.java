package school.sptech.entily;

import school.sptech.dao.RegistroDAO;
import java.util.List;

public class EscolaEstadual extends Escola {

    public EscolaEstadual() {
        this.setTipoEscola_id(1); // ID 1 = Estadual
    }

    @Override
    public void gerarIndicadorMacro(RegistroDAO dao, int ano) {

        List<Double> nCn = dao.buscarNotasParaCalculo(this.getTipoEscola_id(), ano, "nota_cn");
        List<Double> nCh = dao.buscarNotasParaCalculo(this.getTipoEscola_id(), ano, "nota_ch");
        List<Double> nLp = dao.buscarNotasParaCalculo(this.getTipoEscola_id(), ano, "nota_lp");
        List<Double> nMt = dao.buscarNotasParaCalculo(this.getTipoEscola_id(), ano, "nota_mt");
        List<Double> nRed = dao.buscarNotasParaCalculo(this.getTipoEscola_id(), ano, "nota_red");

        dao.salvarEstatisticaMacro(ano, "ESTADUAL",
                calcularMedianaLista(nCn), calcularMedianaLista(nCh),
                calcularMedianaLista(nLp), calcularMedianaLista(nMt),
                calcularMedianaLista(nRed));
    }
}