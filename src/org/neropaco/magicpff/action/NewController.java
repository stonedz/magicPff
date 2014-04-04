package org.neropaco.magicpff.action;

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.jetbrains.php.*;
import com.jetbrains.php.lang.PhpFileType;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;


/**
 * Created with IntelliJ IDEA.
 * User: stonedz
 * Date: 4/1/14
 * Time: 12:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class NewController extends AnAction {

    public void update(AnActionEvent event) {
        Project project = event.getData(PlatformDataKeys.PROJECT);

        if (project == null) {
            return;
        }

        DataContext dataContext = event.getDataContext();
        IdeView view = LangDataKeys.IDE_VIEW.getData(dataContext);
        if (view == null) {
            return;
        }

        final PsiDirectory initialBaseDir = view.getOrChooseDirectory();
        if (initialBaseDir == null) {
            return;
        }
    }

    @Override
    public void actionPerformed(AnActionEvent event) {
        final Project project = event.getData(PlatformDataKeys.PROJECT);
        buildFile(event, project, "/resources/controller.php");
    }

    public static void buildFile(AnActionEvent event, final Project project, String templatePath) {

        String fileName = Messages.showInputDialog(project, "File name (without extension and _Controller part)", "Create Controller",  PhpIcons.PHP_FILE);

        if(fileName == null || StringUtils.isBlank(fileName)) {
            return;
        }

        if(fileName.endsWith("Controller")) {
            fileName = fileName.substring(0, fileName.length() - 10);
        }
        else if(fileName.endsWith("_Controller")) {
            fileName = fileName.substring(0, fileName.length() - 11);
        }

        fileName = StringUtils.capitalize(fileName);

        if(fileName == null || StringUtils.isBlank(fileName)) {
            return;
        }

        DataContext dataContext = event.getDataContext();
        IdeView view = LangDataKeys.IDE_VIEW.getData(dataContext);
        if (view == null) {
            return;
        }

        VirtualFile vf = project.getBaseDir();
        final PsiDirectory initialBaseDir;
        try{
            initialBaseDir = PsiManager.getInstance(project).findDirectory(vf).findSubdirectory("app").findSubdirectory("controllers");
        }
        catch(Exception e) {
            Messages.showErrorDialog("Dont' seem to be a Pff project", "Something's wrong");
            return;
        }

        String content;
        try {
            content = ServiceAction.inputStreamToString(NewController.class.getResourceAsStream(templatePath));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        //content = "prova {{ ControllerName }}";

        final PsiFileFactory factory = PsiFileFactory.getInstance(project);

//        SymfonyBundleUtil symfonyBundleUtil = new SymfonyBundleUtil(project);
//        SymfonyBundle symfonyBundle = symfonyBundleUtil.getContainingBundle(initialBaseDir);
//
//        if(symfonyBundle != null) {
//            bundleName = StringUtils.strip(symfonyBundle.getNamespaceName(), "\\");
//        }
//
//        String path = symfonyBundle.getRelative(initialBaseDir.getVirtualFile());
//        if(path != null) {
//            bundleName = bundleName.concat("\\" + path);
//        }

        content = content.replace("{{ ControllerName }}", fileName);

        fileName = fileName.concat("_Controller.php");

        if(initialBaseDir.findFile(fileName) != null) {
            Messages.showInfoMessage("File exists", "Error");
            return;
        }

        final PsiFile file = factory.createFileFromText(fileName, PhpFileType.INSTANCE, content);

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            @Override
            public void run() {
                CodeStyleManager.getInstance(project).reformat(file);
                initialBaseDir.add(file);
            }
        });

        PsiFile psiFile = initialBaseDir.findFile(fileName);
        if(psiFile != null) {
            view.selectElement(psiFile);
        }
    }
}
