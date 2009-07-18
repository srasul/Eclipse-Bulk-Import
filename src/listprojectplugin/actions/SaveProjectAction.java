
package listprojectplugin.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import listprojectplugin.Activator;
import listprojectplugin.preferences.PreferenceConstants;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

public class SaveProjectAction
    implements IWorkbenchWindowActionDelegate {

    private IWorkbenchWindow window;

    public void dispose() {
    // TODO Auto-generated method stub

    }

    public void init(IWorkbenchWindow window) {
        this.window = window;
    }

    public void run(IAction action) {

        IWorkspaceRoot myWorkspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        final IProject[] projects = myWorkspaceRoot.getProjects();

        if(projects.length > 0) {

            IRunnableWithProgress r = new IRunnableWithProgress() {
                public void run(IProgressMonitor monitor)
                    throws InvocationTargetException, InterruptedException {

                    String dirName =
                        Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.PROJECT_LIST_PATH);

                    if(dirName == null) {
                        MessageDialog.openInformation(
                            window.getShell(),
                            "ListProjectPlugin Plug-in",
                            "no directory specified where to save project list file. specify dir via preferences");
                    }
                    else {

                        File projectListFile = new File(dirName + "/projectList.txt");

                        BufferedWriter out = null;
                        try {
                            if(projectListFile.exists()) {
                                projectListFile.delete();
                                projectListFile.createNewFile();
                            }
                            else {

                                projectListFile.createNewFile();
                            }

                            out = new BufferedWriter(new FileWriter(projectListFile));
                        }
                        catch(IOException e) {
                            e.printStackTrace();
                        }

                        for(IProject p : projects) {

                            try {
                                out.write(p.getLocation().toFile().getAbsolutePath() + "\n");
                            }
                            catch(IOException e) {
                                e.printStackTrace();
                            }
                        }

                        try {
                            out.close();
                        }
                        catch(IOException e) {
                            e.printStackTrace();
                        }

                    }
                }

            };

            IWorkbench wb = PlatformUI.getWorkbench();
            IProgressService ps = wb.getProgressService();
            try {
                ps.busyCursorWhile(r);
            }
            catch(InvocationTargetException e) {
                e.printStackTrace();
            }
            catch(InterruptedException e) {
                e.printStackTrace();
            }

            MessageDialog.openInformation(window.getShell(), "Bulk Import Plug-in", "Saved location of "
                + projects.length + " projects");
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
    // TODO Auto-generated method stub

    }

}
