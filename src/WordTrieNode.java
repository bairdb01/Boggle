/**
 * Author: Benjamin Baird
 * Created on: 2016-11-19
 * Last Updated on: 2016-11-19
 * Filename: WordTrie
 * Description: Contains symbol,link to right-sibling and the first child
 */
public class WordTrieNode {
    private String prefix;
    private WordTrieNode sibling;
    private WordTrieNode children;

    public WordTrieNode(){
        prefix = "";
    }

    public WordTrieNode(String prefix, WordTrieNode sibling){
        this.prefix = prefix;
        this.sibling = sibling;
    }

    public void setSibling(WordTrieNode sibling) {
        this.sibling = sibling;
    }

    public void setChildren(WordTrieNode children) {
        this.children = children;

    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public WordTrieNode getChildren() {
        return children;
    }

    public WordTrieNode getSibling() {
        return sibling;
    }

}
