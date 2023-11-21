package com.example;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import jm.music.tools.Prob;

public class MarkovChainGenerator<E> extends ProbabilityGenerator<E> {
	//ArrayList<E> tokens = new ArrayList<E>();
	ProbabilityGenerator<E> ProbGen = new ProbabilityGenerator<E>();
	ArrayList<ArrayList<Float>> transitionTable = new ArrayList<ArrayList<Float>>();
	int tokenCount = 0;
	void train(ArrayList<E> data)
	{
		ProbGen.train(data);

		int lastIndex = -1;
		for(E token: data)
		{
			int tokenIndex = tokens.indexOf(token); //sets tokenIndex to the index of the token in tokens
			if(!(tokens.contains(token)))
			{
				ArrayList<Float> myRow = new ArrayList<Float>(); //create a new row
				for(int i = 0; i < tokens.size(); i++)
				{
					myRow.add(0.0f); //initialize all values in that row with 0
				}
				transitionTable.add(myRow); //add the row to the table
				for (ArrayList<Float> row : transitionTable) { //creates a column and initializes all the values to 0
					row.add(0.0f);
				}
				tokens.add(token); //adds a token to the tokens arraylist
				tokenIndex = tokens.indexOf(token); 
			}
			if(lastIndex > -1) //if the last index is greater than -1
			{
				ArrayList<Float> row = transitionTable.get(lastIndex); //get a row from the table
				float element = row.get(tokenIndex);//set element to a specific index of the row
				element += 1.0f;//add 1 occurence to the element
				row.set(tokenIndex, element);//set the index of the specfic row gotten to the value of element
				
			}
			lastIndex = tokenIndex;//make last index equal to the index of the most recent token added to the 
			tokenCount++;//increments the tokenCount by 1
		}
		//System.out.println(transitionTable);
	}
	public ArrayList<ArrayList<Float>> normalize(ArrayList<ArrayList<Float>> table) //normalizes the table to be percentages
	{
		ArrayList<ArrayList<Float>> newTable = new ArrayList<ArrayList<Float>>();
		for(int i = 0; i < table.size(); i++)
		{
			float sumRow = 0;
			ArrayList<Float> row = new ArrayList<Float>();
			for(int j = 0; j < transitionTable.get(i).size(); j++)
			{
				sumRow += transitionTable.get(i).get(j);
			}
			for(int j = 0; j < transitionTable.get(i).size(); j++)
			{
				if(sumRow == 0)
				{
					row.add(0.0f);
				}
				else
					row.add(table.get(i).get(j)/sumRow);
			}
			newTable.add(row);
		}
		return newTable;
	}
	E generate(E initialToken)
	{
		float sum = 0;
		int initialTokenIndex = tokens.indexOf(initialToken);
		symbolsCount = transitionTable.get(initialTokenIndex);
		for(int i = 0; i < symbolsCount.size(); i++)
		{
			sum += symbolsCount.get(i);
		}
		if(sum == 0)
		{
			return ProbGen.generateOneToken();
		}
		return ProbGen.generate(tokens, symbolsCount, (int)sum);
	}
	ArrayList<E> generate(E initial, int numberOfTokensToGenerate)
	{
		E token = generate(initial);
		ArrayList<E> generatedTokens = new ArrayList<E>();
		for(int i = 0; i < numberOfTokensToGenerate; i++)
		{
			generatedTokens.add(token);
			token = generate(token);
			
		}
		return generatedTokens;
	}
  	//nested convenience class to return two arrays from sortTransitionTable() method
	//students do not need to use this class
	protected class SortTTOutput
	{
		public ArrayList<E> symbolsListSorted;
		ArrayList<ArrayList<Float>> ttSorted;
	}

	//sort the symbols list and the counts list, so that we can easily print the probability distribution for testing
	//symbols -- your alphabet or list of symbols (input)
	//tt -- the unsorted transition table (input)
	//symbolsListSorted -- your SORTED alphabet or list of symbols (output)
	//ttSorted -- the transition table that changes reflecting the symbols sorting to remain accurate  (output)
	public SortTTOutput sortTT(ArrayList<E> symbols, ArrayList<ArrayList<Float>> tt)	{

		SortTTOutput sortArraysOutput = new SortTTOutput(); 
		
		sortArraysOutput.symbolsListSorted = new ArrayList<E>(symbols);
		sortArraysOutput.ttSorted = new ArrayList<ArrayList<Float>>();
	
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
			sortArraysOutput.ttSorted.add(new ArrayList<Float>());
			for( int j=0; j<tt.get(index).size(); j++)
			{
				int index2 = symbols.indexOf(sortArraysOutput.symbolsListSorted.get(j));
				sortArraysOutput.ttSorted.get(i).add(tt.get(index).get(index2));
			}
		}

		return sortArraysOutput;

	}
	
	//this prints the transition table
	//symbols - the alphabet or list of symbols found in the data
	//tt -- the transition table of probabilities (not COUNTS!) for each symbol coming after another
	public void printProbabilityDistribution(boolean round, ArrayList<E> symbols, ArrayList<ArrayList<Float>> tt)
	{
		//sort the transition table
		SortTTOutput sorted = sortTT(symbols, tt);
		symbols = sorted.symbolsListSorted;
		tt = sorted.ttSorted;

		System.out.println("-----Transition Table -----");
		
		System.out.println(symbols);
		
		for (int i=0; i<tt.size(); i++)
		{
			System.out.print("["+symbols.get(i) + "] ");
			for(int j=0; j<tt.get(i).size(); j++)
			{
				if(round)
				{
					DecimalFormat df = new DecimalFormat("#.##");
					System.out.print(df.format((double)tt.get(i).get(j)) + " ");
				}
				else
				{
					System.out.print((double)tt.get(i).get(j) + " ");
				}
			
			}
			System.out.println();


		}
		System.out.println();
		
		System.out.println("------------");
	}
	public void printProbabilityDistribution(boolean round)
	{
		printProbabilityDistribution(round, tokens, normalize(transitionTable));
	}
}
