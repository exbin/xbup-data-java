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
package org.exbin.xbup.audio.xbplugins;

import java.io.File;
import java.util.Locale;
import javax.swing.filechooser.FileFilter;

/**
 * Audio file type.
 *
 * @version 0.1.25 2015/02/20
 * @author ExBin Project (http://exbin.org)
 */
public class AudioFileFilter extends FileFilter {

    public static final String AUDIO_FILE_TYPE = "AudioFileFilter";
    private String ext;

    public AudioFileFilter(String ext) {
        this.ext = ext;
    }

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return true;
        }

        String extension = getExtension(f);
        if (extension != null) {
            return extension.toLowerCase(Locale.getDefault()).equals(getExt());
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "Audio files " + getExt().toUpperCase(Locale.getDefault()) + " (*." + getExt() + ")";
    }

    public String getFileTypeId() {
        return AUDIO_FILE_TYPE + "." + ext;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public static String getExtension(File f) {
        String ext = null;
        String str = f.getName();
        int extPos = str.lastIndexOf('.');

        if (extPos > 0 && extPos < str.length() - 1) {
            ext = str.substring(extPos + 1).toLowerCase(Locale.getDefault());
        }
        return ext;
    }

}
