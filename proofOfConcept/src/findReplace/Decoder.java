package findReplace;

import java.io.FileNotFoundException;
import java.io.IOException;

import main.Iostream;

public class Decoder
{
	static String find = "\"";
	static String replace = "";
	
	static String next = "";
	
	static boolean first = true;
	
	static String read = "";
	
	public static void main(String args[]) throws FileNotFoundException, IOException
	{			
		Iostream io1 = new Iostream();
		io1.ifStream("src/findReplace/input.txt");

		String cur = io1.fReadLine();
		System.out.println(cur);
		
		while(cur != null)
		{			
			read += cur + "\n";
			cur = io1.fReadLine();
			
			System.out.println(cur);
		}
		
		Iostream io2 = new Iostream();
		io2.ofStream("src/findReplace/output.txt", true);

		for(int i = 0; i < read.length() - find.length(); i++)
		{
			if(read.substring(i, i + find.length()).equals(find))
			{
				io2.fprint(replace);
				i += find.length() - 1;
			}
			else
			{
				io2.fprint("" + read.charAt(i));
			}
		}	
		io2.fclose();
		
		System.out.println("Complete.");
	}
	
}
