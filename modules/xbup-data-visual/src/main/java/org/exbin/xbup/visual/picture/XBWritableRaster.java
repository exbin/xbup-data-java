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
package org.exbin.xbup.visual.picture;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import org.exbin.xbup.core.block.XBBlockTerminationMode;
import org.exbin.xbup.core.parser.XBProcessingException;
import org.exbin.xbup.core.serial.XBSerializable;
import org.exbin.xbup.core.serial.param.XBPInputSerialHandler;
import org.exbin.xbup.core.serial.param.XBPListener;
import org.exbin.xbup.core.serial.param.XBPOutputSerialHandler;
import org.exbin.xbup.core.serial.param.XBPProvider;
import org.exbin.xbup.core.serial.param.XBPSerializable;

/**
 * Bitmap Picture Raster (Testing only).
 *
 * @version 0.1.24 2015/01/24
 * @author ExBin Project (http://exbin.org)
 */
public class XBWritableRaster extends WritableRaster implements XBPSerializable {

    public static long[] XBUP_BLOCKREV_CATALOGPATH = {0, 4, 0, 0, 0};

    public XBWritableRaster(SampleModel sampleModel, DataBuffer dataBuffer, Rectangle aRegion, Point sampleModelTranslate, WritableRaster parent) {
        super(sampleModel, dataBuffer, aRegion, sampleModelTranslate, parent);
    }

    public XBWritableRaster(SampleModel sampleModel, DataBuffer dataBuffer, Point origin) {
        super(sampleModel, dataBuffer, origin);
    }

    public XBWritableRaster(SampleModel sampleModel, Point origin) {
        super(sampleModel, origin);
    }

    public static void serializeXBWritableRasterToXBT(final WritableRaster source, XBPListener serial) throws XBProcessingException, IOException {
        serial.putBegin(XBBlockTerminationMode.SIZE_SPECIFIED);
        serial.putData(new InputStream() {

            private int posX, posY, val;

            void InputStream(WritableRaster source) {
                posX = 0;
                posY = 0;
                val = 0;
            }

            @Override
            public int read() throws IOException {
                if (posY < 0) {
                    return posY;
                }
                int result = source.getSample(posX, posY, val);
//                        if ((val & 1) == 1) result = result >> 8;
                if (val == 2) {
                    val = 0;
                    if (posX == source.getWidth() - 1) {
                        posX = 0;
                        if (posY == source.getHeight() - 1) {
                            posY = -1;
                        } else {
                            posY++;
                        }
                    } else {
                        posX++;
                    }
                } else {
                    val++;
                }
                return result;
            }

            @Override
            public int available() throws IOException {
                if (posY < 0) {
                    return 0;
                }
                int size = (source.getHeight() - posY - 1) * source.getWidth() * 3
                        + (source.getWidth() - posX) * 3 - val;
                return size;
            }
        });
        serial.putEnd();
    }

    public static void serializeXBWritableRasterFromXBT(WritableRaster source, XBPProvider serial) throws XBProcessingException, IOException {
        serial.pullBegin();
        int posX, posY, val;
        posX = 0;
        posY = 0;
        val = 0;

        InputStream stream = serial.pullData();
        while (true) {
            int input = stream.read();
            source.setSample(posX, posY, val, input);
//                        if ((val & 1) == 1) result = result >> 8;
            if (val == 2) {
                val = 0;
                if (posX == source.getWidth() - 1) {
                    posX = 0;
                    if (posY == source.getHeight() - 1) {
                        break;
                    } else {
                        posY++;
                    }
                } else {
                    posX++;
                }
            } else {
                val++;
            }
        }
        serial.pullEnd();
    }

    public static XBSerializable getXBWritableRasterSerializator(WritableRaster source) {
        return new XBTSerializator(source);
    }

    @Override
    public void serializeFromXB(XBPInputSerialHandler serializationHandler) throws XBProcessingException, IOException {
        serializeXBWritableRasterFromXBT(this, serializationHandler);
    }

    @Override
    public void serializeToXB(XBPOutputSerialHandler serializationHandler) throws XBProcessingException, IOException {
        serializeXBWritableRasterToXBT(this, serializationHandler);
    }

    private static class XBTSerializator implements XBPSerializable {

        private final WritableRaster source;

        public XBTSerializator(WritableRaster source) {
            this.source = source;
        }

        @Override
        public void serializeFromXB(XBPInputSerialHandler serializationHandler) throws XBProcessingException, IOException {
            serializeXBWritableRasterFromXBT(source, serializationHandler);
        }

        @Override
        public void serializeToXB(XBPOutputSerialHandler serializationHandler) throws XBProcessingException, IOException {
            serializeXBWritableRasterToXBT(source, serializationHandler);
        }
    }
}
