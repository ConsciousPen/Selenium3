/* Copyright © 2016 EIS Group and/or one of its affiliates. All rights reserved. Unpublished work under U.S. copyright laws.
 CONFIDENTIAL AND TRADE SECRET INFORMATION. No portion of this work may be copied, distributed, modified, or incorporated into any other media without EIS Group prior written consent.*/
package aaa.admin.modules.general.note;

public enum NoteType {

    NOTE("Note", new Note());

    private String noteType;
    private INote note;

    NoteType(String notesType, INote notes) {
        this.noteType = notesType;
        this.note = notes;
    }

    public INote get() {
        return note;
    }

    public String getName() {
        return noteType;
    }

    public String getKey() {
        return note.getClass().getSimpleName();
    }

}
