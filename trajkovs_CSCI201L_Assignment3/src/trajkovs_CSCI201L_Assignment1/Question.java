package trajkovs_CSCI201L_Assignment1;

public class Question {
	private String Category;
	private int PointValue;
	private String Question;
	private String Answer;
	private boolean Answered;
	
	public Question (String Category, int PointValue, String Question, String Answer) {
		this.Category = Category;
		this.PointValue = PointValue;
		this.Question = Question;
		this.Answer = Answer;
	}

	public Question (String Question, String Answer) {
		this.Category = null;
		this.PointValue = 0;
		this.Question = Question;
		this.Answer = Answer;

		this.Answered = false;
	}
	
	public String getCategory() {
		return Category;
	}

	public void setCategory(String category) {
		Category = category;
	}

	public int getPointValue() {
		return PointValue;
	}

	public void setPointValue(int pointValue) {
		PointValue = pointValue;
	}

	public String getQuestion() {
		return Question;
	}

	public void setQuestion(String question) {
		Question = question;
	}

	public String getAnswer() {
		return Answer;
	}

	public void setAnswer(String answer) {
		Answer = answer;
	}
	
	public boolean isAnswered() {
		return this.Answered;
	}
	
	public void setAnswered() {
		Answered = true;
	}
	
	public void setUnanswered() {
		Answered = false;
	}
}
