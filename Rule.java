import java.util.ArrayList;


public class Rule {
	private int startRuleNumber;
	private String l;
	private String[] r;
	private ArrayList<Character> first;
	private ArrayList<Character> follow;

	public void setL(String l){
		this.l = l;
	}
	public String getL(){
		return l;
	}
	public void setR(String[] r){
		this.r = r;
	}
	public String[] getR(){
		return r;
	}
	public void setFirst(ArrayList<Character> first){
		this.first = first;
	}
	public ArrayList<Character> getFirst(){
		return first;
	}
	public void setFollow(ArrayList<Character> follow){
		this.follow = follow;
	}
	public ArrayList<Character> getFollow(){
		return follow;
	}
	public int getStartRuleNumber() {
		return startRuleNumber;
	}
	public void setStartRuleNumber(int startRuleNumber) {
		this.startRuleNumber = startRuleNumber;
	}
}
