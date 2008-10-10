/*
 * -----LICENSE START-----
 * JSHManager - A Java-based tool for managing one's ScoreHero account.
 * Copyright (C) 2008 Tim Mullin
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * -----LICENSE END-----
*/
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
