package com.example.mirai.libraries.notification.fixtures;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtFactory {

	public static String generateJwtToken(String userId) {
		final Date createdDate = new Date();
		final Date expirationDate = new Date(createdDate.getTime() + 3600000);
		final PrivateKey privateKey = getPrivateKey();
		return Jwts.builder()
				.setIssuer("https://mirai.example.com")
				.setSubject("30e03af604637a4ac79530e03af60463")
				.claim("scope", new Object[] { "mirai_profile" })
				.claim("full_name", new Object[] { userId + "_full_name" })
				.claim("user_id", new Object[] { userId })
				.claim("department_name", new Object[] { userId + "_department_name" })
				.claim("employee_number", new Object[] { userId + "_employee_number" })
				.claim("abbreviation", userId + "_abbreviation")
				.claim("email", new Object[] { userId + "_email@example.net" })
				.claim("department_number", new Object[] { userId + "_department_number" })
				.setIssuedAt(createdDate)
				.setExpiration(expirationDate)
				.signWith(
						SignatureAlgorithm.RS512,
						privateKey
				)
				.compact();
	}

	private static PrivateKey getPrivateKey() {
		PrivateKey privateKey = null;
		try {
			byte[] privateKeyBytes = Files.readAllBytes(Paths.get(ClassLoader.getSystemResource("jwt/private-key.der").toURI()));
			PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
		}
		catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return privateKey;
	}

}
