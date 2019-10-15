package indexer;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.*; 

/*
 * This file produces several text files, each of which answers part
 * of the assignment
 */


public class indexer {
	/*
	 * constructor
	 */
	public indexer() {
	}
	/*
	 * my maps
	 */
	public static HashMap<String,LinkedList<tuple>> tupleMap = new HashMap<String,LinkedList<tuple>>();
	public static HashMap<Integer,String> docMap = new HashMap<Integer,String>();
	public static ArrayList<Integer> HeapsUnique = new ArrayList<Integer>();
	public static ArrayList<Integer> HeapsCount = new ArrayList<Integer>();
	
	/*
	 * these fields store stats for my stat function
	 */
	static int totalDocs=0;
	static int totalWords=0;

	
	/*
	 * This class defines my tuples
	 */
	public static class tuple {
		public int termID;
		public int docID;
		public int posID;
		
		public tuple(int termID, int docID, int posID) {
			this.termID = termID;
			this.docID = docID;
			this.posID = posID;
		}
		
		@Override
		public String toString() {
			return "[" + this.termID + ", " + this.docID +  ", " + this.posID + "]";
		}
	}
	
	
	/*
	 * this function outputs a file called over1000.txt that contains the terms
	 * that have >1000 occurrences, and a count of these terms at the bottom of the file. 
	 * the count is also returned by this function
	 */
	public static int over1000() throws IOException {
		int count = 0;
		BufferedWriter writer = new BufferedWriter(new FileWriter("src/indexer/over1000.txt"));
		for (String key: tupleMap.keySet()) {
			if (tupleMap.get(key).size() > 1000) {
				writer.write(key + ": " + tupleMap.get(key).size() + "\n");				 
				count++;				
			}
		}
		writer.write("FINAL COUNT IS: " + count + "\n");
		writer.close();
		return count;
		
	}
	
	/*
	 * This function outputs a file called only1.txt that contains the terms that 
	 * have only 1 occurence, and the count of these terms at the bottom of the file
	 *and it also returns the count 
	 *
	 */
	public static int only1() throws IOException {
		int count = 0;
		BufferedWriter writer = new BufferedWriter(new FileWriter("src/indexer/only1.txt"));
		for (String key: tupleMap.keySet()) {
			if (tupleMap.get(key).size() ==1) {
				writer.write(key + ": " + tupleMap.get(key).size() + "\n");				 
				count++;				
			}
		}
		writer.write("FINAL COUNT IS: " + count + "\n");
		writer.close();
		return count;
		
	}
	
	/*
	 * This function outputs a file called stats.txt tht contains:
	 * Total # of Documents
	 * Total # of Words
	 * # of unique words
	 */
	public static int stat() throws IOException {
		int count = 0;
		BufferedWriter writer = new BufferedWriter(new FileWriter("src/indexer/stat.txt"));
		int numofWords = tupleMap.size();
		writer.write("Total number of Documents in Collection: " + totalDocs + "\n" );
		writer.write("Vocabulary size (# of unique words): " + numofWords + "\n"  );
		writer.write("Total number of words: " + totalWords + "\n" );
		writer.close();
		return count;
	}
	/*
	 * this function produces two text files which serve as the row and column
	 * for my Heaps law Figure found in the hw2.pdf
	 */
	public static int heaps() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("src/indexer/heapsunique.txt"));
		BufferedWriter writer2 = new BufferedWriter(new FileWriter("src/indexer/heapscount.txt"));
		for (int num: HeapsUnique) {
			writer.write(num + "\n");
		}
		for (int num: HeapsCount) {
			writer2.write(num + "\n");
		}		
		writer.close();
		writer2.close();
		return 1;
	}
	/*
	 * this function produces two text files which serve as the row and column
	 * for my Zipfs law Figure found in the hw2.pdf
	 */
	public static int zipfs() throws IOException {
		int i = 0;
		BufferedWriter writer = new BufferedWriter(new FileWriter("src/indexer/zip1.txt"));
		BufferedWriter writer2 = new BufferedWriter(new FileWriter("src/indexer/zip2.txt"));
		for (String term: tupleMap.keySet()) {
			if (i>1000) {
				break;
			}
			int freq = tupleMap.get(term).size();
			writer.write(term + "\n");
			writer2.write(freq + "\n");
			i++;
			
		}
		writer.close();
		writer2.close();
		return 1;
	}
	/*
	 * This function retrieves the title of a document given the doc #
	 */
	@SuppressWarnings("resource")
	public static String getTitle(String input) throws IOException {
		String[] DocNum = input.split("-");
		String filename = DocNum[0];
		
		String filepath = ("src/indexer/ap89_collection/" + filename);
		Path myPath = Paths.get(filepath);
		Stream<String> streamOfStrings;
		streamOfStrings = Files.lines(myPath, StandardCharsets.ISO_8859_1);
		StringBuilder fileStringBuilder = new StringBuilder();
		streamOfStrings.forEach(s -> fileStringBuilder.append(s).append("\n"));
		String fileString = fileStringBuilder.toString();
		streamOfStrings.close();
		
		String[] fileByDoc = fileString.split("<DOC>");
		String targetDoc = "";
		for (String Doc: fileByDoc) {
			if (Doc.contains(input)) {
				targetDoc = Doc;				
			}
		}
		String headDelim = "<HEAD>";
		String headDelimClose = "</HEAD>";
		String[] linesOfDoc = targetDoc.split("\n");
		for (String line : linesOfDoc) {
			if (line.contains(headDelim)) {
				String headclean = line.replace(headDelim, "");
				String headclean2 = headclean.replace(headDelimClose, "");
				return headclean2;
			}
		}
		return "-1";
	}
	/*
	 * this function takes the queries in queries.txt and finds which documents
	 * contain all of the terms in each query. 
	 */
	public static int search() throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter("src/indexer/results.txt"));
		String filepath = "src/indexer/queries.txt";
		Path myPath = Paths.get(filepath);
		//first i get my file into a stream of Lines
		Stream<String> streamOfStrings;
		streamOfStrings = Files.lines(myPath, StandardCharsets.ISO_8859_1);
		StringBuilder fileStringBuilder = new StringBuilder();
		streamOfStrings.forEach(s -> fileStringBuilder.append(s).append("\n"));
		String queries = fileStringBuilder.toString();
		streamOfStrings.close();
		String[] queryArray = queries.split("\n");
		//split into words
		for (String line: queryArray) {
			String[] words = line.split(" ");
			String word = words[0];
			String word2 = "";
			ArrayList<String> results = new ArrayList<String>();
			ArrayList<String> results2 = new ArrayList<String>();
			//check if its there
			if (tupleMap.get(word)==null && words.length ==1) {
				writer.write("Results for " + word + " are not found\n");
			}
			if (tupleMap.get(word)!=null) {
				LinkedList<tuple> tupleList = tupleMap.get(word);
				for (tuple tuple : tupleList) {
					String YourDoc = docMap.get(tuple.docID);
					results.add(YourDoc);				
				}				
			}
			//check second term
			if (words.length >1) {
				if (tupleMap.get(words[1]) != null) {
					word2 = words[1];
					LinkedList<tuple> tupleList2 = tupleMap.get(word2);
					for (tuple tuple : tupleList2) {
						String YourDoc = docMap.get(tuple.docID);
						results2.add(YourDoc);				
					}										
				}
			}
			
			//if both terms returned results i combine my list
			if (results.size() > 0 && results2.size() > 0) {
				results.retainAll(results2);
				writer.write("Found " + results.size() + " results for " + word + " " + word2 + "\n");
				if (results.size()>5) {
					for (int i = 0; i<5; i++) {
						String docno = results.get(i);
						String title = getTitle(docno);
						if (title != "-1") {
							writer.write(docno + ": " +title + "\n");
						}
						
					}
				}
				
			} 
			if (results.size()>0 && results2.size() ==0) {
				writer.write("Found " + results.size() + " results for " + word + "\n");
				if (results.size()>5) {
					for (int i = 0; i<5; i++) {
						String docno = results.get(i);
						String title = getTitle(docno);
						if (title != "-1") {
							writer.write(docno + ": " +title + "\n");
						}
						
					}
				}
				
			} 
			if (results2.size() >0 && results.size() ==0) {
				writer.write("Found " + results2.size() + " results for " + word2 + "\n");
				if (results2.size()>5) {
					for (int i = 0; i<5; i++) {
						String docno = results2.get(i);
						String title = getTitle(docno);
						if (title != "-1") {
							writer.write(docno + ": " +title + "\n");
						}
						
					}
				}				
			}												
		}
		writer.close();
		return 1;		
	}
	/*
	 * this function tokenizes a string of words
	 */
	public static ArrayList<String> tokenizer(String input) throws IOException {
		//first i import my stopwords
		List<String> stopwords = Files.readAllLines(Paths.get("src/indexer/stoplist.txt"));
		
		input = input.replaceAll("[^a-zA-Z ]", "");
		ArrayList<String> words = Stream.of(input.toLowerCase().split(" "))
	            .collect(Collectors.toCollection(ArrayList<String>::new));
		totalWords = totalWords + words.size();
	    words.removeAll(stopwords);
		return words;
	}
	/*
	 * This is my main function that does my indexing
	 */
	public static int Index() throws IOException {
		//first I need to get my Array of Files
		String folderPath = "src/indexer/ap89_collection";
		File folder = new File(folderPath);
		File[] directoryArray = folder.listFiles();
		
		//for every file in my directory
		for(File file: directoryArray) {
			//get path
			String path = file.getPath();
			Path myPath = Paths.get(path);
			//first i get my file into a stream of Lines
			Stream<String> streamOfStrings;
			String fileString = "";
			
			//stream from file to get a String from each file
			try {
				streamOfStrings = Files.lines(myPath, StandardCharsets.ISO_8859_1);
				StringBuilder fileStringBuilder = new StringBuilder();
				streamOfStrings.forEach(s -> fileStringBuilder.append(s).append("\n"));
				fileString = fileStringBuilder.toString();
				streamOfStrings.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	
			//Now I split by documents
			String docRX = "<DOC>";
			String[] DocArray = fileString.split(docRX);
			
			//termID
			int termID = 0;
			int docID = 0;
			for(String document: DocArray ) {
				//For each document I need a DOCNO 
				String DOCNO = "No Name";
				docID++;
				//split document into lines
				String[] lineArray = document.split("\n");
				String docDelim = "<DOCNO>";
				String docDelimClose = "</DOCNO>";
				String headDelim = "<HEAD>";
				String headDelimClose = "</HEAD>";
				
				for (String line: lineArray) {
					if (line.contains(docDelim)) {
						//remove tags from line
						String lineclean = line.replace(docDelim, "");
						String lineclean2 = lineclean.replace(docDelimClose, "");				
						DOCNO = lineclean2.trim();
						docMap.put(docID, DOCNO);
						totalDocs++;
					}
					//now I have my DOCNO I need to extract words from HEAD
					if(line.contains(headDelim)) {
						String headclean = line.replace(headDelim, "");
						String headclean2 = headclean.replace(headDelimClose, "");
						//removed the Eds tag
						String eds = "Eds:";
						String headclean3 = headclean2.replace(eds, "");
						String head = headclean3.trim();
						
						//now i need to tokenize
						ArrayList<String> words = tokenizer(head);
						ArrayList<String> uniquewords = new ArrayList<String>();
						int pos = 1;						
						for (String word: words) {
							if (uniquewords.contains(word)==false ) {
								uniquewords.add(word);
							}
							//if word is not in my map then i make a new list
							//and put onto my map
							if (tupleMap.get(word)== null) {
								//make new tuple list, add tuple, add to maps
								LinkedList<tuple> tupleList = new LinkedList<tuple>();
								tuple termTuple = new tuple(termID,docID, pos);
								tupleList.add(termTuple);
								tupleMap.put(word, tupleList);								
								termID++;								
								pos++;
								
							} else {
								//word exists on map, just need to add to it
								LinkedList<tuple> tupleList = tupleMap.get(word);
								int thisTermID = tupleMap.get(word).get(0).termID;
								tuple termTuple = new tuple(thisTermID,docID, pos);
								tupleList.add(termTuple);
								tupleMap.put(word, tupleList);
								pos++;
							}														
						} //end of line of words
						int uniq = uniquewords.size();
						HeapsUnique.add(uniq);
						int count = pos;
						HeapsCount.add(count);
					} //end of head section				
				}			
			}//end of documents
		}//end of directory
		return 1;
	}//end of index class
		

	
	/**
	 * Run the tests in this class.
	 * 
	 * @param args the program arguments
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		Index();
		
		//I get my terms with >1000 occurrences into a text file
		over1000();
		//Now my single occurrence terms
		only1();
		
		//now i generate my stat file
		stat();
		
		//next i generate my search results file
		search();
		
		//next I generate the text files for Heaps Law
		heaps();
		
		//lastly i generate the files for my Zipfs Law
		zipfs();


	}//end of main

}//end of indexer class
