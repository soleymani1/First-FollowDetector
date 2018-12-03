import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;


public class Computation {
	private ArrayList<Rule> rules;
	private ArrayList<Character> first;
	private ArrayList<Character> follow;
	private ArrayList<String> moveList = new ArrayList<String>();
	private ArrayList<String> followMoveList = new ArrayList<String>();
	private HashSet<Character> terminals = new HashSet<Character>();
	private HashSet<Character> variables = new HashSet<Character>();
	private ArrayList<Character> t;
	private ArrayList<Character> v;
	private String [][] table;
	private int count = 0;
	private int temp = 0;
	
	private void createTable(){
		t = new ArrayList<Character>();
		t.addAll(terminals);
		v = new ArrayList<Character>();
		v.addAll(variables);
		table = new String[v.size()][t.size()];
		System.out.println(v.size()+" "+t.size());
		for(int i=0;i<table.length;i++){
			for(int j=0;j<table[0].length;j++){
				table[i][j]="";
			}
		}
	}
	public void run(){
		fillRules();
		createTable();
		for(int i=0; i<rules.size();i++){
			computeFirst(i);
		}
		moveFirst();
		moveFirst();
		printFirst();
		for(int i=0; i<rules.size();i++){
			computeFollow(i);
		}
		moveFollow();
		moveFollow();
		printFollow();
		printTable();
	}
	private void fillRules(){
		Scanner in = new Scanner(System.in);
		System.out.println("Please enter the number of lines: ");
		int lineNum = in.nextInt();
		rules = new ArrayList<Rule>();
		Rule rule;
		String line;
		String [] splitLR;
		String [] splitR;
		for(int i=0;i<lineNum;i++){
			rule = new Rule();
			line = in.next();
			//System.out.println(line);
			splitLR = line.split("=>");
			splitR = splitLR[1].split("\\|");
			rule.setStartRuleNumber(temp);
			temp+= splitR.length;
			rule.setL(splitLR[0]);
			variables.add(rule.getL().charAt(0));
			rule.setR(splitR);
			rules.add(rule);
			for(int j=0;j<splitR.length;j++){
				//System.out.println("@@@@@");
				for(int k=0;k<splitR[j].length();k++){
					//System.out.println("+++++++++");
					if(Character.isUpperCase(splitR[j].charAt(k))){
						//variables.add(splitR[j].charAt(k));
					} else terminals.add(splitR[j].charAt(k));
				}
			}
		}
		//System.out.println(rules.get(0).getL());
		in.close();
	}
	private void computeFirst(int num){
		first = new ArrayList<Character>();
		Rule rule = rules.get(num);
		String [] r = rule.getR();
		for(int i=0;i<r.length;i++){
			first(r[i],0,num);
			count = i;
		}
		rule.setFirst(first);
		rules.set(num, rule);
	}
	private void first(String r, int charCount, int num){
		if(isTerminal(r.charAt(charCount))){
			first.add(r.charAt(charCount));
			int x = getTIndex(r.charAt(charCount));
			//System.out.println("x"+x);
			table[num][x] = 
					table[num][x] + ","+ (rules.get(num).getStartRuleNumber()+count);
		}
		else {
			moveList.add(num+","+getIndex(String.valueOf(r.charAt(charCount)))+","+(rules.get(num).getStartRuleNumber()+count));
			if (charCount !=1){
				if(isLambda(String.valueOf(r.charAt(charCount)))){
					first(r,1,num);
				}
			}
		}
	}
	private void moveFirst(){
		ArrayList<Character> first1;
		ArrayList<Character> first2;
		String [] temp;
		for (int i=0;i<moveList.size();i++){
			temp = moveList.get(i).split(",");
			int num = Integer.valueOf(temp[0]);
			Rule rule = rules.get(num);
			first1 = rule.getFirst();
			first2 = rules.get(Integer.valueOf(temp[1])).getFirst();
			for (int j=0;j<first2.size();j++){
				int x = getTIndex(first2.get(j));
				table[num][x] = table[num][x] + ","+
			(rules.get(num).getStartRuleNumber()+Integer.valueOf(temp[1]));
			}
			first1.addAll(first2);
			rule.setFirst(removeDuplicateFirst(first1));
			rules.set(num, rule);
		}
	}
	private boolean isTerminal(char c){
		return !Character.isUpperCase(c);
	}
	private int getIndex(String s){
		int index = -1;
		for (int i=0; i<rules.size();i++){
			if (rules.get(i).getL().equals(s)){
				index = i;
			}
		}
		return index;
	}
	private ArrayList<Character> removeDuplicateFirst(ArrayList<Character> al){
		Set<Character> hs = new HashSet<>();
		hs.addAll(al);
		al.clear();
		al.addAll(hs);
		return al;
	}
	private void printFirst(){
		Rule rule;
		ArrayList<Character> first;
		System.out.println("First:");
		for(int i=0;i<rules.size();i++){
			rule = rules.get(i);
			first = rule.getFirst();
			System.out.print(rule.getL()+":\t");
			for (int j=0;j<first.size();j++){
				if (!first.get(j).equals('$'))System.out.print(first.get(j)+" ");
			}
			System.out.println();
		}
	}
	private boolean isLambda(String s){
		boolean res = false;
		for (int i=0; i<rules.size();i++){
			if (rules.get(i).getL().equals(s)){
				String [] r = rules.get(i).getR();
				for(int j=0;j<r.length;j++){
					if (r[j].equals("$")){
						res = true;
						break;
					}
				}
			}
		}
		return res;
	}


	private void computeFollow(int num){
		follow = new ArrayList<Character>(); 
		Rule rule = rules.get(num);
		String [] r = rule.getR();
		for(int i=0;i<r.length;i++){
			follow(r[i],r[i].length()-1,num);
			follow2(r[i]);
			
		}
		try{
			follow.addAll(rule.getFollow());
			//System.out.println(follow.size());
		}
		catch (NullPointerException e){
			//e.printStackTrace();
		}
		rule.setFollow(follow);
		rules.set(num, rule);
	}
	private void follow2(String r){
		ArrayList<Character> follow; 
		int num;
		for(int i=0;i<r.length()-1;i++){
			if(!isTerminal(r.charAt(i))){
				follow = new ArrayList<Character>();
				num = getIndex(String.valueOf(r.charAt(i)));
				if(isTerminal(r.charAt(i+1))){
					follow.add(r.charAt(i+1));
				}
				else {
					Rule rule = rules.get(getIndex(String.valueOf(r.charAt(i+1))));
					if(isLambda(rule.getL()) && r.length()>i+2){
						Rule rule2 =rules.get(getIndex(String.valueOf(r.charAt(i+2))));
						try{
							follow.addAll(rule2.getFirst());
						}
						catch (NullPointerException e){
							//e.printStackTrace();
						}
					}
					try{
						follow.addAll(rule.getFirst());
					}
					catch (NullPointerException e){
						//e.printStackTrace();
					}
				}
				Rule rule = rules.get(num);
				try{
					follow.addAll(rule.getFollow());
				}
				catch (NullPointerException e){
					//e.printStackTrace();
				}
				rule.setFollow(follow);
				rules.set(num, rule);
			}
		}
	}
	private void follow(String r,int charCount, int num){
		if(!isTerminal(r.charAt(charCount))){
			followMoveList.add(getIndex(String.valueOf(r.charAt(charCount)))+","+num);
			if (charCount !=charCount-1 && charCount>0){
				if(isLambda(String.valueOf(r.charAt(charCount)))){
					follow(r,charCount-1,num);
				}
			}
		}
	}
	private void printFollow(){
		System.out.println("Follow:");
		Rule rule;
		ArrayList<Character> follow;
		for(int i=0;i<rules.size();i++){
			rule = rules.get(i);
			follow = rule.getFollow();
			System.out.print(rule.getL()+":\t");
			for (int j=0;j<follow.size();j++){
				if (!follow.get(j).equals('$'))System.out.print(follow.get(j)+" ");
			}
			System.out.println();
		}
	}
	private void moveFollow(){
		ArrayList<Character> follow1;
		ArrayList<Character> follow2;
		String [] temp;
		for (int i=0;i<followMoveList.size();i++){
			temp = followMoveList.get(i).split(",");
			//System.out.println(temp[0]+":"+temp[1]);
			int num = Integer.valueOf(temp[0]);
			Rule rule = rules.get(num);
			follow1 = rule.getFollow();
			follow2 = rules.get(Integer.valueOf(temp[1])).getFollow();
			follow1.addAll(follow2);
			rule.setFollow(removeDuplicateFirst(follow1));
			rules.set(num, rule);
		}
	}
	private int getTIndex(Character c){
		//System.out.println(terminals.size() + variables.size());
		return t.indexOf(c);
	}
	private void printTable(){
		System.out.println();
		addFollowTable();
		boolean isLL1 = true;
		for(int i=0;i<table.length;i++){
			for(int j=0;j<table[0].length-1;j++){
				if(table[i][j].equals("")) System.out.print("*\t");
				else {
					System.out.print(table[i][j]+"\t");
					if (table[i][j].split(",").length>2) isLL1 = false;
				}
			}
			System.out.println();
		}
		if(isLL1) System.out.println("YES");
		else System.out.println("NO");
	}
	private void addFollowTable(){
		for(int i=0;i<v.size();i++){
			for(int j=0;j<rules.get(i).getFollow().size();j++){
				if(isLambda(rules.get(i).getL())){
					table[i][getTIndex(rules.get(i).getFollow().get(j))] = 
							table[i][getTIndex(rules.get(i).getFollow().get(j))] + ",$";
				}
			}
		}
	}
}
