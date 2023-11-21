package com.example;

import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Score;
import jm.music.data.Phrase;

import java.util.ArrayList;

public class GeneratedNotesToScore
{
    ArrayList<Note> notes = new ArrayList<>();
    Phrase phrase = new Phrase();
    ArrayList<Part> parts = new ArrayList<>();
    Score HTTYD = new Score();
    ArrayList<Integer> pitches;
    ArrayList<Double> rhythms;

    void readPitchesAndRhythms(ArrayList<Integer> p, ArrayList<Double> r)
    {
        pitches = p;
        rhythms = r;
    }

    Score toScore()
    {
        makeNotes();
        notesToPhrases();
        phrasesToNewParts();

        for( int i = 0; i < parts.size(); i++)
        {
            HTTYD.add(parts.get(i));
        }
        return HTTYD;
    }

    void makeNotes()
    {
        for(int i = 0; i < pitches.size(); i++)
        {
            Note newNote = new Note(pitches.get(i), rhythms.get(i));
            notes.add(newNote);
        }
    }

    void notesToPhrases()
    {
        for(int i = 0; i < notes.size(); i++)
        {
            phrase.add(notes.get(i));
        }
    }

    void phrasesToNewParts()
    {
        Part altoSax = new Part(phrase, "Alto", 65, 0);
        Part tenorSax = new Part(phrase, "Tenor", 66, 1);
        Part bariSax = new Part(phrase, "Bari", 67, 2);
        Part contra = new Part(phrase, "Contra", 43, 3);
        Part piccolo = new Part(phrase, "Piccolo", 72, 4);
        Part trumpet = new Part(phrase, "Trumpet", 56, 5);
        Part trombone = new Part(phrase, "Trombone", 57, 6);
        parts.add(trombone);
        parts.add(trumpet);
        parts.add(contra);
        parts.add(piccolo);
        parts.add(bariSax);
        parts.add(tenorSax);
        parts.add(altoSax);
    }
}