import org.json.simple.JSONObject;

// Main�� implements �ϸ�, Main �� ����� ������ ClientApplication Ŭ������  Main ��ü�� �����Ͽ� 
// ����� ���� ó���� Main�� �ϵ��� �� �� ���
public interface MessageHandler {
	public void handleMessage(JSONObject obj);
}