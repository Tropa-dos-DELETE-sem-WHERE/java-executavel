package school.sptech.entily;

public class Registro {
    private Integer idregistro;
    private Integer ano;
    private Double nota_cn;
    private Double nota_ch;
    private Double nota_lp;
    private Double nota_mt;
    private Double nota_red;
    private Integer escola_id;


    public Integer getIdregistro() {
        return idregistro;
    }

    public void setIdregistro(Integer idregistro) {
        this.idregistro = idregistro;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Double getNota_cn() {
        return nota_cn;
    }

    public void setNota_cn(Double nota_cn) {
        this.nota_cn = nota_cn;
    }

    public Double getNota_ch() {
        return nota_ch;
    }

    public void setNota_ch(Double nota_ch) {
        this.nota_ch = nota_ch;
    }

    public Double getNota_lp() {
        return nota_lp;
    }

    public void setNota_lp(Double nota_lp) {
        this.nota_lp = nota_lp;
    }

    public Double getNota_mt() {
        return nota_mt;
    }

    public void setNota_mt(Double nota_mt) {
        this.nota_mt = nota_mt;
    }

    public Double getNota_red() {
        return nota_red;
    }

    public void setNota_red(Double nota_red) {
        this.nota_red = nota_red;
    }

    public Integer getEscola_id() {
        return escola_id;
    }

    public void setEscola_id(Integer escola_id) {
        this.escola_id = escola_id;
    }

    @Override
    public String toString() {
        return "Registro{" +
                "idregistro=" + idregistro +
                ", ano=" + ano +
                ", nota_cn=" + nota_cn +
                ", nota_ch=" + nota_ch +
                ", nota_lp=" + nota_lp +
                ", nota_mt=" + nota_mt +
                ", nota_red=" + nota_red +
                ", escola_id=" + escola_id +
                '}';
    }
}
