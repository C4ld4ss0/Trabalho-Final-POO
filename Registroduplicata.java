public class Registroduplicata {
    private String nomeArquivo;
    private int numeroLinha;
    private String identificadorRepetido;
    
    public Registroduplicata(String nomeArquivo, int numeroLinha, String identificadorRepetido) {
        this.nomeArquivo = nomeArquivo;
        this.numeroLinha = numeroLinha;
        this.identificadorRepetido = identificadorRepetido;
    }
//
    public String getNomeArquivo() { return nomeArquivo; }
    public int getNumeroLinha() { return numeroLinha; }
    public String getIdentificadorRepetido() { return identificadorRepetido; }

    @Override
    public String toString() {
        return String.format("Arquivo: %s, Linha: %d, Identificador Repetido: %s", 
        nomeArquivo, numeroLinha, identificadorRepetido);
    }
}