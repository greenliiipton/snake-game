
package com.greenliiipton.snakegame;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

public class LineCounterAction extends AnAction {

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        e.getPresentation().setEnabled(
                e.getData(CommonDataKeys.EDITOR) != null
        );
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) return;

        String text = editor.getDocument().getText();

        int empty = 0;
        int comments = 0;
        int code = 0;
        boolean inBlock = false;

        String[] lines = text.split("\\R", -1);
        int count = text.isEmpty() ? 0 : lines.length;

        if (count > 0 && lines[count - 1].isEmpty()) {
            count--;
        }

        for (int n = 0; n < count; n++) {
            String line = lines[n];
            boolean hasCode = false;
            boolean hasComment = false;

            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);

                if (inBlock) {
                    hasComment = true;
                    if (c == '*' && i + 1 < line.length()
                            && line.charAt(i + 1) == '/') {
                        inBlock = false;
                        i++;
                    }
                    continue;
                }

                if (c == '"' || c == '\'') {
                    hasCode = true;
                    char quote = c;
                    i++;

                    while (i < line.length()) {
                        if (line.charAt(i) == '\\') {
                            i += 2;
                            continue;
                        }
                        if (line.charAt(i) == quote) break;
                        i++;
                    }
                    continue;
                }

                if (c == '/' && i + 1 < line.length()) {
                    char next = line.charAt(i + 1);

                    if (next == '/') {
                        hasComment = true;
                        break;
                    }

                    if (next == '*') {
                        hasComment = true;
                        inBlock = true;
                        i++;
                        continue;
                    }
                }

                if (c == '#') {
                    hasComment = true;
                    break;
                }

                if (!Character.isWhitespace(c)) {
                    hasCode = true;
                }
            }

            if (hasCode) {
                code++;
            } else if (hasComment) {
                comments++;
            } else {
                empty++;
            }
        }

        Messages.showInfoMessage(
                e.getProject(),
                "Всего строк: " + count +
                "\nСтрок кода: " + code +
                "\nКомментариев: " + comments +
                "\nПустых строк: " + empty,
                "Snake Game — статистика кода"
        );
    }
}
