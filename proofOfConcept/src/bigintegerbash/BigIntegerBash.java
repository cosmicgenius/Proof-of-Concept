package bigintegerbash;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;

public class BigIntegerBash {
	
	public static void main(String args[]) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		BigInteger N = new BigInteger(br.readLine());
		
		BigInteger ans = BigInteger.ZERO;
		
		for (int i = 1; i < 10; i++) {
			for (int j = 1; j < 10; j++) {
				BigInteger I = BigInteger.valueOf(i);
				BigInteger J = BigInteger.valueOf(j);
				 
				if (I.gcd(J).equals(BigInteger.ONE)) {
					System.out.println(I + " " + J + " " + I.gcd(J).equals(BigInteger.ONE) + " " + N.divide(I.max(J)));
										
					ans = ans.add(N.divide(I.max(J)));
					ans = ans.mod(BigInteger.valueOf(998244353));
					
				}
			}
		}
		System.out.println(ans);
	}
	
}
