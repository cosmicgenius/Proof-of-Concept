package file_io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public class Input 
{

	public static void main(String args[]) throws IOException 
	{
		File file = new File("C:\\Users\\antho\\Documents\\Programming\\Java Save Files\\Testin.txt"); 
		 
	    if (file.exists())                          
	    { 
			BufferedReader inFile = new BufferedReader(new java.io.FileReader("C:\\Users\\antho\\Documents\\Programming\\Java Save Files\\Testin.txt"));
	
			// For each line in the file, read in the line and display it with the
			// line number
			int lineNum = 0;
	
			// Compare the results of calling the readLine method to null
			// to determine if you are at the end of the file.
			String line = inFile.readLine();
			while (line != null) {
				System.out.println(++lineNum + ": " + line);
				line = inFile.readLine();
			}
	
			// Close the buffered reader input stream attached to the file
			inFile.close();
	    }
	    else
	    {
			System.out.println("INVALID FILE");
	    }
	}

}
