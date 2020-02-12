package de.timeout.sudo.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordCryptor {
	
	private static final SecureRandom random = new SecureRandom();

	/**
	 * Each token produced by this class uses this identifier as a prefix.
	 */
	public static final String ID = "$31$";

	private static final String ALGORITHM = "PBKDF2WithHmacSHA1";

	private static final int SIZE = 128;

	private static final Pattern layout = Pattern.compile("\\$31\\$(\\d\\d?)\\$(.{43})");

	private static int iterations(int cost) {
		if ((cost < 0) || (cost > 30))
	      throw new IllegalArgumentException("cost: " + cost);
	    return 1 << cost;
	}
	
	private PasswordCryptor() {
		/* CONSTRUCTOR IS NOT REQUIRED */
	}

	/**
	 * Hash a password for storage.
	 * 
	 * @return a secure authentication token to be stored for later authentication 
	 */
	public static String encode(String password) {
	    byte[] salt = new byte[SIZE / 8];
	    random.nextBytes(salt);
	    byte[] dk = pbkdf2(password.toCharArray(), salt, 1 << 30);
	    byte[] hash = new byte[salt.length + dk.length];
	    System.arraycopy(salt, 0, hash, 0, salt.length);
	    System.arraycopy(dk, 0, hash, salt.length, dk.length);
	    Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
	    return ID + 30 + '$' + enc.encodeToString(hash);
	}

	/**
	 * Authenticate with a password and a stored password token.
	 * 
	 * @return true if the password and token match
	 */
	public static boolean authenticate(String password, String token) {
	    Matcher m = layout.matcher(token);
	    if (!m.matches())
	      throw new IllegalArgumentException("Invalid token format");
	    int iterations = iterations(Integer.parseInt(m.group(1)));
	    byte[] hash = Base64.getUrlDecoder().decode(m.group(2));
	    byte[] salt = Arrays.copyOfRange(hash, 0, SIZE / 8);
	    byte[] check = pbkdf2(password.toCharArray(), salt, iterations);
	    int zero = 0;
	    for (int idx = 0; idx < check.length; ++idx)
	      zero |= hash[salt.length + idx] ^ check[idx];
	    return zero == 0;
	}

	private static byte[] pbkdf2(char[] password, byte[] salt, int iterations) {
	    KeySpec spec = new PBEKeySpec(password, salt, iterations, SIZE);
	    try {
	      SecretKeyFactory f = SecretKeyFactory.getInstance(ALGORITHM);
	      return f.generateSecret(spec).getEncoded();
	    }
	    catch (NoSuchAlgorithmException ex) {
	      throw new IllegalStateException("Missing algorithm: " + ALGORITHM, ex);
	    }
	    catch (InvalidKeySpecException ex) {
	      throw new IllegalStateException("Invalid SecretKeyFactory", ex);
	    }
	}
}