

import org.jsoup.Jsoup;

import org.jsoup.helper.Validate;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.io.File;


public class PageParser{
	
	public String url;
	public Elements text; 
	public boolean failed; 
	
	public PageParser(String s){
		url = s;
		failed = true;
	}
	
	public Elements readPage(){
		try{
			Document doc = Jsoup.connect(url).get();
			this.text = doc.select("article#WikiaMainContent");
			failed = false;
		}
		catch(IOException e){failed = true;}
		return text;
	}
			
	public String pageName(){					// returns the name of the object the Wiki page is about. 
		//MyLogger.finer(text.toString());
		Elements name =  text.select("h1"); 
		return(name.first().ownText());
																					 
	}
	
	public boolean isCharacter(){
		Elements minititles = text.select("aside h2.pi-header"); //get elements in <aside ...><h2 class="pi-header ...">...</h2></aside>
		for (Element minititle : minititles){
			if (minititle.ownText().equals("Biographical information")) {return true;} 
		}
		return false;
	}
	
	
	public Elements getSons(){
		Elements sons = text.select("a[href^=/wiki/]:not([href*=?],[href*=%],[href*=:],[href*=:])");
		return sons;
	}
	
	//Only for testing
	public static void main(String[] args) throws IOException{
		PageParser test = new PageParser("http://starwars.wikia.com/wiki/Anakin_Solo");
		test.text=test.readPage();
		System.out.println(test.text.text());
		System.out.println(test.pageName());
		System.out.println(test.isCharacter());
		Elements sons=test.getSons();
		System.out.println("Sons : " + sons.toString());
		System.out.println("number of sons : " + sons.size());
	}
	
}