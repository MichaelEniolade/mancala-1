package me.dacol.marco.mancala.preferences;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import me.dacol.marco.mancala.R;

public class PreferencesFragment extends PreferenceFragment {

    // Preferences Key
    public static final String KEY_PLAYER_NAME = "pref_key_player_name";
    public static final String KEY_PLAYER_ANIMATION = "pref_key_animation_on_off"; // false: Black, true: White

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load preference
        addPreferencesFromResource(R.layout.fragment_preference);
    }
}
