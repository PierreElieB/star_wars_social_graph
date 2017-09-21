import java.lang.Comparable; 

public class WookiePage implements Comparable<WookiePage> {
	public String name; 
	public String url; 
	boolean isCharacter; 
	int depth; 
	
	public WookiePage(String u, String n, int k){
		url = u; 
		name = n; 
		depth = k; 	
	}
	
	public int compareTo(WookiePage w){
		int res = (this.url).compareTo(w.url); 
		return(res); 
	}
	
	@Override
	public boolean equals(Object o){
		if(o == this) return true; 
		if(o instanceof WookiePage){
			WookiePage w = (WookiePage) o; 
			return(this.url==w.url); 
		}
		return(false); 
	}
}
