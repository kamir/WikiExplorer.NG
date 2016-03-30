package com.cloudera.wikiexplorer.ng.util.io;

import java.io.*;

public class StreamCopier {

  public static void main(String[] args) {

    try {
      // Kopiert die Standardteingabe auf die Standardtausgabe
      copy(System.in, System.out);
    }
    catch (IOException e) {
      System.err.println(e);
    }
    System.out.println("OK ...");
  }

  public static void copy(File in, File out)
   throws IOException {
   FileInputStream fin = new FileInputStream( in );
   FileOutputStream fout = new FileOutputStream( out );
   copy( fin, fout );
  };
  
  public static void copy(InputStream in, OutputStream out)
   throws IOException {

    // do not allow other threads to read from the
    // input or write to the output while copying is
    // taking place

    // System.out.println( "copy: " + in + " to " + out );

    synchronized (in) {
      synchronized (out) {

        byte[] buffer = new byte[256];
        while (true) {
          int bytesRead = in.read(buffer);
          if (bytesRead == -1) break;
          out.write(buffer, 0, bytesRead);
        }
      }
    }
    out.close();
    in.close();
  }

}
