package ca.grasley.spaceshooter;

public class UsuarioRanking {
    private String userId;
    private String usuario;
    private int puntuacion;

    public UsuarioRanking() {}

    public UsuarioRanking(String userId, String usuario, int puntuacion) {
        this.userId = userId;
        this.usuario = usuario;
        this.puntuacion = puntuacion;
    }

    public String getUserId() { return userId; }
    public String getUsuario() { return usuario; }
    public int getPuntuacion() { return puntuacion; }
}
