package cherriew_CSCI201_Assignment1;

// simple Pair class
public class Pair {
	    private String word;
	    private int ranking;
	    
	    public Pair(String w, int r){
	        this.word = w;
	        this.ranking = r;
	    }
	    // getters
	    public String getKey(){ return word; }
	    public int getVal(){ return ranking; }
	    // setters
	    public void setKey(String w){ this.word = w; }
	    public void setVal(int r){ this.ranking = r; }

}
	

