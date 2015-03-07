package beans;

import org.hibernate.validator.constraints.NotBlank;

public class Poll {
	
	private String id;
	@NotBlank
	private String question;
	@NotBlank
	private String started_at;
	@NotBlank
	private String expired_at;
	@NotBlank
	private String[] choice;
	private int[] results;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getStarted_at() {
		return started_at;
	}
	public void setStarted_at(String started_at) {
		this.started_at = started_at;
	}
	public String getExpired_at() {
		return expired_at;
	}
	public void setExpired_at(String expired_at) {
		this.expired_at = expired_at;
	}
	public String[] getChoice() {
		return choice;
	}
	public void setChoice(String[] choice) {
		this.choice = choice;
	}
	public int[] getResults() {
		return results;
	}
	public void setResults(int[] results) {
		this.results = results;
	}
	
	public Poll() {
		super();
	}
	
	public Poll(String id, String question, String started_at, String expired_at, String[] choice, int[] results) {
		super();
		this.id = id;
		this.question = question;
		this.started_at = started_at;
		this.expired_at = expired_at;
		this.choice = choice;
		this.results = results;
	}
	
	public Poll(String id, String question, String started_at,	String expired_at, String[] choice) {
		super();
		this.id = id;
		this.question = question;
		this.started_at = started_at;
		this.expired_at = expired_at;
		this.choice = choice;
	}
}
