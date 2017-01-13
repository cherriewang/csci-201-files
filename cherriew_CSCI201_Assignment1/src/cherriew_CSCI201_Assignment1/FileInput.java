package cherriew_CSCI201_Assignment1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;
public class FileInput {

	private static String[] files = {"a", "b", "c"};
	private static File qwerty;
	private static File wordlist;
	private static File textFile;
	private static Trie allWords; 
	private static HashMap<Character, Vector<Character> > keyboard;
	private static HashMap<String, Vector<String> > topTen;
	//private static ArrayList<String> suggestions;
	
	// setters for the files
	public static void setFile1(){
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter file name 1: ");
		files[0] = scanner.nextLine();
		scanner.close();
	}
	public static void setFile2(){
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter file name 2: ");
		files[1] = scanner.nextLine();
		scanner.close();
	}
	public static void setFile3(){
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter file name 3: ");
		files[2] = scanner.nextLine();
		scanner.close();
	}
	
	// function that makes the files
	public static void makeFiles(int size_) {
		// makes files 
		for(int i=0; i<size_; i++){
			// making qwerty
			if (files[i].endsWith(".kb")){
				qwerty = new File(files[i]);
			}
			// making wordlist
			if(files[i].endsWith(".wl")){
				wordlist = new File(files[i]);
			}
			// making textFile
			if(files[i].endsWith(".txt")){
				textFile = new File(files[i]);
			}
		}
	}
	
	// PUBLIC API FROM ROSETTA CODE.ORG
	// @ Content is available under GNU Free Documentation License 1.2 unless otherwise noted.
    public static int distance(String a, String b) {
        a = a.toLowerCase();
        b = b.toLowerCase();
        // i == 0
        int [] costs = new int [b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }
	
    // adapted from Wikipedia
    // implementation of mergesort for Java arrays
    public static Pair[] mergeSort(Pair array[])
	 // pre: array is full, all elements are valid integers (not null)
	 // post: array is sorted in ascending order (lowest to highest)
	 {
	 	// if the array has more than 1 element, we need to split it and merge the sorted halves
	 	if(array.length > 1)
	 	{
	 		// number of elements in sub-array 1
	 		// if odd, sub-array 1 has the smaller half of the elements
	 		// e.g. if 7 elements total, sub-array 1 will have 3, and sub-array 2 will have 4
	 		int elementsInA1 = array.length / 2;
	 		// we initialize the length of sub-array 2 to
	 		// equal the total length minus the length of sub-array 1
	 		int elementsInA2 = array.length - elementsInA1;
	                 // declare and initialize the two arrays once we've determined their sizes
	 		Pair arr1[] = new Pair[elementsInA1];
	 		Pair arr2[] = new Pair[elementsInA2];
	 		// copy the first part of 'array' into 'arr1', causing arr1 to become full
	 		for(int i = 0; i < elementsInA1; i++)
	 			arr1[i] = array[i];
	 		// copy the remaining elements of 'array' into 'arr2', causing arr2 to become full
	 		for(int i = elementsInA1; i < elementsInA1 + elementsInA2; i++)
	 			arr2[i - elementsInA1] = array[i];
	 		// recursively call mergeSort on each of the two sub-arrays that we've just created
	 		// note: when mergeSort returns, arr1 and arr2 will both be sorted!
	 		// it's not magic, the merging is done below, that's how mergesort works :)
	 		arr1 = mergeSort(arr1);
	 		arr2 = mergeSort(arr2);
	 		
	 		// the three variables below are indexes that we'll need for merging
	 		// [i] stores the index of the main array. it will be used to let us
	 		// know where to place the smallest element from the two sub-arrays.
	 		// [j] stores the index of which element from arr1 is currently being compared
	 		// [k] stores the index of which element from arr2 is currently being compared
	 		int i = 0, j = 0, k = 0;
	 		// the below loop will run until one of the sub-arrays becomes empty
	 		// in my implementation, it means until the index equals the length of the sub-array
	 		while(arr1.length != j && arr2.length != k)
	 		{
	 			// if the current element of arr1 is less than current element of arr2
	 			if(arr1[j].getVal() < arr2[k].getVal())
	 			{
	 				// copy the current element of arr1 into the final array
	 				array[i] = arr1[j];
	 				// increase the index of the final array to avoid replacing the element
	 				// which we've just added
	 				i++;
	 				// increase the index of arr1 to avoid comparing the element
	 				// which we've just added
	 				j++;
	 			}
	 			// if the current element of arr2 is less than current element of arr1
	 			else
	 			{
	 				// copy the current element of arr2 into the final array
	 				array[i] = arr2[k];
	 				// increase the index of the final array to avoid replacing the element
	 				// which we've just added
	 				i++;
	 				// increase the index of arr2 to avoid comparing the element
	 				// which we've just added
	 				k++;
	 			}
	 		}
	 		// at this point, one of the sub-arrays has been exhausted and there are no more
	 		// elements in it to compare. this means that all the elements in the remaining
	 		// array are the highest (and sorted), so it's safe to copy them all into the
	 		// final array.
	 		while(arr1.length != j)
	 		{
	 			array[i] = arr1[j];
	 			i++;
	 			j++;
	 		}
	 		while(arr2.length != k)
	 		{
	 			array[i] = arr2[k];
	 			i++;
	 			k++;
	 		}
	 	}
	 	// return the sorted array to the caller of the function
	 	return array;
	 }
    
    // this guy is a doozy w o w
    // works recursively to find a solution
	public static void swapper(String word, ArrayList<String> s, int index){
		// loop through all characters of this word
		for(int i = index; i < word.length(); i++){
			System.out.println("WORKING ON CHARACTER " + i + " OF WORD " + word);
			
			// generate vector of chars adjacent to the char i'm at
			Vector<Character> change;
			change = keyboard.get(word.charAt(i));
			
			// loop through each char in the adjacent vector
			for(int j = 0; j < change.size(); j++){
				
				// replace the char i'm at with the char in the adjacent vector
				String newTempWord = word.substring(0, i) + change.get(j) + word.substring(i+1, word.length());
				System.out.println("ntw:" + newTempWord);

				
				// check if the new word created is an actual word
				if(allWords.isEntry(newTempWord) == true){
					if (s.contains(newTempWord) == false){
						s.add(newTempWord);
					}		
				}
				
				// check if this new word is a prefix of anything
				String[] goodPrefix = allWords.suggest(newTempWord);
				for(int k = 0; k < goodPrefix.length; k++) {
					if (s.contains(goodPrefix[k]) == false && allWords.isEntry(goodPrefix[k])) {
						s.add(goodPrefix[k]);
					}
				}
								
				// recursive call w newly swapped word
				//System.out.println("going into recursive call with: " + word);
				String [] newPrefix = allWords.suggest(newTempWord.substring(0, i+1));
				if (newPrefix.length > 1) { 
					boolean has = false;
					for (String test : newPrefix) { if (s.contains(test)) { has = true; }}
					if (!has) swapper(newTempWord, s, index+1); 
				} else {
					//System.out.println("No words with prefix: " + newTempWord.substring(0, i+1));
				} 
			}
		}
	}
	
	
	public static void autoCorrect(String word){
		
		ArrayList<String> suggestions = new ArrayList<String>();
		
		// TEST PRINT OUT
		System.out.println("-----------> word being corrected: " + word);
		
		// AUTO COMPLETE SOLUTIONS
		String[] goodPrefix = allWords.suggest(word);
		for(int i = 0; i < goodPrefix.length; i++) {
			if (suggestions.contains(goodPrefix[i]) == false && allWords.isEntry(goodPrefix[i])) {
				suggestions.add(goodPrefix[i]);
			}
		}
		
		swapper(word, suggestions, 0);
		
		// make a vector here that we can hash into our topTen hash map
		Vector<Pair> toSort = new Vector<Pair>();
		
		// printing out what we have in our suggestions so far
		for(int i = 0; i < suggestions.size(); i++){
			// TESTING
			//System.out.println("IN SUGGESTIONS: " + suggestions.get(i));
			// make a pair with the word and its rank
			int val; 
			// call function to calculate ranking for the word we're at
			val = distance(suggestions.get(i), word);
			//System.out.println("word: " + word + ":\t" + val);
			Pair ranked = new Pair(suggestions.get(i), val);
			toSort.addElement(ranked);
			if (i == suggestions.size()-1) System.out.println("Total suggestions:" + i);
		}
		
		
		// vector is full here! we need to sort it and then put it into hash map
		// convert it to an array of strings
		Pair[] temp = toSort.toArray(new Pair[toSort.size()]);
		//Vector<Pair> vector = new Vector<Pair>(Arrays.asList(temp));
		//Collections.sort(toSort);
		
		// sort it here
		temp = mergeSort(temp);
		
		// was temp sorted properly?
//		for(int i=0; i<temp.length; i++){
//			System.out.println("should be sorted: " + temp[i].getKey());
//		}
		
		
		Vector<String> justTen = new Vector<String>();
		for(int i = 0; i < 10; i++){
			if (i < temp.length) {
				justTen.addElement(temp[i].getKey());
				System.out.println("IN TOP TEN VECTOR: " + temp[i].getKey());
			}
		}
		
		
		// inserts it into the final hash map
		topTen.put(word, justTen);
	}
	
	// main takes in arguments
	public static void main (String[] args) {
		
		// checking the correct num of arguments
		int size = args.length;
		if (size == 0){
			System.err.println("Must enter three filenames!");
			setFile1();
			setFile2();
			setFile3();
		}
		else if (size == 1){
			System.err.println("Must enter three filenames!");
			files[0] = args[0];
			setFile2();
			setFile3();
		}
		else if (size == 2){
			System.err.println("Must enter three filenames!");
			files[0] = args[0];
			files[1] = args[1];
			setFile3();
		}
		else if (size == 3){
			// we have the correct number of arguments
			files = args;
		}
		
		// TESTING: PRINTING OUT FILE NAMES		
//		System.out.println(files[0]);
//		System.out.println(files[1]);
//		System.out.println(files[2]);
		
		// calls make files function
		makeFiles(size);
		
		// check that all of the files exists 
		if (qwerty.exists() == false){
			System.err.println("File 1 not found.");
			setFile1();
			makeFiles(size);
		}
		if (wordlist.exists() == false){
			System.err.println("File 2 not found.");
			setFile2();
			makeFiles(size);
		}
		if (textFile.exists() == false){
			System.err.println("File 3 not found.");
			setFile3();
			makeFiles(size);
		}
	
		// put all the words in wordlist into the trie
		allWords = new Trie();

		try (BufferedReader br = new BufferedReader(new FileReader(wordlist))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		       // process the line.
		    	allWords.add(line);
		    }
		} catch (FileNotFoundException e1) {
			System.err.println("End of file.");
		} catch (IOException e1) {
			System.err.println("Keyboard file not found.");
		}
		
		// TESTING THE TRIE
//		if(allWords.isEntry("promiscuities") == true) {
//			System.out.println("good");
//		} else {
//			System.out.println("BAD");
//		}
		
		
		// put all the words in qwerty into a hash map
		keyboard = new HashMap<Character, Vector<Character> >();
		 
		FileReader fileReader; 
		try {
			fileReader = new FileReader(qwerty);
			BufferedReader br = new BufferedReader(fileReader);

			String line = null;
			 // if no more lines the readLine() returns null
			try {
				// read line in while not the end of file
				while ((line = br.readLine()) != null) {	
					// make the value the first character
					char k = line.charAt(0);
					// create a vector of adj values
					Vector<Character> v = new Vector<Character>(10,2);
					for (int i=2; i<line.length(); i++) {	
						v.addElement(line.charAt(i));
					}
					// insert into the hash map
					keyboard.put(k, v);
				}
			} catch (IOException e) {
				// mandatory catch thing
				System.err.println("End of file.");
			}
			
		} catch (FileNotFoundException e) {
			// other mandatory catch thing
			System.err.println("Keyboard file not found.");
		}
		
		// TEST HASHMAP
//		Set<Character> keys = keyboard.keySet();
//        for(Character key: keys){
//            System.out.println("KEY: " + key);
//            Vector<Character> tmp = keyboard.get(key);
//            for(int i=0; i<tmp.size(); i++){
//            	System.out.println(tmp.elementAt(i));
//            }
//        }

			
		// make the hash map here that all my words will be using
		topTen = new HashMap<String, Vector<String> >();
		
		ArrayList<String> wrongs = new ArrayList<String>();
		// going through the words in text file
		Scanner nxtWord;
		try {
			nxtWord = new Scanner(textFile);
			String s; 
			while(nxtWord.hasNext()) 
			{ 
				s = nxtWord.next(); 
				s = s.replaceAll("[^a-zA-Z]", "").toLowerCase();
				// check whether s is a word
				if(allWords.isEntry(s) != true){
					// if its not a word, we need to make suggestions!
					// calling function
					//allWords.suggest(s);
					// call the function that does that juicy auto-correct
					wrongs.add(s);
				}
			} 
			
		} catch (FileNotFoundException e1) {
			System.err.println("Text file not found.");
		} 
		
		System.out.println("All wrong words:");
		for (String s : wrongs) {
			System.out.println(s);
		}
		
		for (String s : wrongs) {
			autoCorrect(s);
		}
		
		// going to create an output stream, output file here
		try (Writer writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream("src/cherriew_CSCI201_Assignment1/output.txt"),"utf-8"))) {
			
			for (HashMap.Entry<String, Vector<String> > entry : topTen.entrySet()) {
				  String key = entry.getKey();
				  Vector<String> value = entry.getValue();
				  // loop through the values vector and put them all in one string
				  String buildMe = new String();
				  for(int j=0; j<value.size(); j++){
					  buildMe = buildMe + " " + value.elementAt(j);
				  }
				  
				  writer.write(key + " - " + buildMe + '\n');
				  System.out.println(key + " - " + buildMe);
			}
			
			//writer.write("something");
		} catch (IOException e) {
			System.err.println("Keyboard file not found.");
		}
		
		
		// TESTING!!!
		// printing out what we have in suggestions now
//		System.out.println("---------------------------SUGGESTIONS FINAL---------------------------");
//		for(int i=0; i<suggestions.size(); i++){
//			System.out.println("IN SUGGESTIONS: " + suggestions.get(i));
//		}
		
		
		// exiting gracefully...
		System.exit(1);
	}
}