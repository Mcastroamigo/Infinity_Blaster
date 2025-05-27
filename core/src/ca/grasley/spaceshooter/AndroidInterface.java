package ca.grasley.spaceshooter;

public interface AndroidInterface {
    void vibrate(int milliseconds);
    void goToMainPage();
    void savePoints(int points);  // <-- Nuevo método
}
