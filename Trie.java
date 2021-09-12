package trie;

import java.util.ArrayList;

/**
 * This class implements a Trie. 
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {
	
	// prevent instantiation
	private Trie() { }
	
	/*
	private static boolean prefixMatches(String[] array, int inputWordIndex, int prefixIndex, short charStartIndex, short charEndIndex) {
		String tempWord = array[inputWordIndex].substring(charStartIndex, charEndIndex+1);
		String tempPrefix = array[prefixIndex].substring(charStartIndex, charEndIndex+1);
		System.out.println("prefixMatches Method:" + tempWord + " " + tempPrefix);
		if (tempWord.equals(tempPrefix)) {
			return true;
		} else return false;
	}
	*/
	
	private static int indexOfLastCommonCharPrefix(String[] array, int inputWordIndex, String prefix) {
		int charIndex = 0;
		while (array[inputWordIndex].charAt(charIndex) == prefix.charAt(charIndex)) {
			charIndex++;
			if (charIndex >= array[inputWordIndex].length() || charIndex >= prefix.length()) {
				break;
			}
		}
		return charIndex-1;
	}
	
	// Outputs the index number of the last character the two words have in common.
	// Outputs -1 if there is no common prefix.
	private static int indexOfLastCommonChar(String[] array, int inputWordIndex, int leafNodeIndex) {
		int charIndex = 0;
		while (array[inputWordIndex].charAt(charIndex) == array[leafNodeIndex].charAt(charIndex)) {
			charIndex++;
			if (charIndex >= array[inputWordIndex].length() || charIndex >= array[leafNodeIndex].length()) {
				break;
			}
		}
		return charIndex-1;
	}
	
	/**
	 * Builds a trie by inserting all words in the input array, one at a time,
	 * in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!)
	 * The words in the input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */
	
	/*
	 * Step 1: Check whether node is prefix or leaf/word node.
	 * 1. Prefix Node
	 * 		-Do prefixes match?
	 * 			1. Yes
	 * 				-Go to the child node. Repeat step 1.
	 * 			2. No
	 * 				-Go to sibling. Repeat step 1.
	 * 2. Leaf Node
	 * 		-Is there a common prefix?
	 * 			1. Yes
	 * 				-Does the node have a prefix parent (second triplet != 0)?
	 * 					1. Yes
	 * 						-Does the number of characters of the prefix node match the number of shared
	 * 						 characters of the inserted word and leaf node? (Use second triplet).
	 */
	
	public static TrieNode buildTrie(String[] allWords) {
		if (allWords == null) {
			return null;
		}
		TrieNode root = new TrieNode(null, null, null);
		boolean newWordAdded = false;
		TrieNode prev = root;
		TrieNode current = root.firstChild;
		for (int i=0; i < allWords.length; i++) {
			newWordAdded = false;
			prev = root;
			current = root.firstChild;
			while (current != null) {
				if (current.firstChild != null) {
					// This is a prefix node.
					if (indexOfLastCommonCharPrefix(allWords, i, allWords[current.substr.wordIndex].substring(0, current.substr.endIndex + 1)) == current.substr.endIndex) {
						prev = current;
						current = current.firstChild;
						continue;
					} else if (indexOfLastCommonCharPrefix(allWords, i, allWords[current.substr.wordIndex].substring(0, current.substr.endIndex + 1)) == -1) {
						prev = current;
						current = current.sibling;
						continue;
					} else if (indexOfLastCommonCharPrefix(allWords, i, allWords[current.substr.wordIndex].substring(0, current.substr.endIndex + 1)) < current.substr.endIndex && current.substr.startIndex <= indexOfLastCommonCharPrefix(allWords, i, allWords[current.substr.wordIndex].substring(0, current.substr.endIndex + 1))) {
						// The prefix node is less specific than the input word prefix.
						int lastCommonChar = indexOfLastCommonCharPrefix(allWords, i, allWords[current.substr.wordIndex].substring(0, current.substr.endIndex + 1));
						if (prev.firstChild == current) {
							TrieNode newPrefixNode = new TrieNode(new Indexes(current.substr.wordIndex, current.substr.startIndex, (short) lastCommonChar), current, current.sibling);
							TrieNode newWordNode = new TrieNode(new Indexes(i, (short)(newPrefixNode.substr.endIndex + 1), (short)(allWords[i].length() - 1)), null, null);
							prev.firstChild = newPrefixNode;
							current.sibling = newWordNode;
							current.substr.startIndex = (short)(newPrefixNode.substr.endIndex + 1);
							newWordAdded = true;
							break;
						} else {
							TrieNode newPrefixNode = new TrieNode(new Indexes(current.substr.wordIndex, current.substr.startIndex, (short) lastCommonChar), current, current.sibling);
							TrieNode newWordNode = new TrieNode(new Indexes(i, (short)(newPrefixNode.substr.endIndex + 1), (short)(allWords[i].length() - 1)), null, null);
							prev.sibling = newPrefixNode;
							current.sibling = newWordNode;
							current.substr.startIndex = (short)(newPrefixNode.substr.endIndex + 1);
							newWordAdded = true;
							break;
						}
					} else {
						prev = current;
						current = current.sibling;
					}
				} else {
					//This is a leaf node.
					if (indexOfLastCommonChar(allWords, i, current.substr.wordIndex) == -1) {
						// There is no shared prefix. The node at the current variable is connected to root.
						prev = current;
						current = current.sibling;
						continue;
					} else {
						// Leaf node and inserted word has common prefix. We need to check if there is a prefix node.
						if (current.substr.startIndex != 0) {
							// The current node has a prefix node.
							if (indexOfLastCommonChar(allWords, i, current.substr.wordIndex) == current.substr.startIndex - 1) {
								// The current node and the inserted word has the same prefix as the prefix node.
								prev = current;
								current = current.sibling;
								continue;
							} else if (indexOfLastCommonChar(allWords, i, current.substr.wordIndex) >= current.substr.startIndex) {
								// The current node and the inserted word has more characters in common than the prefix node. New prefix node must be created.
								TrieNode newPrefixNode = new TrieNode(new Indexes(current.substr.wordIndex, current.substr.startIndex, (short)indexOfLastCommonChar(allWords, i, current.substr.wordIndex)), current, current.sibling);
								TrieNode newWordNode = new TrieNode(new Indexes(i, (short)(newPrefixNode.substr.endIndex + 1), (short)(allWords[i].length() - 1)), null, null);
								if (prev.firstChild == current) {
									prev.firstChild = newPrefixNode;
									current.sibling = newWordNode;
									current.substr.startIndex = (short)(newPrefixNode.substr.endIndex + 1);
									newWordAdded = true;
									break;
								} else {
									prev.sibling = newPrefixNode;
									current.sibling = newWordNode;
									current.substr.startIndex = (short)(newPrefixNode.substr.endIndex + 1);
									newWordAdded = true;
									break;
								}
							} else {
								// The current node and the inserted word has less characters in common than the prefix node.
								
							}
						} else {
							// The current node does not have a prefix node, and it is connected to root.
							TrieNode newPrefixNode = new TrieNode(new Indexes(current.substr.wordIndex, (short) 0, (short)indexOfLastCommonChar(allWords, i, current.substr.wordIndex)), current, current.sibling);
							TrieNode newWordNode = new TrieNode(new Indexes(i, (short)(newPrefixNode.substr.endIndex + 1), (short)(allWords[i].length() - 1)), null, null);
							if (prev.firstChild == current) {
								prev.firstChild = newPrefixNode;
								current.sibling = newWordNode;
								current.substr.startIndex = (short)(newPrefixNode.substr.endIndex + 1);
								newWordAdded = true;
								break;
							} else {
								prev.sibling = newPrefixNode;
								current.sibling = newWordNode;
								current.substr.startIndex = (short)(newPrefixNode.substr.endIndex + 1);
								newWordAdded = true;
								break;
							}
						}
					}
				}
			}
			if (i == 0) {
				root.firstChild = new TrieNode(new Indexes(0, (short) 0, (short)(allWords[0].length() - 1)), null, null);
				newWordAdded = true;
			}
			if (!newWordAdded){
				TrieNode newWordNode = new TrieNode(new Indexes(i, prev.substr.startIndex, (short)(allWords[i].length() - 1)), null, null);
				prev.sibling = newWordNode;
			}
		}
		return root;
	}
	
	/*
	if(root.firstChild == null) {
		root.firstChild = new TrieNode(new Indexes(0,(short) 0,(short) ((short) allWords[0].length()-1)), null, null);
		continue;
	}
	current = root.firstChild;
	if (current.firstChild != null) {
		if (prefixMatches(allWords, i, current.substr.wordIndex, current.substr.startIndex, current.substr.endIndex) ) {
			current = current.firstChild;
			
		} else {
			current = current.sibling;
		}
	}
	*/
	
	/**
	 * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf nodes in the 
	 * trie whose words start with this prefix. 
	 * For instance, if the trie had the words "bear", "bull", "stock", and "bell",
	 * the completion list for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell"; 
	 * for prefix "be", the completion would be the leaf nodes that hold "bear" and "bell", 
	 * and for prefix "bell", completion would be the leaf node that holds "bell". 
	 * (The last example shows that an input prefix can be an entire word.) 
	 * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be",
	 * the returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
	 *
	 * @param root Root of Trie that stores all words to search on for completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all leaf nodes in trie that hold words that start with the prefix, 
	 * 			order of leaf nodes does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	
	private static ArrayList<TrieNode> getNodesUnderPrefixNode(TrieNode prefixNode, ArrayList<TrieNode> result) {
		TrieNode current = prefixNode.firstChild;
		while (current != null) {
			if (current.firstChild == null) {
				result.add(current);
				current = current.sibling;
			} else {
				getNodesUnderPrefixNode(current, result);
				current = current.sibling;
			}
		}
		return result;
	}
	
	
	public static ArrayList<TrieNode> completionList(TrieNode root, String[] allWords, String prefix) {
		ArrayList<TrieNode> result = new ArrayList<TrieNode>();
		TrieNode current = root.firstChild;
		boolean returnAllNodesInside = false;
		while (current != null) {
			if (current.firstChild != null) {
				// This is a prefix node.
				int endChar = 0;
				if (current.substr.endIndex > prefix.length() - 1) {
					endChar = prefix.length() - 1;
				} else {
					endChar = current.substr.endIndex;
				}
				if (allWords[current.substr.wordIndex].substring(0, endChar + 1).equals(prefix.substring(0, endChar + 1))) {
					if (current.substr.endIndex >= prefix.length() - 1) {
						// The prefix is less specific than the prefix node, so we can return all leaf nodes under this prefix node.
						returnAllNodesInside = true;
						break;
					} else {
						// The prefix is more specific than the prefix node, so we have inspect all elements inside.
						current = current.firstChild;
						continue;
					}
				} else {
					// The prefixes don't match.
					current = current.sibling;
					continue;
				}
			} else {
				// This is a leaf/word node.
				if (prefix.length() > allWords[current.substr.wordIndex].length()) {
					return null;
				} else if (!allWords[current.substr.wordIndex].substring(0, prefix.length()).equals(prefix)) {
					// The prefix of the current word node doesn't match the prefix we're looking for.
					current = current.sibling;
					continue;
				} else {
					// The prefix of the current word node matches the prefix we're looking for.
					result.add(current);
					return result;
				}
			}
		}
		if (returnAllNodesInside == true) {
			return getNodesUnderPrefixNode(current, result);
		}
		return null;
	}
	
	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}
	
	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
							.substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
 }