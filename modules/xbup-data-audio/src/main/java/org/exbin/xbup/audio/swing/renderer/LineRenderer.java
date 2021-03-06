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
import org.exbin.xbup.audio.swing.XBWavePanel;
import org.exbin.xbup.audio.wave.XBWave;

/**
 * Dots wave renderer.
 *
 * @version 0.2.0 2016/02/05
 * @author ExBin Project (http://exbin.org)
 */
public class LineRenderer extends DefaultRenderer {

    public LineRenderer() {
    }

    @Override
    public void paint(Graphics g, XBWavePanel panel, int begin, int end, RenderType renderType) {
        super.paint(g, panel, begin, end, renderType);

        LineRecord lineRecord = new LineRecord();
        XBWave wave = panel.getWave();
        int stopPos = end;
        if (wave != null) {
            g.setColor(panel.getWaveColor());
            int channelsCount = wave.getAudioFormat().getChannels();
            int[] prevMin = {-1, -1};
            int[] prevMax = {-1, -1};
            if (stopPos >= (panel.getWaveLength() - panel.getWindowPosition()) * panel.getScaleRatio()) {
                stopPos = (int) ((panel.getWaveLength() - panel.getWindowPosition()) * panel.getScaleRatio()) - 1;
            }

            for (int pos = begin - 1; pos < stopPos; pos++) {
                for (int channel = 0; channel < channelsCount; channel++) {
                    int pomPos = pos;
                    if (pomPos < 0) {
                        pomPos = 0;
                    }

                    int linePosition = panel.getWindowPosition() + (int) (pomPos / panel.getScaleRatio());
                    int value = wave.getRatioValue(linePosition, channel, panel.getHeight() / channelsCount) + (channel * panel.getHeight()) / channelsCount;
                    lineRecord.min = value;
                    lineRecord.max = value;
                    if (panel.getScaleRatio() < 1) {
                        int lineEndPosition = panel.getWindowPosition() + (int) ((pomPos + 1) / panel.getScaleRatio());
                        for (int inLinePosition = linePosition + 1; inLinePosition < lineEndPosition; inLinePosition++) {
                            int inValue = wave.getRatioValue(inLinePosition, channel, panel.getHeight() / channelsCount) + (channel * panel.getHeight()) / channelsCount;
                            if (inValue < lineRecord.min) {
                                lineRecord.min = inValue;
                            }
                            if (inValue > lineRecord.max) {
                                lineRecord.max = inValue;
                            }
                        }
                    }

                    if (panel.getScaleRatio() < 1) {
                        if (prevMax[channel] >= 0) {
                            g.drawLine(pos - 1, prevMax[channel], pos, lineRecord.max);
                            g.drawLine(pos - 1, prevMin[channel], pos, lineRecord.min);
                        }

                        prevMax[channel] = lineRecord.max;
                        prevMin[channel] = lineRecord.min;
                    } else {
                        if (prevMax[channel] >= 0) {
                            g.drawLine(pos - 1, prevMax[channel], pos, value);
                        }
                        prevMax[channel] = value;
                    }
                }
            }
        }
    }

    private class LineRecord {

        int min;
        int max;
    }
}
