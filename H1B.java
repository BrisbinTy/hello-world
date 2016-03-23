
// H1B.java CS6025 Yizong Cheng January 2015
// Huffman decoder

import java.io.*;

// Usage:  java H1B < encoded > original

public class H1B {
  int[][] codetree = null;
  int buf = 0; int position = 0;
  int actualNumberOfSymbols = 0;

  void readTree(){  // read Huffman tree
  try{
   actualNumberOfSymbols = System.in.read();
   codetree = new int[actualNumberOfSymbols * 2 - 1][2];
   for (int i = 0; i < actualNumberOfSymbols * 2 - 1; i++){
     codetree[i][0] = System.in.read();
     codetree[i][1] = System.in.read();
   }
  } catch (IOException e){
     System.err.println(e);
     System.exit(1);
  }
 }

   int inputBit(){ // get one bit from System.in
   if (position == 0)
     try{
       buf = System.in.read();
       if (buf < 0){ return -1;
 }
            
       position = 0x80;
     }catch(IOException e){
        System.err.println(e);
        return -1;
     }
   int t = ((buf & position) == 0) ? 0 : 1;
   position >>= 1;  
   return t;
 }

 void decode(){
  int bit = -1;  int k = 0;
  while ((bit = inputBit()) >= 0){
    // your four lines of code?
    k = codetree[k][bit];
    //Move down the code tree with index k until a leaf is reached.
    if (codetree[k][0] == 0) {
        System.out.write(codetree[k][1]);
	k = 0;
    }
    //Output symbol needs to start moving again from root of the tree.
    
  }
  System.out.flush();
 }

    
    public static void main(String[] args) {
        H1B h1 = new H1B();
        h1.readTree();
        h1.decode();
    }
}
