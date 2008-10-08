package jshm.gui.plaf;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.AbstractButton;

import org.jdesktop.swingx.plaf.windows.WindowsHyperlinkUI;

public class MyHyperlinkUI extends WindowsHyperlinkUI {
    /**
     * {@inheritDoc} <p>
     * Overridden to always paint the underline.
     */
    @Override
    protected void paintText(Graphics g, AbstractButton b, Rectangle textRect,
            String text) {
        super.paintText(g, b, textRect, text);
        paintUnderline(g, b, textRect, text);
    }
    
    private void paintUnderline(Graphics g, AbstractButton b, Rectangle rect,
            String text) {
        // JW: copied from JXTable.LinkRenderer
        FontMetrics fm = g.getFontMetrics();
        int descent = fm.getDescent();

        // REMIND(aim): should we be basing the underline on
        // the font's baseline instead of the text bounds?
        g.drawLine(rect.x + getTextShiftOffset(),
                (rect.y + rect.height) - descent + 1 + getTextShiftOffset(),
                rect.x + rect.width + getTextShiftOffset(),
                (rect.y + rect.height) - descent + 1 + getTextShiftOffset());
    }
}
