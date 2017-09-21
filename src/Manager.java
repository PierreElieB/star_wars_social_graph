
import java.util.LinkedList;
import java.util.Stack;

import org.jsoup.Jsoup;
import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.File;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.HashSet; 
import java.util.Iterator; 

public class Manager {
	
	private static int depth; 									//maximum search depth
	public static Stack<WookiePage> impression; 			  	// A shared impression stack. It will be emptied at the end of each round to print the names of the characters situated at a k-distance from the root character.				  
	public static Stack<WookiePage> toVisit; 					// A shared Stack. It contains the WookiePages to be visited for the next step.
	private static Stack<Thread> toLaunch; 						// A stack used only by the Manager class. It contains the threads to launch. 
	public static TreeSet<WookiePage> t; 						// Binary Search Tree containing the informations about all the already visited WookiePages. 
	private static TreeSet<String> alreadyImpressed; 			// This is to avoid to print some characters several times. 
	public static int[] canal; 									// Shared array which counts the number of threads who haven't finished their jobs yet.
	public static int[] failuresperstep ;						// Shared array which counts the number of failures at each step. 
	public static int[] requestsfailed ;						// Shared array which counts the number af failed requests.
	private static int numberofcharacters ;
	public static int workingThreads; 
	
	final static boolean USEPROXY = true ;						// True if you use a proxy, false elseway.
	final static int DEPTH = 10;																// Search depth 
	final static boolean PRINT_CHARACTER = true ;										//true if you would like to have the answer to the question of the exercise
	final static String ROOT_URL = "http://starwars.wikia.com/wiki/Anakin_Solo";			// The url adress of the departure character (this is one and entry from the user). 
	final static boolean USELOGGER = false; 													//True if you would like to use a logger
	final static boolean PRINTMOREINFORMATIONS = false; 		// True if you would like some additional informations to be printed. 
	final static int MAXIMAL = 150; 
	final static boolean PRINTMOREMORE = false; 
	
	void SetProxy(){
		System.setProperty("http.proxyHost", "kuzh.polytechnique.fr");
		System.setProperty("http.proxyPort", "8080");
		System.setProperty("https.proxyHost", "kuzh.polytechnique.fr");
		System.setProperty("https.proxyPort", "8080");
	}
		
	public Manager(int d, String rootUrl){ 
				
		if(USELOGGER) new MyLogger();
		if (USEPROXY) SetProxy();
		depth=d; 
		canal = new int[depth+3]; 
		impression = new Stack<WookiePage>();   
		toVisit = new Stack<WookiePage>(); 
		alreadyImpressed = new TreeSet<String>(); 
		WookiePage root = new WookiePage(rootUrl,rootUrl,0);
		t = new TreeSet<WookiePage>();
		t.add(root); 
		failuresperstep = new int[depth+3];
		requestsfailed = new int[depth+3];
		workingThreads = 0; 
		
		if(USELOGGER) MyLogger.info("Launching search : exploring root page");
		
		synchronized(toVisit){			// The root will be the first visited website. 
			toVisit.add(root); 
		}
		
			
			
		if(USELOGGER) MyLogger.fine("Launching PBFS"); // This is the beginning of the search.
		
		int threadsLaunched;
		
		for(int k=0; k<depth; k++){
			
			synchronized(canal){
				canal[k] = 0; 
			}
			
			toLaunch = new Stack<Thread>(); 

			
			if(USELOGGER) MyLogger.info("Depiling pages to visit, depth level = " + k +"/"+depth);
			
			synchronized(toVisit){			// The waiting threads (corresponding to websites to be visited) are popped from the stack and launched. 
				
				threadsLaunched = 0; 		// Counts the number of threads we'll launch at this round. 
				
				while(!(toVisit.isEmpty())){
					
					WookiePage w = toVisit.pop();
					boolean b = t.contains(w);    
					
					if((b)||(k==0)){
						Thread tt = new Thread(new PageExploration(w)); 
						toLaunch.push(tt); 
						canal[k]++; 
					}
				}
						
				while(!(toLaunch.isEmpty())){
					Thread tt=toLaunch.pop(); 
				
					while(workingThreads>=MAXIMAL){		// Too much threads running. We have to wait that some thread finishes before we launch the new one. 
							try{toVisit.wait(); }
								catch(Exception e){}
							}
						tt.start(); 						// Now, there aren't too many threads running : we could launch the new one. 
						workingThreads++; 					// Take note that workingThreads (a shared counter) is protected by the lock (toVisit). 
						threadsLaunched++; 
						//System.out.println("Threads launched : "+threadsLaunched);
					}
				}		
			
				if(USELOGGER) MyLogger.fine("All threads for page exploration launched");
			
				// Waiting for the launched threads. 
			
				synchronized(canal){
					while(canal[k]>0){
						try{
							canal.wait(); 
							}
						catch(InterruptedException f){
							if(USELOGGER) MyLogger.severe("Error in main thread while waiting for other threads to finish. Details : " + f.getMessage());
						}
					}
				}
			
				int numberOfCharactersInRound;
			
				// We print the name and the distance of the characters found at this step. 
			
				synchronized(impression){   
					numberOfCharactersInRound = impression.size();
				
					if (PRINT_CHARACTER){
					
						TreeSet<String> ss = new TreeSet<String>(); 

						while(!(impression.isEmpty())){
							String s = impression.pop().name; 
							String q = s+" : "+k; 
							if(!(alreadyImpressed.contains(s))){	// If we have not printed s yet : we put print it and we put it in alreadyImpressed. 
								alreadyImpressed.add(s); 
								ss.add(q); 
							}
						}
					
						for(String blabl : ss){
							System.out.println(blabl); 
						}
					}
					else impression.clear();
				}
				numberofcharacters = numberofcharacters + numberOfCharactersInRound;
			
				// We print some additional informations 
			
				if(PRINTMOREINFORMATIONS){
					if(USELOGGER) {MyLogger.info("Round "+ k +" ended");} else {System.out.println("Round "+ k +" ended"); }
					if(USELOGGER) {MyLogger.info(threadsLaunched +" threads launched = new pages explored");}else{System.out.println(threadsLaunched +" threads launched = new pages explored"); }
					synchronized(requestsfailed){
						if(USELOGGER) {MyLogger.info(requestsfailed[k] + " server requests failed");}else{System.out.println(requestsfailed[k] + " server requests failed");}
					}
					synchronized(failuresperstep){
						if(USELOGGER) {MyLogger.info(failuresperstep[k] + " pages not reached and failed");} else{System.out.println(failuresperstep[k] + " pages not reached and failed");}
					}
					if(USELOGGER) {MyLogger.info(numberOfCharactersInRound +" new characters found");} else{System.out.println(numberOfCharactersInRound +" new characters found");}
					if(USELOGGER) {MyLogger.info(t.size() + " pages in the tree in total");} else{System.out.println(t.size() + " pages in the tree in total");}
					if(USELOGGER) {MyLogger.info(numberofcharacters + " characters in total");} else{System.out.println(numberofcharacters + " characters in total");}
					if(USELOGGER) {MyLogger.info(toVisit.size() + " new links to explore found");} else{ System.out.println(toVisit.size() + " new links to explore found");}
			}			
		}
	}
	
	public static void main(String[] args){
		new Manager(DEPTH, ROOT_URL); 		// Does the required job and print the results. 
	}
}
