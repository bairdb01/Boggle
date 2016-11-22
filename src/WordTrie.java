import java.io.*;
import java.net.URL;

/**
 * Author: Benjamin Baird
 * Created on: 2016-11-19
 * Last Updated on: 2016-11-19
 * Filename: WordTrie
 * Description: Performs simple operations on the trie
 */
public class WordTrie {
    WordTrieNode root;

    // Initialize the trie with terms from the word file
    public WordTrie(){
        this.root = new WordTrieNode();
        try {
            InputStream is = this.getClass().getResourceAsStream("dictionary.txt");
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
//                System.out.println(line);
                if (line == "T\n")
                    System.out.println();
                if (line.length() > 2)
                    addTerm(line.toUpperCase());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException ioError) {
            ioError.printStackTrace();
        }
    }

    // Traverse the trie and add the term if it doesn't exist
    public void addTerm(String term){
        WordTrieNode curNode = root.getChildren();
        WordTrieNode parent = root;
        String prefix = "";
        String parentPrefix = "";
        // Iterate over trie
        while (curNode != null) {

            if (term.startsWith(prefix)) {
                if (term.equals(prefix)) {
                    return;
                }
                parentPrefix = prefix;
                prefix += curNode.getPrefix();

                if (!term.startsWith(prefix) || curNode.getPrefix().equals("")) {
                    prefix = parentPrefix;
                    curNode = curNode.getSibling();
                    continue;
                } else {
                    parent = curNode;
                    curNode = curNode.getChildren();
                }
            }
        }

        if (term.equals(prefix)) {
            return;
        }

        // Term doesn't exist, add to trie
        String newTerm = term.substring(prefix.length());

        // Check if it has anything in common with siblings
        curNode = parent.getChildren();
        while (curNode != null) {
            String nodePrefix = curNode.getPrefix();
            String similarPrefix = prefix;
            for(int i = 0; i < Math.min(newTerm.length(), nodePrefix.length()); i++) {
                if (nodePrefix.charAt(i) == newTerm.charAt(i)) {
                    similarPrefix += nodePrefix.charAt(i);
                } else if (!similarPrefix.equals(prefix)){
                    // Split the parent node at differing character
                    WordTrieNode deltaOne = new WordTrieNode(nodePrefix.substring(i), null);
                    deltaOne.setChildren(curNode.getChildren());
                    WordTrieNode deltaTwo = new WordTrieNode(newTerm.substring(i), deltaOne);
                    deltaTwo.setChildren(new WordTrieNode("", null));
                    WordTrieNode newParent = new WordTrieNode(similarPrefix.substring(prefix.length()), parent.getChildren().getSibling());
                    newParent.setChildren(deltaTwo);
                    parent.setChildren(newParent);
                    return;
                } else {
                    break;
                }
            }
            curNode = curNode.getSibling();
        }
        WordTrieNode newNode = new WordTrieNode(newTerm,  parent.getChildren());
        newNode.setChildren(new WordTrieNode("", null));    // Say that prefix exists in trie
        parent.setChildren(newNode);
    }

    // For custom words
    public boolean trieContains(String term){
        term = term.toUpperCase();
        WordTrieNode curNode = root.getChildren();
        String prefix = "";
        String prevPrefix = "";
        while (curNode != null) {
            prefix += curNode.getPrefix();
            if (prefix.equals(term) && curNode.getPrefix() == "") {
                return true;
            } else if (term.startsWith(prefix) && curNode.getPrefix() != "") {
                curNode = curNode.getChildren();
                prevPrefix = prefix;
            } else {
                curNode = curNode.getSibling();
                prefix = prevPrefix;
            }
        }

        return false;
    }



}
