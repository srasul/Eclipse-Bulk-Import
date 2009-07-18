
package listprojectplugin.preferences;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import listprojectplugin.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer
    extends AbstractPreferenceInitializer {

    /*
     * (non-Javadoc)
     * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
     */
    public void initializeDefaultPreferences() {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.setDefault(PreferenceConstants.SAVE_LIST_AT, PreferenceConstants.SHUTDOWN);

        // project list in workspace dir
        store.setDefault(PreferenceConstants.PROJECT_LIST_PATH, ResourcesPlugin.getWorkspace().getRoot().getLocation()
            .toFile().getAbsolutePath());
    }
}
