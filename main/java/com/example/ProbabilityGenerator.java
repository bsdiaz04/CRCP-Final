/*
 * c2017-2023 Brayden Diaz - some code provided by Courtney Brown (NOTE: you'll have to change the name and give me a bit of credit!)
 * 
 * Class: ProbabliityGenerator
 * 
 */


package com.example;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ProbabilityGenerator <E>
{
	ArrayList<E> tokens = new ArrayList<>(); //declares an arraylist of tokens that will be for pitches and rhythms
	ArrayList<Float> symbolsCount; //declares an arraylist that will be for the amount of times a unique token has been found
	int numTokens; // the amount of tokens seen in total

	void train(ArrayList<E> data) //trains the probability generator
	{
		if(tokens == null || symbolsCount == null) //if any of the arraylists are empty ( error handling for generate later on) 
		{
			tokens = new ArrayList<E>(); //initialize tokens arraylist
			symbolsCount = new ArrayList<Float>();//initialize symbols count arraylist
			numTokens = 0;//initialize numTokens with 0
		}
		for(E note: data)//for each note, Note is just a token
		{
			if(tokens.contains(note))// if tokens contains this pitch or rhythm
			{
				int index = tokens.indexOf(note); //set index variable to the index of that pitch or rhythm
				symbolsCount.set(index, symbolsCount.get(index) + 1);//set the index of symbols count to the value at the index + 1
				
			}
			else //if tokens does not have the pitch or rhythm
			{
				tokens.add(note); // add the pitch or rhythm
				symbolsCount.add(1.0f);// add one appearance to symbols count
			}
			numTokens++;//increment numtokens once because we've seen any of the tokens 
		}
	}

	E generate( ArrayList<E> symbols, ArrayList<Float> counts, int numTokens) //declares a new method of generate that returns a token
	{
		E token = null; //initializes a single token with null
		float rIndex = (float) Math.random();//sets the random index value
		
		if(symbols.size() <= 0)
		{
			System.out.println("help");
		}
		for(int i = 0; i < symbols.size(); i++)//iterate through symbols
		{
			if(rIndex < counts.get(i) / (double)numTokens)//if the random index is less than the probability 
			{
				token = symbols.get(i);//initialize token with i and break the loop
				break;
			}
			else //if random index is greater than the probability
			{
				rIndex -= counts.get(i) / (double)numTokens;//subtract random index with the probability you had and then it will recheck the next probability
			}
		}
		return token;
	}

	ArrayList <E> generate(int x) //overloads the generate function to return an ArrayList of tokens
	{
		ArrayList<E> genTokens = new ArrayList<E>();//initialize an ArrayList of tokens
		for(int i = 0; i < x; i++)//iterate through the loop for as many notes as you want to generate
		{
			genTokens.add(generate(tokens, symbolsCount, numTokens));//add each token you generate from the above function to the ArrayList of tokens
		}

		return genTokens;
	}

	E generateOneToken() //overloads the generate function to return an single tokens
	{
		return generate(tokens, symbolsCount, numTokens); // generates a single token
	}
	
	//nested convenience class to return two arrays from sortArrays() method
	//students do not need to use this class
	protected class SortArraysOutput
	{
		public ArrayList<E> symbolsListSorted;
		public ArrayList<Float> symbolsCountSorted;
	}

	//sort the symbols list and the counts list, so that we can easily print the probability distribution for testing
	//symbols -- your alphabet or list of symbols (input)
	//counts -- the number of times each symbol occurs (input)
	//symbolsListSorted -- your SORTED alphabet or list of symbols (output)
	//symbolsCountSorted -- list of the number of times each symbol occurs inorder of symbolsListSorted  (output)
	public SortArraysOutput sortArrays(ArrayList<E> symbols, ArrayList<Float> counts)	{

		SortArraysOutput sortArraysOutput = new SortArraysOutput(); 
		
		sortArraysOutput.symbolsListSorted = new ArrayList<E>(symbols);
		sortArraysOutput.symbolsCountSorted = new ArrayList<Float>();
	
		//sort the symbols list
		Collections.sort(sortArraysOutput.symbolsListSorted, new Comparator<E>() {
			@Override
			public int compare(E o1, E o2) {
				return o1.toString().compareTo(o2.toString());
			}
		});

		//use the current sorted list to reference the counts and get the sorted counts
		for(int i=0; i<sortArraysOutput.symbolsListSorted.size(); i++)
		{
			int index = symbols.indexOf(sortArraysOutput.symbolsListSorted.get(i));
			sortArraysOutput.symbolsCountSorted.add(counts.get(index));
		}

		return sortArraysOutput;

	}
	
	//Students should USE this method in your unit tests to print the probability distribution
	//HINT: you can overload this function so that it uses your class variables instead of taking in parameters
	//boolean is FALSE to test train() method & TRUE to test generate() method
	//symbols -- your alphabet or list of symbols (input)
	//counts -- the number of times each symbol occurs (input)
	//sumSymbols -- the count of how many tokens we have encountered (input)
	public void printProbabilityDistribution(boolean round, ArrayList<E> symbols, ArrayList<Float> counts, double sumSymbols)
	{
		//sort the arrays so that elements appear in the same order every time and it is easy to test.
		SortArraysOutput sortResult = sortArrays(symbols, counts);
		ArrayList<E> symbolsListSorted = sortResult.symbolsListSorted;
		ArrayList<Float> symbolsCountSorted = sortResult.symbolsCountSorted;

		System.out.println("-----Probability Distribution-----");
		
		for (int i = 0; i < symbols.size(); i++)
		{
			if (round){
				DecimalFormat df = new DecimalFormat("#.##");
				System.out.println("Data: " + symbolsListSorted.get(i) + " | Probability: " + df.format((double)symbolsCountSorted.get(i) / sumSymbols));
			}
			else
			{
				System.out.println("Data: " + symbolsListSorted.get(i) + " | Probability: " + (double)symbolsCountSorted.get(i) / sumSymbols);
			}
		}
		
		System.out.println("------------");
	}
	public void printProbabilityDistribution(boolean round)
	{
		printProbabilityDistribution(round, tokens, symbolsCount, numTokens);
	}
}
