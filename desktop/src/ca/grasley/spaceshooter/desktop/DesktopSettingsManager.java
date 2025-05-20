package ca.grasley.spaceshooter.desktop;

import ca.grasley.spaceshooter.SettingsManager;

public class DesktopSettingsManager implements SettingsManager {
    @Override
    public boolean isVibrationEnabled() {
        return false; // No hay vibración en escritorio, así que devuelves false o una preferencia fija
    }
}
