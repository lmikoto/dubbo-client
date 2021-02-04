package io.github.lmikoto;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.*;

import javax.swing.*;

public class DubboClientAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);

        PsiElement psiElement = e.getData(CommonDataKeys.PSI_ELEMENT);
        if (!(psiElement instanceof PsiMethod)) {
            Messages.showMessageDialog("只适用于dubbo方法", "warn", (Icon)null);
        } else {
            PsiMethod psiMethod = (PsiMethod)psiElement;
            PsiParameterList parameterList = psiMethod.getParameterList();
            PsiJavaFile javaFile = (PsiJavaFile)psiMethod.getContainingFile();
            PsiClass psiClass = (PsiClass)psiElement.getParent();
            String interfaceName = String.format("%s.%s", javaFile.getPackageName(), psiClass.getName());
            String[] methodType = new String[parameterList.getParameters().length];

            for(int i = 0; i < parameterList.getParameters().length; ++i) {
                methodType[i] = parameterList.getParameters()[i].getType().getCanonicalText();;
            }

            Object[] initParamArray = ParamUtils.getInitParamArray(psiMethod.getParameterList());
            String methodName = psiMethod.getName();
            ToolWindow toolWindow = ToolWindowManager.getInstance(e.getProject()).getToolWindow("DubboClient");
            if (toolWindow != null) {
                toolWindow.show(() -> {
                });
                ClientPanel client = (ClientPanel)toolWindow.getComponent().getComponent(0);
                DubboEntity entity = new DubboEntity();
                entity.setInterfaceName(interfaceName);
                entity.setParamObj(initParamArray);
                entity.setMethodType(methodType);
                entity.setMethodName(methodName);
                ClientPanel.refreshUI(client, entity);
            }

        }
    }
}