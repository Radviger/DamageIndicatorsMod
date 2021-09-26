package ru.radviger.damageindicators.core;

import java.awt.*;
import java.awt.datatransfer.*;

public final class TextTransfer implements ClipboardOwner {
    @Override
    public void lostOwnership(Clipboard aClipboard, Transferable aContents) {
    }

    public void setClipboardContents(String text) {
        StringSelection stringSelection = new StringSelection(text);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, this);
    }
}
