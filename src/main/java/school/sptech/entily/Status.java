package school.sptech.entily; // Mantendo o nome do seu pacote

public enum Status {
    ERRO(1),
    SUCESSO(2),
    AVISO(3);

    private final int id;

    Status(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}