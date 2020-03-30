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
			System.out.println("Aqui0");
			List<Response> response = mapper.readValue(JSON, new TypeReference<List<Response>>(){});
			
			System.out.println("Aqui");
			
			
			
			System.out.println("Aqui1");
			
			if (response == null) {
				System.err.println("No response from server");
				return Collections.emptyList();
			}
			System.out.println("Aqui2");
			System.out.println(response.size());
			System.out.println("Aqui3");
			
			
			for (Response tweet: response) {
				System.out.println(tweet.qtdTotal + "  -  " +
						  tweet.coordinates.lng + "  -  " +
						  tweet.coordinates.lat + "  -  " +
						  tweet.text);
				
				System.out.println(ret.size());
				
				
				
				
				Tweet meuTweet = new Tweet(tweet.qtdTotal, tweet.coordinates.lng,tweet.coordinates.lat,tweet.text);
				
				System.out.println("Tweet criado e adding no ret");
				ret.add( meuTweet );
				System.out.println("adicionado no ret");
				
			}
			
			
//			response.forEach(tweet->{
//				System.out.println(tweet.qtdTotal + "  -  " +
//						  tweet.coordinates.lng + "  -  " +
//						  tweet.coordinates.lat + "  -  " +
//						  tweet.text);
//				
//				System.out.println(ret.size());
//				
//				
//				
//				
//				Tweet meuTweet = new Tweet(tweet.qtdTotal, 
//						  tweet.coordinates.lng,
//						  tweet.coordinates.lat,
//						  tweet.text);
//				
//				System.out.println("Tweet criado e adding no ret");
//				ret.add( meuTweet );
//				System.out.println("adicionado no ret");
//						
//			});
			

		} catch (IOException aE) {
			aE.printStackTrace();
		}

		return ret;
	}

	private static class Response {
		public Coordinates coordinates;
		public int qtdTotal;
		public String text;
	}

	public static class Coordinates {
		public double lat;
		public double lng;
	}

}
