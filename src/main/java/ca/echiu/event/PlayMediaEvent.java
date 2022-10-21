package ca.echiu.event;

import ca.echiu.wrapper.FileWrapper;

import java.io.File;


public class PlayMediaEvent {
    private File file;

    public PlayMediaEvent(File file) {
        this.file = file;
    }

    public File getFile(){
        return file;

    }
}
