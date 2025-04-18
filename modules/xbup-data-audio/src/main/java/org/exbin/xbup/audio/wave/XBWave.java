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
package org.exbin.xbup.audio.wave;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.exbin.auxiliary.binary_data.BinaryData;
import org.exbin.auxiliary.binary_data.ByteArrayData;
import org.exbin.auxiliary.binary_data.ByteArrayEditableData;
import org.exbin.auxiliary.binary_data.EditableBinaryData;
import org.exbin.xbup.core.block.declaration.XBDeclBlockType;
import org.exbin.xbup.core.parser.XBProcessingException;
import org.exbin.xbup.core.serial.param.XBPSequenceSerialHandler;
import org.exbin.xbup.core.serial.param.XBPSequenceSerializable;
import org.exbin.xbup.core.serial.param.XBSerializationMode;
import org.exbin.xbup.core.type.XBData;
import org.exbin.xbup.core.ubnumber.UBNatural;
import org.exbin.xbup.core.ubnumber.type.UBNat32;

/**
 * Simple panel audio wave.
 *
 * @version 0.2.0 2016/05/24
 * @author ExBin Project (http://exbin.org)
 */
public class XBWave implements XBPSequenceSerializable {

    public static final long[] XBUP_BLOCKREV_CATALOGPATH = {1, 5, 0, 0};
    public static final long[] XBUP_FORMATREV_CATALOGPATH = {1, 5, 0, 0};
    private AudioFormat audioFormat;
    private final XBData data = new XBData(65520);

    public XBWave() {
        audioFormat = null;
    }

    public XBWave(AudioFormat audioFormat) {
        this.audioFormat = audioFormat;
    }

    public void loadFromFile(File soundFile) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            audioFormat = audioInputStream.getFormat();
            System.out.println(getAudioFormat());
            if ((audioFormat.getChannels() > 2)
                    || (audioFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED && audioFormat.getSampleSizeInBits() != 8)
                    || (audioFormat.getEncoding() != AudioFormat.Encoding.PCM_SIGNED && audioFormat.getEncoding() != AudioFormat.Encoding.PCM_UNSIGNED && audioFormat.getSampleSizeInBits() == 8)
                    || (!(audioFormat.getSampleSizeInBits() == 8 || audioFormat.getSampleSizeInBits() == 16 || audioFormat.getSampleSizeInBits() == 24 || audioFormat.getSampleSizeInBits() == 32))) {
                System.out.println("Unable to load! Currently only 44kHz SIGNED 16bit Mono/Stereo is supported.");
                return;
            }
//            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, getAudioFormat());
//            SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);

//            sourceDataLine.open(getAudioFormat());
//            sourceDataLine.start();
            data.loadFromStream(audioInputStream);
//        } catch (LineUnavailableException ex) {
//            Logger.getLogger(XBWave.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedAudioFileException | IOException ex) {
            Logger.getLogger(XBWave.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void saveToFile(File soundFile) {
        saveToFile(soundFile, Type.WAVE);
    }

    public void saveToFile(File soundFile, Type fileType) {
        try {
            AudioSystem.write(new AudioInputStream(data.getDataInputStream(), audioFormat, getLengthInTicks()), fileType, soundFile);
        } catch (IOException ex) {
            Logger.getLogger(XBWave.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void serializeXB(XBPSequenceSerialHandler serial) throws XBProcessingException, IOException {
        serial.begin();
        serial.matchType(new XBDeclBlockType(XBUP_BLOCKREV_CATALOGPATH));
        if (serial.getSerializationMode() == XBSerializationMode.PULL) {
            UBNatural sampleRate = serial.pullAttribute().convertToNatural();
            UBNatural sampleSizeInBits = serial.pullAttribute().convertToNatural();
            UBNatural channels = serial.pullAttribute().convertToNatural();
            UBNatural signed = serial.pullAttribute().convertToNatural();
            UBNatural bigEndian = serial.pullAttribute().convertToNatural();
            audioFormat = new AudioFormat(sampleRate.getInt(), sampleSizeInBits.getInt(), channels.getInt(), signed.getInt() == 1, bigEndian.getInt() == 1);
        } else {
            serial.putAttribute(new UBNat32((long) audioFormat.getSampleRate()));
            serial.putAttribute(new UBNat32(audioFormat.getSampleSizeInBits()));
            serial.putAttribute(new UBNat32(audioFormat.getChannels()));
            serial.putAttribute(new UBNat32(audioFormat.getEncoding() == AudioFormat.Encoding.PCM_SIGNED ? 1 : 0));
            serial.putAttribute(new UBNat32(audioFormat.isBigEndian() ? 1 : 0));
        }
        serial.consist(new XBPSequenceSerializable() {
            @Override
            public void serializeXB(XBPSequenceSerialHandler serial) throws XBProcessingException, IOException {
                serial.begin();
                if (serial.getSerializationMode() == XBSerializationMode.PULL) {
                    data.loadFromStream(serial.pullData());
                } else {
                    serial.putData(data.getDataInputStream());
                }
                serial.end();
            }
        });
        serial.end();
    }

    public void performTransformReverse() {
        performTransformReverse(0, getLengthInTicks() - 1);
    }

    public void performTransformReverse(long startPosition, long endPosition) {
        if (!data.isEmpty()) {
            // TODO support for non-whole-byte alignment later
            int sampleSize = audioFormat.getSampleSizeInBits() / 8;
            long startDataPos = startPosition * audioFormat.getChannels() * sampleSize;
            long endDataPos = endPosition * audioFormat.getChannels() * sampleSize;

            byte[] buffer1 = new byte[sampleSize];
            byte[] buffer2 = new byte[sampleSize];
            long remaining = (endPosition - startPosition) * audioFormat.getChannels();

            while (remaining > 0) {
                data.copyToArray(startDataPos, buffer1, 0, sampleSize);
                endDataPos -= sampleSize;
                data.copyToArray(endDataPos, buffer2, 0, sampleSize);
                data.replace(startDataPos, buffer2, 0, sampleSize);
                data.replace(endDataPos, buffer1, 0, sampleSize);
                startDataPos += sampleSize;

                remaining--;
            }
        }
    }

    public int getPageSize() {
        return data.getPageSize();
    }

    public InputStream getInputStream() {
        return data.getDataInputStream();
    }

    public AudioInputStream getAudioInputStream() {
        return new AudioInputStream(data.getDataInputStream(), audioFormat, getLengthInTicks());
    }

    /**
     * Returns data size in bytes.
     *
     * @return size in bytes
     */
    public long getDataSize() {
        return data.getDataSize();
    }

    /**
     * Returns data for single tick.
     *
     * @param targetData target data
     * @param position tick position
     * @param channel channgel number
     */
    public void getValue(EditableBinaryData targetData, int position, int channel) {
        int bytesPerSample = audioFormat.getSampleSizeInBits() >> 3;
        int dataPosition = (position * audioFormat.getChannels() + channel) * bytesPerSample;
        targetData.replace(0, data, dataPosition, bytesPerSample);
    }

    public int getRatioValue(int position, int channel, int height) {
        int bytesPerSample = audioFormat.getSampleSizeInBits() >> 3;

        int chunk = ((position * audioFormat.getChannels() + channel) * bytesPerSample) / data.getPageSize();
        int offset = ((position * audioFormat.getChannels() + channel) * bytesPerSample) % data.getPageSize();

        long value;
        if (audioFormat.getEncoding() == AudioFormat.Encoding.PCM_UNSIGNED) {
            value = data.getPage(chunk).getByte(offset) & 0xFF;
        } else {
            value = data.getPage(chunk).getByte(offset);
        }
        if (bytesPerSample > 1) {
            value += ((long) ((data.getPage(chunk).getByte(offset + 1) + 127)) << 8);
            if (bytesPerSample > 2) {
                value += ((long) ((data.getPage(chunk).getByte(offset + 2) + 127)) << 16);
                if (bytesPerSample > 3) {
                    value += ((long) ((data.getPage(chunk).getByte(offset + 3) + 127)) << 24);
                }
            }
        }

        return (int) (((long) value * height) >> audioFormat.getSampleSizeInBits());
    }

    public void setValue(BinaryData sourceData, int position, int channel) {
        int bytesPerSample = audioFormat.getSampleSizeInBits() >> 3;
        int dataPosition = (position * audioFormat.getChannels() + channel) * bytesPerSample;
        data.replace(dataPosition, sourceData, 0, bytesPerSample);
    }

    public void setRatioValue(int pos, int value, int channel, int height) {
        // TODO: support for different bitsize
        int chunk = ((pos * audioFormat.getChannels() + channel) * 2) / data.getPageSize();
        int offset = ((pos * audioFormat.getChannels() + channel) * 2) % data.getPageSize();
        ByteArrayEditableData block = new ByteArrayEditableData();
        block.insert(0, data.getPage(chunk));

        int pomValue = ((value - (height / 2)) << 16) / height;
        block.setByte(offset, (byte) (pomValue & 255));
        block.setByte(offset + 1, (byte) ((pomValue >> 8) & 255));
        data.replace((long) chunk * data.getPageSize(), block);
        /*        int value = 127 + getBlock(chunk)[offset] + (getBlock(chunk)[offset+1] + 127)*256;
         return (int) ((long) value * height) / 65536; */
    }

    public AudioFormat getAudioFormat() {
        return audioFormat;
    }

    public int getLengthInTicks() {
        int bytesPerSample = audioFormat.getSampleSizeInBits() >> 3;
        return (int) data.getDataSize() / (audioFormat.getChannels() * bytesPerSample);
    }

    public void setLengthInTicks(int ticks) {
        int bytesPerSample = audioFormat.getSampleSizeInBits() >> 3;
        data.setDataSize(ticks * audioFormat.getChannels() * bytesPerSample);
    }

    public void append(byte[] data) {

    }

    public void apendTo(XBWave wave) {
        apendTo(wave, 0, getLengthInTicks());
    }

    public void apendTo(XBWave wave, int start, int length) {

    }

    public byte[] readChunk(int start, int length) {
        int pos = 0;
        return null;
    }

    /**
     * Cuts section of wave as plain data.
     *
     * @param startPosition position in ticks
     * @param length length in ticks
     * @return data blob
     */
    public BinaryData cutData(int startPosition, int length) {
        long startDataPos = startPosition * audioFormat.getChannels() * 2;
        long dataLength = length * audioFormat.getChannels() * 2;
        BinaryData cutData = data.copy(startDataPos, dataLength);
        data.remove(startDataPos, dataLength);
        return cutData;
    }

    public void insertData(BinaryData deletedData, int startPosition) {
        long startDataPos = startPosition * audioFormat.getChannels() * 2;
        data.insert(startDataPos, deletedData);
    }

    public XBWave copy(int startPosition, int length) {
        long startDataPos = startPosition * audioFormat.getChannels() * 2;
        long dataLength = length * audioFormat.getChannels() * 2;
        XBWave waveCopy = new XBWave();
        waveCopy.audioFormat = audioFormat;
        waveCopy.data.insert(0, data, startDataPos, dataLength);
        return waveCopy;
    }

    public void insertWave(XBWave pastedWave, int startPosition) {
        // TODO match audio format
        insertData(pastedWave.data, startPosition);
    }
}
