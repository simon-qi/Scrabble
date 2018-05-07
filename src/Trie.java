class Trie {
	Node root;
	
	class Node {
	    Node[] children;
	    boolean isWord;
	    
	    public Node() {
	        children = new Node[26];
	        isWord = false;
	    }
	    
	    private void insert(String word, int depth) {
	    	if (depth == word.length()) {
	    		isWord = true;
	    		return;
	    	}
	    	
	    	if (children[word.charAt(depth) - 'a'] == null) {
	    		children[word.charAt(depth) - 'a'] = new Node();
	    	}
	    	children[word.charAt(depth) - 'a'].insert(word, depth + 1);
	    }
	    
	    private boolean search(String word, int depth) {
	    	if (depth == word.length())
	    		return isWord;
	        if (children[word.charAt(depth) - 'a'] == null)
	        	return false;
	        return children[word.charAt(depth) - 'a'].search(word, depth + 1);
	    }
	    
	    private boolean startsWith(String prefix, int depth) {
	    	if (depth == prefix.length())
	    		return true;
	    	if (children[prefix.charAt(depth) - 'a'] == null) {
	    		return false;
	    	}
	    	return children[prefix.charAt(depth) - 'a'].startsWith(prefix, depth + 1);
	    }
	}

    /** Initialize your data structure here. */
    public Trie() {
        root = new Node();
    }
    

    /** Inserts a word into the trie. */
    public void insert(String word) {
        root.insert(word, 0);
    }
    
    
    /** Returns if the word is in the trie. */
    public boolean search(String word) {
        return root.search(word, 0);
    }
    
    /** Returns if there is any word in the trie that starts with the given prefix. */
    public boolean startsWith(String prefix) {
    	return root.startsWith(prefix, 0);
        
    }
}
