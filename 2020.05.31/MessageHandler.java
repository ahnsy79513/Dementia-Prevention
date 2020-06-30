import org.json.simple.JSONObject;

// Main의 implements 하며, Main 이 멤버로 가지는 ClientApplication 클래스에  Main 객체를 전달하여 
// 결과에 대한 처리는 Main이 하도록 할 때 사용
public interface MessageHandler {
	public void handleMessage(JSONObject obj);
}