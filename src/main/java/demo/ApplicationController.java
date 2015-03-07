package demo;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.validation.Valid;






import org.apache.tomcat.util.codec.binary.Base64;
//import org.apache.http.protocol.HTTP;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import beans.Moderator;
import beans.Poll;

@RestController
@RequestMapping("/app/v1")
public class ApplicationController {

	/*Auto increment the counters for moderator and poll respectively*/
	private final AtomicInteger moderator_counter = new AtomicInteger();
	private final AtomicLong poll_counter = new AtomicLong();
//	long tempLong = Long.parseLong("1ADC2FZ");
	
	/*Maintain a list to store dynamically created moderators and polls*/
	ArrayList<Moderator> moderator_list = new ArrayList<Moderator>();
	ArrayList<Poll> poll_list = new ArrayList<Poll>();
	

	@RequestMapping(value="/moderators", method = {RequestMethod.POST}, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Moderator> createModerator(@RequestBody @Valid Moderator mod){
		System.out.println("Creating a moderator...");
		Moderator moderator = new Moderator(moderator_counter.incrementAndGet(), mod.getName(), mod.getEmail(), mod.getPassword(), new java.util.Date().toString());
		moderator_list.add(moderator);
		return new ResponseEntity<Moderator>(moderator, HttpStatus.CREATED);
	}

	//added request header for authorization
	@RequestMapping(value="/moderators/{moderator_id}", method = {RequestMethod.GET}, produces = "application/json")
	public ResponseEntity<Moderator> searchModerator(@PathVariable("moderator_id") Integer mod_id, @RequestHeader(value="Authorization") String authorizationDetail) throws UnsupportedEncodingException{
		System.out.println("Searching a specific moderator");
//added boolean and if auth statements
		boolean authenticationSuccess = checkAuthorizationDetail(authorizationDetail);
		if(authenticationSuccess){
			for(Moderator mod:moderator_list){
				if(mod.getId() == mod_id){
					return new ResponseEntity<Moderator>(mod, HttpStatus.OK);
				}
			}
		}
		return new ResponseEntity<Moderator>(HttpStatus.BAD_REQUEST);
	}
	
	@RequestMapping(value="/moderators/{moderator_id}", method = {RequestMethod.PUT}, consumes = "application/json", produces = "application/json")
	public ResponseEntity<Moderator> updateModerator(@PathVariable("moderator_id") Integer mod_id, @RequestBody Moderator moderator,@RequestHeader(value="Authorization") String authorizationDetail) throws UnsupportedEncodingException{
		System.out.println("Updating a moderator");
		boolean authenticationSuccess = checkAuthorizationDetail(authorizationDetail);
		if(authenticationSuccess){
			for(Moderator mod:moderator_list){
				if(mod.getId() == mod_id){
					if(moderator.getName()!=null){
						mod.setName(moderator.getName());
					}
					if(moderator.getEmail()!=null){
						mod.setEmail(moderator.getEmail());
					}
					if(moderator.getPassword()!=null){
						mod.setPassword(moderator.getPassword());
					}
					return new ResponseEntity<Moderator>(mod, HttpStatus.CREATED);
				}
			}
		}
		return new ResponseEntity<Moderator>(HttpStatus.BAD_REQUEST);
	}
	
	@RequestMapping(value="/moderators/{moderator_id}/polls", method = {RequestMethod.POST}, consumes = "application/json", produces = "application/json")
	public ResponseEntity<LinkedHashMap> createPoll(@PathVariable("moderator_id") Integer mod_id, @RequestBody Poll poll, @RequestHeader(value="Authorization") String authorizationDetail) throws UnsupportedEncodingException{
		System.out.println("Creating a poll...");
//		poll_counter.set(tempLong);
		boolean authenticationSuccess = checkAuthorizationDetail(authorizationDetail);
		LinkedHashMap pollHashMap = new LinkedHashMap<>();
		if(authenticationSuccess){
			double rn = Math.random();
			Poll poll_new = new Poll(Long.toHexString((long) ((rn * 987654) + poll_counter.incrementAndGet())), poll.getQuestion(), poll.getStarted_at(), poll.getExpired_at(), poll.getChoice());
			int[] temp = new int[poll.getChoice().length]; 
			poll_new.setResults(temp);
			
			poll_list.add(poll_new);
			
			for(Moderator mod:moderator_list){
				if(mod.getId() == mod_id){
					mod.getPollList().add(poll_new);
					pollHashMap.put("id", poll_new.getId());
					pollHashMap.put("question", poll_new.getQuestion());
					pollHashMap.put("started_at", poll_new.getStarted_at());
					pollHashMap.put("expired_at", poll_new.getExpired_at());
					pollHashMap.put("choice", poll_new.getChoice());
					return new ResponseEntity<LinkedHashMap>(pollHashMap, HttpStatus.CREATED);
				}
			}
		}
		return new ResponseEntity<LinkedHashMap>(HttpStatus.BAD_REQUEST);
	}
		
	@RequestMapping(value="/polls/{poll_id}", method={RequestMethod.GET}, produces = "application/json")
	public ResponseEntity<LinkedHashMap> searchPollWithoutResult(@PathVariable("poll_id") String poll_id){
		//System.out.println("Searching a poll without results");
		LinkedHashMap pollHashMap = new LinkedHashMap<>();
		for(Poll pollObject: poll_list){
			if(pollObject.getId().toString().equals(poll_id)){
				pollHashMap.put("id", pollObject.getId());
				pollHashMap.put("question", pollObject.getQuestion());
				pollHashMap.put("started_at", pollObject.getStarted_at());
				pollHashMap.put("expired_at", pollObject.getExpired_at());
				pollHashMap.put("choice", pollObject.getChoice());
				
				return new ResponseEntity<LinkedHashMap>(pollHashMap, HttpStatus.OK);
			}
		}
		return null;
	}
	
	@RequestMapping(value="/moderators/{moderator_id}/polls/{poll_id}", method={RequestMethod.GET}, produces = "application/json")
	public ResponseEntity<Poll> searchPollWithResult(@PathVariable("moderator_id") Integer mod_id ,@PathVariable("poll_id") String poll_id, @RequestHeader(value="Authorization") String authorizationDetail) throws UnsupportedEncodingException{
		System.out.println("Searching a poll with results");
		boolean authenticationSuccess = checkAuthorizationDetail(authorizationDetail);
		if(authenticationSuccess){
			for(Moderator mod: moderator_list){
				if(mod.getId() == mod_id){
					for(Poll pollObject: mod.getPollList()){
						if(pollObject.getId().toString().equals(poll_id)){
							return new ResponseEntity<Poll>(pollObject, HttpStatus.OK);
						}
					}
				}
			}
		}
		return new ResponseEntity<Poll>(HttpStatus.BAD_REQUEST);
	}

	@RequestMapping(value="/moderators/{moderator_id}/polls", method={RequestMethod.GET}, produces = "application/json")
	public ResponseEntity<ArrayList<Poll>> listPolls(@PathVariable("moderator_id") Integer mod_id, @RequestHeader(value="Authorization") String authorizationDetail) throws UnsupportedEncodingException{
		System.out.println("Listing all polls for a specific moderator");
		boolean authenticationSuccess = checkAuthorizationDetail(authorizationDetail);
		if(authenticationSuccess){
			for(Moderator mod: moderator_list){
				if(mod.getId() == mod_id){
					return new ResponseEntity<ArrayList<Poll>>(mod.getPollList(), HttpStatus.OK);
				}
			}
		}
		return new ResponseEntity<ArrayList<Poll>>(HttpStatus.BAD_REQUEST);
	}
	
	@RequestMapping(value="/moderators/{moderator_id}/polls/{poll_id}", method={RequestMethod.DELETE})
	public ResponseEntity<String> deletePoll(@PathVariable("moderator_id") Integer mod_id ,@PathVariable("poll_id") String poll_id, @RequestHeader(value="Authorization") String authorizationDetail) throws UnsupportedEncodingException{
		System.out.println("Deleting a poll...");
		boolean authenticationSuccess = checkAuthorizationDetail(authorizationDetail);
		if(authenticationSuccess){
			for(Moderator mod: moderator_list){
				if(mod.getId() == mod_id){
					for(Poll pollObject: mod.getPollList()){
						if(pollObject.getId().toString().equals(poll_id)){
							mod.getPollList().remove(pollObject);
							return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
						}
					}
				}
			}
		}
		return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
	}
	
	@RequestMapping(value="/polls/{poll_id}", method={RequestMethod.PUT})
	public ResponseEntity<String> votePoll(@PathVariable("poll_id") String poll_id, @RequestParam("choice") Integer choice){
		System.out.println("Voting on a poll...");
		for(Poll poll: poll_list){
			if(poll.getId().toString().equals(poll_id)){
				poll.getResults()[choice]++;
				return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
			}
		}
		return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
	}
	
	public boolean checkAuthorizationDetail(String authorizationDetail) throws UnsupportedEncodingException {
		String[] authorizationDetailArray = authorizationDetail.split(" ");
		byte[] decodedString = Base64.decodeBase64(authorizationDetailArray[1]);
		String authorizationString = new String(decodedString, "UTF-8");
		if (authorizationString.indexOf(":") > 0) {
			String[] credentials = authorizationString.split(":");
			String username = credentials[0];
			String password = credentials[1];
			if (username.equals("foo") && password.equals("bar"))
				return true;
		}
		return false;

	}
}