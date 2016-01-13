//Alexander Chatron-Michaud
//260611509

import java.util.*;
import java.io.*;

// This class implements a google-like search engine
public class searchEngine {

	public HashMap<String, LinkedList<String> > wordIndex; 
    // this will contain a set of pairs (String, LinkedList of Strings)	
    public directedGraph internet;             // this is our internet graph
    // Constructor initializes everything to empty data structures
    // It also sets the location of the internet files
    searchEngine() {
	// Below is the directory that contains all the internet files
    	htmlParsing.internetFilesLocation = "internetFiles";
    	wordIndex = new HashMap<String, LinkedList<String> > ();		
    	internet = new directedGraph();				
    } // end of constructor2015
    // Returns a String description of a searchEngine
    public String toString () {
    	return "wordIndex:\n" + wordIndex + "\ninternet:\n" + internet;
    } 
    // This does a graph traversal of the internet, starting at the given url.
    // For each new vertex seen, it updates the wordIndex, the internet graph,
    // and the set of visited vertices.
    
    void traverseInternet(String url) throws Exception {

    	internet.addVertex(url);
    	internet.setVisited(url, true);
    	LinkedList<String> pageText = htmlParsing.getContent(url);
    	LinkedList<String> neighbors = htmlParsing.getLinks(url);
    	Iterator<String> i = pageText.iterator();
    	while (i.hasNext()) {
    		String nextWord = i.next();
			if (wordIndex.containsKey(nextWord)) { //word is in index
				if ((wordIndex.get(nextWord).contains(url) != true)) { //but url is not currently there
					wordIndex.get(nextWord).addLast(url); //add the url to that word
				}
			}
			else { //word hasn't been logged yet, so we should add it and the url to it
				LinkedList<String> nextWordList = new LinkedList<String>();
				nextWordList.addLast(url);
				wordIndex.put(nextWord, nextWordList); //put the new word in the word index with the linked list of sites
			}
		}
		//we've added all the words in this url to the word index, now we need to add vertex/edges to graph
		Iterator<String> j = neighbors.iterator();
		while (j.hasNext()) {
			String siteName = j.next();
			internet.addEdge(url, siteName);
			//now we can recursively call the traversal method on the websites this is linked to
			if (internet.getVisited(siteName) != true) {
				traverseInternet(siteName);
			}
		}
 	} /* end of traverseInternet. Note: The hints were very helpful, but it took a really long time for me
		 understand which methods to use from the classes we were given */

    void computePageRanks() {
       	LinkedList<String> vtc = internet.getVertices();
       	Iterator<String> i = vtc.iterator();
       	while (i.hasNext()) {  //first we need to initialize all the page ranks at 1
 			internet.setPageRank(i.next(),1);
 		}
 		//we're done initializing the page ranks, so we can calculate:
 		int j = 0;
 		while (j < 100)  { //we need to do the calculation 100 times for it to converge
 			Iterator<String> k = vtc.iterator();
 			while (k.hasNext()) { //calculate for all the vertices
 				String nextSite = k.next();
 				double currentRank = 0;
 				currentRank += 0.5;
 				//we now need to go through all of its edges point toward it
 				Iterator<String> pointingTo = internet.getEdgesInto(nextSite).iterator(); //thank god for this method
 				while (pointingTo.hasNext()) { //perform the formula on neighbors for this vertex
 					String z = pointingTo.next(); //realized you have to set a string here, if you call pointingTo.next() too many times it'll skip far ahead
 					currentRank += 0.5*(internet.getPageRank(z) / internet.getOutDegree(z));
 				}
 				internet.setPageRank(nextSite,currentRank);
 			}
 			j++;
 		}
    } /* end of computePageRanks. This sometimes takes a long time but maybe it's just cause
		 computer is slow. I wasn't sure if there was a way to do this faster with recursion
		 but I spent an hour trying to get that to work and it just didn't. */
    
    String getBestURL(String query) {

    	query = query.toLowerCase(); //i am assuming it should not be case sensitive. don't know about punctuation but nothing was stated about it... i asked on the message boards
    	if (!(wordIndex.containsKey(query))) { //check if word even exists
    		return "(Nothing was found)";							//if not just return empty string
    	}
    	LinkedList<String> websiteList = wordIndex.get(query);
    	Iterator<String> i = websiteList.iterator();
    	String answer = "";
    	double answerPower = -1;
    	while (i.hasNext()) {
    		String currentPossibleAnswer = i.next();
    		if (internet.getPageRank(currentPossibleAnswer) > answerPower) {
    			answer = currentPossibleAnswer;
    			answerPower = internet.getPageRank(currentPossibleAnswer);
    		}
    	}
    	//System.out.println(answerPower);
		return answer;
    } // end of getBestURL
    
    

    public static void main(String args[]) throws Exception, IOException {

        System.out.println("Alexander Chatron-Michaud 260611509");
        System.out.println("I begrudgingly worked on this assignment independently");
    	System.out.println("Please wait for prompt while program loads..."); 
        System.out.println("(Expect some 403 errors on dead links while loading)");	
    	searchEngine mySearchEngine = new searchEngine();
	// to debug your program, start with.
		//mySearchEngine.traverseInternet("http://www.cs.mcgill.ca/~blanchem/250/a.html");

	// When your program is working on the small example, move on to
    	mySearchEngine.traverseInternet("http://www.cs.mcgill.ca");

	// this is just for debugging purposes. REMOVE THIS BEFORE SUBMITTING
    	//System.out.println(mySearchEngine);

    	mySearchEngine.computePageRanks();

        System.out.println("\nLoaded! \n");

    	BufferedReader stndin = new BufferedReader(new InputStreamReader(System.in));
    	String query;
    	do {
    		System.out.print("Enter query: ");
    		query = stndin.readLine();
    		if ( query != null && query.length() > 0 ) {
    			System.out.println("Best site = " + mySearchEngine.getBestURL(query));
    		}
    	} while (query!=null && query.length()>0);				
    } // end of main
}
