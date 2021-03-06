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

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import org.exbin.xbup.core.block.XBBlockTerminationMode;
import org.exbin.xbup.core.block.declaration.XBDeclBlockType;
import org.exbin.xbup.core.parser.XBProcessingException;
import org.exbin.xbup.core.serial.param.XBPInputSerialHandler;
import org.exbin.xbup.core.serial.param.XBPOutputSerialHandler;
import org.exbin.xbup.core.serial.param.XBPSerializable;
import org.exbin.xbup.core.ubnumber.UBNatural;
import org.exbin.xbup.core.ubnumber.type.UBNat32;

/**
 * BufferedImage serialization wrapper.
 *
 * @version 0.1.25 2015/02/03
 * @author ExBin Project (http://exbin.org)
 */
public class XBBufferedImage implements XBPSerializable {

    private BufferedImage image;

    public static long[] XBUP_BLOCKREV_CATALOGPATH = {1, 4, 0, 0, 2, 0};
    public static long[] XBUP_FORMATREV_CATALOGPATH = {1, 4, 0, 1, 0};

    public XBBufferedImage() {
        image = null;
    }

    public XBBufferedImage(BufferedImage image) {
        this.image = image;
    }

    public XBBufferedImage(int width, int height, int imageType, IndexColorModel cm) {
        image = new BufferedImage(width, height, imageType, cm);
    }

    public XBBufferedImage(int width, int height, int imageType) {
        image = new BufferedImage(width, height, imageType);
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    @Override
    public void serializeFromXB(XBPInputSerialHandler serial) throws XBProcessingException, IOException {
        serial.pullBegin();
        serial.matchType(new XBDeclBlockType(XBUP_BLOCKREV_CATALOGPATH));
        UBNatural width = serial.pullAttribute().convertToNatural();
        UBNatural height = serial.pullAttribute().convertToNatural();
        BufferedImage result = new BufferedImage(width.getInt(), height.getInt(), BufferedImage.TYPE_INT_RGB);
        serial.pullConsist(XBWritableRaster.getXBWritableRasterSerializator(result.getRaster()));
        setImage(result);
        serial.pullEnd();
    }

    @Override
    public void serializeToXB(XBPOutputSerialHandler serial) throws XBProcessingException, IOException {
        serial.putBegin(XBBlockTerminationMode.SIZE_SPECIFIED);
        serial.putType(new XBDeclBlockType(XBUP_BLOCKREV_CATALOGPATH));
        WritableRaster raster = image.getRaster();
        serial.putAttribute(new UBNat32(raster.getWidth()));
        serial.putAttribute(new UBNat32(raster.getHeight()));
        serial.putConsist(XBWritableRaster.getXBWritableRasterSerializator(raster));
        serial.putEnd();
    }
}
