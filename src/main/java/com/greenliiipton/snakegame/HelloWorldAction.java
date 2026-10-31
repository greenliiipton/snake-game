package com.greenliiipton.snakegame;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public final class HelloWorldAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Messages.showInfoMessage(
                event.getProject(),
                "Hello, World! Aleksandr",
                "Snake Game"
        );
    }
}
