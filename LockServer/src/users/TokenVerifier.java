package users;

import java.util.TreeMap;

public class TokenVerifier {
	
	private final TreeMap<String, String> tokenClaims;
	
	public TokenVerifier(String token){
		tokenClaims = new TreeMap<String, String>();
		
		//convert token to tokenclaims
		
		//remove curly braces
		token = token.substring(1, token.length()-1);
		
		//split pairs
		String[] keyValuePairs = token.split(",");
		
		//extract key value pairs
		for(String pair : keyValuePairs){
			//split
			String[] entry = pair.split(":");
			//trim
			String key = entry[0].trim();
			String value = entry[1].trim();
			//remove accolades
			key = key.substring(1, token.length()-1);
			value = value.substring(1, token.length()-1);
			//add to treemap
			tokenClaims.put(key, value);
		}		
	}

}
