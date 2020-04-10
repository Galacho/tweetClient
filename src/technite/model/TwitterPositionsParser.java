package technite.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;




public class TwitterPositionsParser {

	private final ObjectMapper mapper = new ObjectMapper();

	public TwitterPositionsParser() {
		// TODO Auto-generated constructor stub
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	Collection<Tweet> unmarshall(String JSON) {
		
		
		Collection<Tweet> ret = new ArrayList<>();

		//System.out.println("JSON:"+JSON);

		try {
			List<Response> response = mapper.readValue(JSON, new TypeReference<List<Response>>(){});
			
			
			if (response == null) {
				System.err.println("No response from server");
				return Collections.emptyList();
			}
			
			
			response.forEach(tweet->{
				//for(int i=0; i< tweet.qtdTotal; i++) {
					ret.add( new Tweet(tweet.qtdTotal, 
							  tweet.coordinates.lng,
							  tweet.coordinates.lat,
							  tweet.text,
							  tweet.hash_tag,
							  tweet.id) );
				//}
			});
			

		} catch (IOException aE) {
			aE.printStackTrace();
		}

		return ret;
	}

	private static class Response {
		public Coordinates coordinates;
		public int qtdTotal;
		public String text;
		public String hash_tag;
		public Long id;
	}

	public static class Coordinates {
		public double lat;
		public double lng;
	}

}
