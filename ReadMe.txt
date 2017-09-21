Read Me : 

Projet INF431 - Star Wars social graph

Melchior d'Harcourt 
Pierre-Elie Bélouard
X2015 

- The .zip file contains a 4-pages .pdf report, a .txt file with some partial results of the execution of the program starting from Anakin Solo (our computers were very powerful and we had poor connection during these holydays, we will try it next Sunday on the computers of the Ecole polytechnique) and the src directory with the 8 Java classes : Manager, MyLogger, PageExploration, PageParser, WookiePage, Tree and TreeException 

- The main class (ie the one you have to run) is Manager. You could change the following parameters in the main class : 
	- USEPROXY : true if you use a proxy (will only work with the proxy of the Ecole polytechnique, for example for the computers in the informatic room)
	- DEPTH : the maximum search depth 
	- ROOT_URL : if you would like to start the search from another character, you have to type the adress of the Wookiepedia page about this character (in English)
	- PRINTMOREINFORMATIONS : True if you would like some more informations (for example the number of characters found by the program, the number of request failures) 
	- MAXIMAL : the maximum number of threads working together 
	
- Before running the program, don't forget to build a path to the Jsoup library. 
