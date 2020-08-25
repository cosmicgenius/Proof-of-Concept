package product_of_primes;

import java.math.BigInteger;

public class Main
{

	static BigInteger prod = BigInteger.ONE;

	public static void main(String args[])
	{
		int n = 0;
		int prime = 2;
		
		while(n < 1069264)
		{
			if(prod.gcd(BigInteger.valueOf(prime)).equals(BigInteger.ONE))
			{
				n++;
				prod = prod.multiply(BigInteger.valueOf(prime));
				System.out.println(n + " " + prime);
			}
			prime++;
		}
		System.out.println(prod.remainder(BigInteger.valueOf(1000)).intValue());
	}
	
}
