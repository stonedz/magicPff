package org.neropaco.magicpff.action;

import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;

/**
 * Created with IntelliJ IDEA.
 * User: stonedz
 * Date: 4/3/14
 * Time: 11:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class NewModel extends AnAction {
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
    public void actionPerformed(AnActionEvent e) {
        // TODO: insert action logic here
    }
}
