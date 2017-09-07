package voorbereiding;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.bson.Document;
import org.bson.conversions.Bson;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.*;
import java.util.Iterator;
import jdk.nashorn.internal.parser.JSONParser;
import jersey.repackaged.com.google.common.collect.Iterators;

@Path("/movies")
public class MovieResource {
	
	@GET
	@Path("{title}")
	@Produces({"text/html"})
	public String searchMovie(@PathParam("title") String title){

		Jedis jedis = new Jedis("localhost");
		jedis.connect();
		jedis.sadd("movies", "{title}");
		
		Set<String> names=jedis.keys("movies");
		Iterator<String> i = names.iterator();
		ArrayList<Document> array = new ArrayList<Document>();	
		    while (i.hasNext()) {
		    	array.add((Document) i.next());
		    }
	
		
		Boolean notInDB = true;
		String returnJSON = "";
		
		for(Document doc: array){
			if ((doc.get("title").toString()).equals(title)){
				
		        JsonObjectBuilder builder = Json.createObjectBuilder();
		        builder.add("title", doc.get("title").toString());
		        builder.add("year", doc.get("year").toString());
		        builder.add("actors", doc.get("actors").toString());
		        System.out.println("gevonden in db!");
		        JsonObject newJSON = builder.build();
		        notInDB = false;
		        returnJSON = newJSON.toString();
				break;
			}
		}
		
		if(notInDB){
			
			Response response = ClientBuilder.newClient()
				.target("http://www.omdbapi.com/" + title + "&apikey=plzBanMe")
				.request(MediaType.APPLICATION_JSON)
				.get();
			
			String jsonString = response.readEntity(String.class);
			JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
			JsonObject object = jsonReader.readObject();
			jsonReader.close();
			
	        Document movie = new Document();
	        movie.append("title", object.getString("movie"));
	        movie.append("year", object.getString("year"));
	        movie.append("acteaur", object.getString("actors"));
	   
	        jedis.set("movies", "{title}");
	        jedis.close();
	        returnJSON = object.toString();
	      
			
		}
		return returnJSON;
	}
}