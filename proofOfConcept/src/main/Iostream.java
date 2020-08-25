package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Iostream
{
	BufferedReader inFile;
	FileWriter fw;
	BufferedWriter pw;
	BufferedReader sysIn;
	
	boolean openI = false;
	boolean openO = false;

	public Iostream()
	{
		sysIn = new BufferedReader(new InputStreamReader(System.in));
	}

	public void ifStream(String location) throws FileNotFoundException, IOException
	{
		File file = new File(location);

		if (file.exists())
		{
			inFile = new BufferedReader(new java.io.FileReader(location));
			openI = true;
		}
		else
		{
			System.out.println("INVALID FILE: ERROR");
		}
	}
	
	public void ofStream(String location, boolean override) throws FileNotFoundException, IOException
	{
		File file = new File(location);

		if (file.exists())
		{
			fw = new FileWriter(location, !override);

			// Create a PrintWriter that automatically flushes data
			// to the output file whenever the println method is used.
			pw = new BufferedWriter(fw);
			
			openO = true;
		}
		else
		{
			System.out.println("INVALID FILE: ERROR");
		}
	}
	
	public void ofStream(String location) throws FileNotFoundException, IOException
	{
		File file = new File(location);

		if (file.exists())
		{
			fw = new FileWriter(location, false);

			// Create a PrintWriter that automatically flushes data
			// to the output file whenever the println method is used.
			pw = new BufferedWriter(fw);
			
			openO = true;
		}
		else
		{
			System.out.println("INVALID FILE: ERROR");
		}
	}

	public String fReadLine() throws IOException
	{
		if (openI)
		{
			String s = inFile.readLine();
			return s;
		}
		return "";
	}

	public int fReadLineInt() throws IOException
	{
		if (openI)
		{
			String s = fReadLine();
			int a = 0;
			for (int i = 0; i < s.length(); i++)
			{
				if ('0' > s.charAt(i) || s.charAt(i) > '9')
				{
					return 0;
				}
				a *= 10;
				a += s.charAt(i) - '0';
	
			}
			return a;
		}
		return Integer.MIN_VALUE;
	}

	public void fprint(String s) throws IOException
	{
		if (openO)
		{
			pw.write(s);
			System.out.print(s);
		}
	}

	public void fprintln(String s) throws IOException
	{
		if (openO)
		{
			pw.write(s);
			pw.newLine();
		}	
	}

	public void fclose() throws IOException
	{
		if (openI && openO)
		{
			inFile.close();
			openI = false;
		}
		if (openO)
		{
			pw.flush();
			pw.close();
			openO = false;
		}
	}
}
