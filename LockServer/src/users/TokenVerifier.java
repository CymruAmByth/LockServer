package users;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TreeMap;

public class TokenVerifier {

	private final TreeMap<String, String> tokenClaims;
	private static final String SERVER_CLIENT_ID = "635248478115-khks0610shmbkpk8qh4btdgeos4c2n3e.apps.googleusercontent.com";

	public TokenVerifier(String token) throws IOException {
		tokenClaims = new TreeMap<String, String>();

		// convert token to token claims
		URL url = new URL("https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=" + token);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		if (con.getResponseCode() == 200) {
			InputStreamReader input = new InputStreamReader(con.getInputStream());
			BufferedReader reader = new BufferedReader(input);

			String line;
			String result = "";
			while ((line = reader.readLine()) != null) {
				result += line;
			}

			// remove curly braces
			result = result.substring(1, result.length() - 1);

			// split pairs
			String[] keyValuePairs = result.split(",");

			// extract key value pairs
			for (String pair : keyValuePairs) {
				// split
				String[] entry = pair.split(":", 2);
				// trim
				String key = entry[0].trim();
				String value = entry[1].trim();
				// remove accolades
				key = key.substring(1, key.length() - 1);
				value = value.substring(1, value.length() - 1);
				// add to treemap
				tokenClaims.put(key, value);
			}

			if (!this.getClaim("aud").equals(SERVER_CLIENT_ID)) {
				throw new IOException("Incorrect app id");
			}
		} else {
			throw new IOException("TokenVerifier: Google endpoint responce is: " + con.getResponseMessage());
		}
	}

	public String getClaim(String claim) {
		if (tokenClaims.containsKey(claim))
			return tokenClaims.get(claim);
		else
			return "";
	}
}
