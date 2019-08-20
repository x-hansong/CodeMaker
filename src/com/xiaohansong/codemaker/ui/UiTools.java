package com.xiaohansong.codemaker.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.Consumer;

class UiTools {
    static MouseListener onMouseClick(final Consumer<MouseEvent> listener) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                listener.accept(e);
            }
        };
    }

    static <T> T with(T component, Consumer<T> update) {
        update.accept(component);
        return component;
    }
}
