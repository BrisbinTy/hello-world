import java.io.*;
import java.util.*;

// H1A.java CS6025 Yizong Cheng January 2015
// Huffman encoder
// Usage: java H1A original > encoded
class Node implements Comparable{
  Node left, right;
  int symbol;
  int frequency;
  public Node(Node l, Node r, int s, int f){
    left = l; right = r; symbol = s; frequency = f;
  }
  public int compareTo(Object obj){
   Node n = (Node)obj;
   return frequency - n.frequency;
  }
}
public class H1A {

  static final int numberOfSymbols = 256;
  static final int blockSize = 1024;
  int[] freq = new int[numberOfSymbols];
  Node tree = null;
  String[] codewords = new String[numberOfSymbols];
  int[][] codetree = null;
  int buf = 0; int position = 0;
  int actualNumberOfSymbols = 0;

  void count(String filename){ // count symbol frequencies
    byte[] buffer = new byte[blockSize];
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(filename);
    } catch (FileNotFoundException e){
      System.err.println(filename + " not found");
      System.exit(1);
    }
    int len = 0;
    int totalLen = 0;
    double entropy = 0;
    for (int i = 0; i < numberOfSymbols; i++) freq[i] = 0;
   try {
    while ((len = fis.read(buffer)) >= 0){
      for (int i = 0; i < len; i++){
       int symbol = buffer[i];
       if (symbol < 0) symbol += 256;
       freq[symbol]++;
       totalLen++;
      }    
    }
    
   double log2 = Math.log(2.0);
   for (int i = 0; i < numberOfSymbols; i++) {
      if (freq[i] > 0) {
          double prob = freq[i] * 1.0 / totalLen;//totalLen is size of input file, count times we see a character
          entropy += prob * Math.log(prob) / log2;
      }
   }
    fis.close();
   } catch (IOException e){
      System.err.println("IOException");
      System.exit(1);
   }
   System.err.println("Entropy: " + (-1*entropy));
  }

  void makeTree(){  // make Huffman prefix codeword tree
   PriorityQueue<Node> pq = new PriorityQueue<Node>();
   for (int i = 0; i < numberOfSymbols; i++) if (freq[i] > 0){
       actualNumberOfSymbols++;
       pq.add(new Node(null, null, i, freq[i]));
   }
   while (pq.size() > 1){
     Node a = pq.poll(); Node b = pq.poll();
     pq.add(new Node(a, b, -1, a.frequency + b.frequency));
   }
   tree = pq.poll();
  }

  void dfs(Node n, String code){  // generate all codewords
    if (n.symbol < 0){
      dfs(n.left, code + "0"); dfs(n.right, code + "1");
    }else codewords[n.symbol] = code;
  }


 void buildTree(){  // make the prefic code tree
  codetree = new int[actualNumberOfSymbols * 2 - 1][2];
  int treeSize = 1;
  for (int i = 0; i < actualNumberOfSymbols * 2 - 1; i++)
    codetree[i][0] = codetree[i][1] = 0;
  for (int i = 0; i < numberOfSymbols; i++) 
   if (codewords[i] != null){
    int len = codewords[i].length();
    int k = 0;
    for (int j = 0; j < len; j++){
      int side = codewords[i].charAt(j) - '0';
      if (codetree[k][side] <= 0) codetree[k][side] = treeSize++;
      k = codetree[k][side];
    }
    codetree[k][1] = i;
  }
 }
    
  void outputTree(){
    System.out.write(actualNumberOfSymbols);
    for (int i = 0; i < actualNumberOfSymbols * 2 - 1; i++){
      System.out.write(codetree[i][0]);
      System.out.write(codetree[i][1]);
    }
  }
    
  void encoding(String filename){ // compress filename to System.out
    byte[] buffer = new byte[blockSize];
    FileInputStream fis = null;
    try {
      fis = new FileInputStream(filename);
    } catch (FileNotFoundException e){
      System.err.println(filename + " not found");
      System.exit(1);
    }
    int len = 0;
   try {
    while ((len = fis.read(buffer)) >= 0){
      for (int i = 0; i < len; i++){
       int symbol = buffer[i];
       if (symbol < 0) symbol += 256;
       outputbits(codewords[symbol]);
      }
    }
    fis.close();
   } catch (IOException e){
      System.err.println("IOException");
      System.exit(1);
   }
    if (position > 0){ System.out.write(buf); }
    System.out.flush();
  }

  void outputbits(String bitstring){ // output codeword
     for (int i = 0; i < bitstring.length(); i++){
      buf <<= 1;
      if (bitstring.charAt(i) == '1') buf |= 1;
      position++;
      if (position == 8){
         position = 0;
         System.out.write(buf);
           // size of the compressed file
         buf = 0;
      }
     }
  }
    public static void main(String[] args) {
     if (args.length < 1){
     System.err.println("Usage: java Huffman file > compressed");
     return;
    }
    H1A h1 = new H1A();
    h1.count(args[0]);
    h1.makeTree();
    h1.dfs(h1.tree, "");
    h1.buildTree(); 
    h1.outputTree();
    h1.encoding(args[0]);
    System.err.println("done"); 
    }
}
