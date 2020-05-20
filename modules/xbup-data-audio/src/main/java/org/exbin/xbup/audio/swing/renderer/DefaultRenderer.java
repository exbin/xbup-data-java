/*
 * Copyright (C) ExBin Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.exbin.xbup.audio.swing.renderer;

import java.awt.Graphics;
import java.awt.Rectangle;
import org.exbin.xbup.audio.swing.XBWavePanel;

/**
 * Default wave renderer.
 *
 * @version 0.2.0 2016/02/05
 * @author ExBin Project (http://exbin.org)
 */
public class DefaultRenderer implements XBWavePanelRenderer {

    public DefaultRenderer() {
    }

    @Override
    public void paint(Graphics g, XBWavePanel panel, int begin, int end, RenderType renderType) {
        Rectangle clipBounds = g.getClipBounds();

        switch (renderType) {
            case NORMAL: {
                g.setColor(panel.getBackground());
                break;
            }
            case CURSOR: {
                g.setColor(panel.getCursorColor());
                break;
            }
            case SELECTION: {
                g.setColor(panel.getSelectionColor());
                break;
            }
            default:
                throw new IllegalStateException();
        }

        g.fillRect(begin, clipBounds.y, end, clipBounds.height);
    }

    protected int getZoomScale(double scaleRatio) {
        if (scaleRatio > 1 / 16f) {
            return 1;
        } else if (scaleRatio > 1 / 256f) {
            return 16;
        } else {
            return 256;
        }
    }
}
