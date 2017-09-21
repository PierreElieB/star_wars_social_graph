
import java.util.LinkedList;
import java.util.Stack; 
import org.jsoup.Jsoup;

import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.io.File;


public class PageExploration implements Runnable {
	
	public WookiePage w; 
	boolean toDo; 
	int failures;									// how many times a request has been sent and has failed. 
	public final int TRIESBEFOREFAILURE = 5; 
	
	PageExploration(WookiePage w2){
		w = w2; 
		failures = 0;
	}
	
	//@SuppressWarnings("unused")
	public void run(){
		if(Manager.USELOGGER) MyLogger.fine("Thread "+Thread.currentThread().getName()+ "launched");
		PageParser d = new PageParser(w.url);
		
		// We send requests to the server till we success or till we reach the maximum number of allowed failures. 
		do{
			d.readPage();  		// Request sent to the server. 
			if(d.failed){
				failures++;
				if(Manager.USELOGGER) MyLogger.finer("Thread " + Thread.currentThread().getName() + " : One request failed ! Trying again...");
			}
		}
		while((d.failed)&&(failures<TRIESBEFOREFAILURE));
		
		synchronized(Manager.requestsfailed){
			Manager.requestsfailed[w.depth]+=failures;
		}
		
		if (failures>=TRIESBEFOREFAILURE){
			synchronized(Manager.failuresperstep){
				Manager.failuresperstep[w.depth]++;
				if(Manager.USELOGGER) MyLogger.info("Thread " + Thread.currentThread().getName() + " : request "+ w.url + " failed "+ failures +" times ! I give up.");
			}
		}
		
		if(!(d.failed)){		// Success : the server have answered. 
			
			w.name = d.pageName();  		
			w.isCharacter = d.isCharacter();
			
			if(!(w.isCharacter)){
				if(Manager.USELOGGER) MyLogger.fine("Thread "+Thread.currentThread().getName()+" found"+ w.name + " which is not a character");
			}
			if(w.isCharacter){									// If the page corresponds to a character : we push it in the impression stack and explore the links of the page. 
				if(Manager.PRINT_CHARACTER){					
					synchronized(Manager.impression){
						Manager.impression.push(w); 
					}
				}
				Elements c = d.getSons();  
				if(Manager.USELOGGER) MyLogger.fine("Thread "+Thread.currentThread().getName()+" found"+w.name + " with "+ c.size()+"sons");				
				for(Element ee : c){						// We fill the shared stack with the Wookiepages to be visited. 
					String s = ee.attr("abs:href");  
					
					boolean addToVisit; 
					WookiePage ww = new WookiePage(s,s,w.depth+1);  
					
					synchronized(Manager.t){
						addToVisit = !(Manager.t.contains(ww)); 
						
						if(addToVisit){ 		
							Manager.t.add(ww); 
						}
					}
					
					if(addToVisit){
						synchronized(Manager.toVisit){
							Manager.toVisit.push(ww); 
						}
					}
				}
			}			
		}		
		

		synchronized(Manager.canal){
			Manager.canal[w.depth]--; 
			int l = Manager.canal[w.depth]; 
			if(Manager.USELOGGER) MyLogger.fine("l="+l);
			if(Manager.canal[w.depth] < 1 ){ 			// We give a signal. 
				Manager.canal.notifyAll(); 
			}
		}
		
		synchronized(Manager.toVisit){
			Manager.workingThreads--; 
			Manager.toVisit.notifyAll(); 		// We give a signal : because this thread is going to be finished with its work, one another thread could take its place and starts the work. 
		}
	}
}

