package file_io;

import java.io.IOException;

public class Output {

	public static void main(String args[]) throws IOException
	{
		// Create a FileWriter attached to a file named "out.txt".
        // The second parameter sets whether or not new data
        // will be appended to the end of the file or the beginning.
        // false means that this file will be overwritten.
        java.io.FileWriter fw = new java.io.FileWriter("C:\\Users\\antho\\Documents\\Programming\\Java Save Files\\Testout.txt", false );
    
        // Create a PrintWriter that automatically flushes data
        // to the output file whenever the println method is used.
        java.io.PrintWriter pw = new java.io.PrintWriter( fw, true );
 
        // Buffer some data to write to the file (doesn't actually write until flush)
        pw.print("3y50`8	4");
        
        // Flush all buffered data to the file.
        pw.flush();
 
        // Write some data and automatically flush it to the file.
        pw.println("blah blah blah");
 
        // Close the PrintWriter for added safety.
        pw.close();
	}
	
}
