/*
 * c2017-2023 Courtney Brown 
 * Class: Project 2 Template
 * Description: This is a template for the project 2 code, which is an implementation of a Markov chain of order 1
 */

package com.example;

//importing the JMusic stuff
import jm.music.data.*;
import jm.util.*;
import jm.music.data.Note;
import jm.music.data.Part;
import jm.music.data.Score;
import jm.music.data.Phrase;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Scanner;

//make sure this class name matches your file name, if not fix.
public class App {

	static MelodyPlayer player; // play a midi sequence
	static MidiFileToNotes midiNotes; // read a midi file
	static int noteCount = 0;
	static ProbabilityGenerator<Integer> pitchGenerator = new ProbabilityGenerator<>();
	static ProbabilityGenerator<Double> rhythmGenerator = new ProbabilityGenerator<>();
	ArrayList<Integer> newPitches = new ArrayList<>();
	ArrayList<Double> newRhythms = new ArrayList<>();

	// make cross-platform
	static FileSystem sys = FileSystems.getDefault();

	static String filePath;
	// the getSeperator() creates the appropriate back or forward slash based on the
	// OS in which it is running -- OS X & Windows use same code :)
	// path to the midi file -- you can
																			// change this to your file
	// location/name

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// run the unit tests
		Scanner scan = new Scanner(System.in);
		while(true)
		{
			printMenu();
			int input = scan.nextInt();
			if(input == 1)
			{
				printMidiList();
				int midiFile = scan.nextInt();
				if(midiFile == 1)
				{
					filePath = "mid" + sys.getSeparator() + "HTTYD Drumline.mid";
					setup();
				}
				else if(midiFile == 2)
				{
					filePath = "mid" + sys.getSeparator() + "Test Drive.mid";
					setup();
				}
				else if(midiFile == 3)
				{
					filePath = "mid" + sys.getSeparator() + "MaryHadALittleLamb.mid";
					setup();
				}

			}
			else if(input == 2)
			{
				generateNewMidi();
			}
			else if(input == 3)
			{
				break;
			}
		}
		// playMelody();
		int whichTest = Integer.parseInt(args[0]);
		whichTest = 4;
		if (whichTest == 2) {

			pitchGenerator.train(midiNotes.getPitchArray());// trains the prob gens
			rhythmGenerator.train(midiNotes.getRhythmArray());

			pitchGenerator.printProbabilityDistribution(false);// prints the prob distribution for pitch and rhythm
			rhythmGenerator.printProbabilityDistribution(false);

			ProbabilityGenerator<Integer> pitchProbGen = new ProbabilityGenerator<Integer>(); // creates a new prob gen
																								// for pitches
			ProbabilityGenerator<Double> rhythmProbGen = new ProbabilityGenerator<Double>();// creates a new prob gen
																							// for rhythms

			pitchProbGen.train(midiNotes.getPitchArray());// trains the pitch gen
			rhythmProbGen.train(midiNotes.getRhythmArray());
			for (int i = 0; i < 10000; i++) {
				ArrayList<Integer> pitches = pitchProbGen.generate(20); // creates a new array for the pitchgen to
																		// generate the new melody
				ArrayList<Double> rhythms = rhythmProbGen.generate(20);// creates a new arraylist for the rhythm gen to
																		// generate the new melody

				pitchGenerator.train(pitches);// trains the probgen that will print the distribution
				rhythmGenerator.train(rhythms);
			}
			pitchGenerator.printProbabilityDistribution(true);// prints the prob distribution for pitch and rhythm
			rhythmGenerator.printProbabilityDistribution(true);
		} else if (whichTest == 3) {
			testAndTrainMarkovChain();
		}

		else if (whichTest == 4) {

			generateMarkovChain();
		}

		// setup the melody player
		// uncomment below when you are ready to test or present sound output
		// make sure that it is commented out for your final submit to github (eg. when
		// pushing)
		// setup();
		// playMelody();

		// uncomment to debug your midi file
		// this code MUST be commited when submitting unit tests or any code to github
		// playMidiFileDebugTest(filePath);
	}

	public static void generateNewMidi() 
	{
		pitchGenerator.train(midiNotes.getPitchArray());
		rhythmGenerator.train(midiNotes.getRhythmArray());

		MarkovChainGenerator<Integer> pitchGen = new MarkovChainGenerator<Integer>();
		MarkovChainGenerator<Double> rhythmGen = new MarkovChainGenerator<Double>();

		pitchGen.train(midiNotes.getPitchArray());
		rhythmGen.train(midiNotes.getRhythmArray());

		MarkovChainGenerator<Integer> ScorePitchGen = new MarkovChainGenerator<Integer>();
		MarkovChainGenerator<Double> ScoreRhythmGen = new MarkovChainGenerator<Double>();

		ArrayList<Integer> newPitches = new ArrayList<>();
		ArrayList<Double> newRhythms = new ArrayList<>();
		for (int i = 0; i < 10000; i++) {
			int initialPitch = pitchGenerator.generateOneToken();
			double initialRhythm = rhythmGenerator.generateOneToken();

			newPitches = pitchGen.generate(initialPitch, 20);
			newRhythms = rhythmGen.generate(initialRhythm, 20);

			ScorePitchGen.train(newPitches);
			ScoreRhythmGen.train(newRhythms);
		}
		int initialPitch = pitchGenerator.generateOneToken();
		double initialRhythm = rhythmGenerator.generateOneToken();

		ArrayList<Integer> ScorePitches = ScorePitchGen.generate(initialPitch, 127);
		ArrayList<Double> ScoreRhythms = ScoreRhythmGen.generate(initialRhythm, 127);

		GeneratedNotesToScore newScore = new GeneratedNotesToScore();

		newScore.readPitchesAndRhythms(ScorePitches, ScoreRhythms);
		Score HTTYDBand = newScore.toScore();
		Write.midi(HTTYDBand, "NewTestDrive.mid");
	}

	public static void printMidiList() 
	{
		System.out.println("Choose a Midi File");
		System.out.println("1. HTTYD Drumline.mid");
		System.out.println("2. Test Drive.mid");
		System.out.println("3. MaryHadALittleLamb.mid");
	}

	public static void printMenu() 
	{
		System.out.println("What would you like to do?");
		System.out.print("1. setup a Midi File \n 2. Generate a new Midi File \n 3. Quit");
	}

	// doing all the setup stuff
	public static void setup() {

		// playMidiFile(filePath); //use to debug -- this will play the ENTIRE file --
		// use ONLY to check if you have a valid path & file & it plays
		// it will NOT let you know whether you have opened file to get the data in the
		// form you need for the assignment

		midiSetup(filePath);
	}

	// plays the midi file using the player -- so sends the midi to an external
	// synth such as Kontakt or a DAW like Ableton or Logic
	static public void playMelody() {

		assert (player != null); // this will throw an error if player is null -- eg. if you haven't called
									// setup() first

		while (!player.atEndOfMelody()) {
			player.play(); // play each note in the sequence -- the player will determine whether is time
							// for a note onset
		}

	}

	// opens the midi file, extracts a voice, then initializes a melody player with
	// that midi voice (e.g. the melody)
	// filePath -- the name of the midi file to play
	static void midiSetup(String filePath) {

		// Change the bus to the relevant port -- if you have named it something
		// different OR you are using Windows
		player = new MelodyPlayer(100, "Bus 1"); // sets up the player with your bus.

		midiNotes = new MidiFileToNotes(filePath); // creates a new MidiFileToNotes -- reminder -- ALL objects in Java
													// must
													// be created with "new". Note how every object is a pointer or
													// reference. Every. single. one.

		// // which line to read in --> this object only reads one line (or ie, voice or
		// ie, one instrument)'s worth of data from the file
		midiNotes.setWhichLine(0); // this assumes the melody is midi channel 0 -- this is usually but not ALWAYS
									// the case, so you can try other channels as well, if 0 is not working out for
									// you.

		noteCount = midiNotes.getPitchArray().size(); // get the number of notes in the midi file

		assert (noteCount > 0); // make sure it got some notes (throw an error to alert you, the coder, if not)

		// sets the player to the melody to play the voice grabbed from the midi file
		// above
		player.setMelody(midiNotes.getPitchArray());
		player.setRhythm(midiNotes.getRhythmArray());
	}

	static void resetMelody() {
		player.reset();

	}

	// this function is not currently called. you may call this from setup() if you
	// want to test
	// this just plays the midi file -- all of it via your software synth. You will
	// not use this function in upcoming projects
	// but it could be a good debug tool.
	// filename -- the name of the midi file to play
	static void playMidiFileDebugTest(String filename) {
		Score theScore = new Score("Temporary score");
		Read.midi(theScore, filename);
		Play.midi(theScore);
	}

	static void testAndTrainMarkovChain() {
		MarkovChainGenerator<Integer> pitchGen = new MarkovChainGenerator<Integer>();// creates a new prob gen for
																						// pitches
		MarkovChainGenerator<Double> rhythmGen = new MarkovChainGenerator<Double>();// creates a new prob gen for
																					// rhythms
		pitchGen.train(midiNotes.getPitchArray());// trains the markov gens
		rhythmGen.train(midiNotes.getRhythmArray());

		pitchGen.printProbabilityDistribution(false);// prints the prob distribution for pitch and rhythm
		rhythmGen.printProbabilityDistribution(false);
	}

	static void generateMarkovChain() {

		pitchGenerator.train(midiNotes.getPitchArray());
		rhythmGenerator.train(midiNotes.getRhythmArray());

		MarkovChainGenerator<Integer> pitchGen = new MarkovChainGenerator<Integer>();
		MarkovChainGenerator<Double> rhythmGen = new MarkovChainGenerator<Double>();

		pitchGen.train(midiNotes.getPitchArray());
		rhythmGen.train(midiNotes.getRhythmArray());

		MarkovChainGenerator<Integer> ScorePitchGen = new MarkovChainGenerator<Integer>();
		MarkovChainGenerator<Double> ScoreRhythmGen = new MarkovChainGenerator<Double>();

		ArrayList<Integer> newPitches = new ArrayList<>();
		ArrayList<Double> newRhythms = new ArrayList<>();
		for (int i = 0; i < 10000; i++) {
			int initialPitch = pitchGenerator.generateOneToken();
			double initialRhythm = rhythmGenerator.generateOneToken();

			newPitches = pitchGen.generate(initialPitch, 20);
			newRhythms = rhythmGen.generate(initialRhythm, 20);

			ScorePitchGen.train(newPitches);
			ScoreRhythmGen.train(newRhythms);
		}
		ScorePitchGen.printProbabilityDistribution(false);
		ScoreRhythmGen.printProbabilityDistribution(false);
	}

}
