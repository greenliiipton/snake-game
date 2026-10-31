
package com.greenliiipton.snakegame;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.CustomStatusBarWidget;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidgetFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CoderTimerWidgetFactory implements StatusBarWidgetFactory {

    private static final String ID = "SnakeCoderTimer";

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getDisplayName() {
        return "Snake Coder Timer";
    }

    @Override
    public boolean isAvailable(Project project) {
        return true;
    }

    @Override
    public StatusBarWidget createWidget(Project project) {
        return new CoderTimerWidget();
    }

    @Override
    public void disposeWidget(StatusBarWidget widget) {
        Disposer.dispose(widget);
    }

    @Override
    public boolean canBeEnabledOn(StatusBar statusBar) {
        return true;
    }

    private static class CoderTimerWidget implements CustomStatusBarWidget {

        private final JLabel label = new JLabel();
        private final Timer timer;
        private long startTime = System.nanoTime();

        CoderTimerWidget() {
            label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            label.setToolTipText("Нажми, чтобы сбросить таймер");

            label.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    startTime = System.nanoTime();
                    updateText();
                }
            });

            timer = new Timer(1000, e -> updateText());
            updateText();
        }

        private void updateText() {
            long seconds = (System.nanoTime() - startTime) / 1_000_000_000L;

            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            long secs = seconds % 60;

            label.setText(String.format(
                    "Coder: %02d:%02d:%02d",
                    hours, minutes, secs
            ));
        }

        @Override
        public String ID() {
            return ID;
        }

        @Override
        public JComponent getComponent() {
            return label;
        }

        @Override
        public void install(StatusBar statusBar) {
            timer.start();
        }

        @Override
        public void dispose() {
            timer.stop();
        }
    }
}
