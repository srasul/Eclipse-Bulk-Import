
package listprojectplugin.actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import listprojectplugin.Activator;
import listprojectplugin.preferences.PreferenceConstants;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.ui.wizards.JavaCapabilityConfigurationPage;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * Our sample action implements workbench action delegate. The action proxy will be created by the workbench and shown
 * in the UI. When the user tries to use the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class OpenProjectAction
    implements IWorkbenchWindowActionDelegate {
    private IWorkbenchWindow window;

    /**
     * The constructor.
     */
    public OpenProjectAction() {}

    public void run(IAction action) {

        IRunnableWithProgress r = new IRunnableWithProgress() {

            @SuppressWarnings("restriction")
            public void run(IProgressMonitor monitor)
                throws InvocationTargetException, InterruptedException {

                final IWorkspace workspace = ResourcesPlugin.getWorkspace();
                IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();

                String dirName =
                    Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.PROJECT_LIST_PATH);

                File projectListFile = new File(dirName + "/projectList.txt");

                if(!projectListFile.exists()) {
                    MessageDialog.openInformation(window.getShell(), "Bulk Import Plug-in", projectListFile
                        .getAbsolutePath()
                        + " not found. cannot do bulk-import");
                    return;
                }

                try {
                    BufferedReader r = new BufferedReader(new FileReader(projectListFile));

                    String str;
                    while((str = r.readLine()) != null) {
                        final String finalStr = str;
                        final IProgressMonitor monitorCopy = monitor;
                        Runnable runnable = new Runnable() {
                            public void run() {
                                try {
                                    IPath projectDotProjectFile = new Path(finalStr + "/.project");
                                    IProjectDescription projectDescription =
                                        workspace.loadProjectDescription(projectDotProjectFile);
                                    JavaPlugin.logErrorMessage("got project description for "
                                        + projectDescription.getName());
                                    IProject project = workspace.getRoot().getProject(projectDescription.getName());
                                    JavaPlugin.logErrorMessage("created project for " + projectDescription.getName());
                                    JavaCapabilityConfigurationPage.createProject(project, projectDescription
                                        .getLocationURI(), monitorCopy);
                                    JavaPlugin
                                        .logErrorMessage("created project with JavaCapabilityConfigurationPage for "
                                            + projectDescription.getName());
                                }
                                catch(CoreException e) {
                                    //JavaPlugin.log(e);
//                                    MessageDialog.openInformation(window.getShell(), "Bulk Import Plug-in", e
//                                        .toString());
                                    e.printStackTrace();
                                }
                            }
                        };

                        final IWorkbench workbench = PlatformUI.getWorkbench();
                        workbench.getDisplay().syncExec(runnable);
                        
                    }

                }
                catch(FileNotFoundException e) {
                    //JavaPlugin.log(e);
                    //MessageDialog.openInformation(window.getShell(), "Bulk Import Plug-in", e.toString());
                    e.printStackTrace();
                }
                catch(IOException e) {
                    //JavaPlugin.log(e);
                    //MessageDialog.openInformation(window.getShell(), "Bulk Import Plug-in", e.toString());
                    e.printStackTrace();
                }

                monitor.done();
            }
        };

        IWorkbench wb = PlatformUI.getWorkbench();
        IProgressService ps = wb.getProgressService();
        try {
            ps.busyCursorWhile(r);
        }
        catch(InvocationTargetException e) {
            //JavaPlugin.log(e);
            e.printStackTrace();
        }
        catch(InterruptedException e) {
            //JavaPlugin.log(e);
            e.printStackTrace();
        }
    }

    /**
     * Selection in the workbench has been changed. We can change the state of the 'real' action here if we want, but
     * this can only happen after the delegate has been created.
     * 
     * @see IWorkbenchWindowActionDelegate#selectionChanged
     */
    public void selectionChanged(IAction action, ISelection selection) {}

    /**
     * We can use this method to dispose of any system resources we previously allocated.
     * 
     * @see IWorkbenchWindowActionDelegate#dispose
     */
    public void dispose() {}

    /**
     * We will cache window object in order to be able to provide parent shell for the message dialog.
     * 
     * @see IWorkbenchWindowActionDelegate#init
     */
    public void init(IWorkbenchWindow window) {
        this.window = window;
    }
}