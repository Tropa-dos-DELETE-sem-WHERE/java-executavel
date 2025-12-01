package school.sptech.entily;

import java.time.LocalDateTime;

public class Logs {
    private Integer id;
    private String descricao;
    private LocalDateTime dataLog;
    private Status tipoLog; // Usa o Enum

    public Logs() {}

    public Logs(String descricao, Status tipoLog) {
        this.descricao = descricao;
        this.tipoLog = tipoLog;
        this.dataLog = LocalDateTime.now(); // Já pega a hora atual automaticamente
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataLog() {
        return dataLog;
    }

    public void setDataLog(LocalDateTime dataLog) {
        this.dataLog = dataLog;
    }

    public Status getTipoLog() {
        return tipoLog;
    }

    public void setTipoLog(Status tipoLog) {
        this.tipoLog = tipoLog;
    }

    @Override
    public String toString() {
        return "Log {" +
                "Hora=" + dataLog +
                ", Tipo=" + tipoLog +
                ", Msg='" + descricao + '\'' +
                '}';
    }
}