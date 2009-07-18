
package listprojectplugin.actions;

import listprojectplugin.Activator;
import listprojectplugin.preferences.PreferenceConstants;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class PluginStartup
    implements IStartup {

    public void earlyStartup() {

        final String saveTime = Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.SAVE_LIST_AT);

        final IWorkbench workbench = PlatformUI.getWorkbench();

        workbench.getDisplay().asyncExec(new Runnable() {
            public void run() {
                final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
                if(window != null) {

                    IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

                    // MessageDialog.openInformation(window.getShell(), "Bulk Import", workspaceRoot
                    // .getLocation().toFile().getAbsolutePath());

                    final Runnable r = new Runnable() {
                        public void run() {
                            SaveProjectAction a = new SaveProjectAction();
                            a.init(window);
                            a.run(null);
                        }
                    };

                    if(saveTime.equals(PreferenceConstants.STARTUP)) {
                        r.run();
                    }

                    workbench.addWorkbenchListener(new IWorkbenchListener() {
                        public void postShutdown(IWorkbench workbench) {}

                        public boolean preShutdown(IWorkbench workbench, boolean forced) {
                            String saveTime =
                                Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.SAVE_LIST_AT);
                            if(saveTime.equals(PreferenceConstants.SHUTDOWN)) {
                                r.run();
                            }
                            return true;
                        }
                    });

                }
            }

        });

    }
}
