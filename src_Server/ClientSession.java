import java.net.InetAddress;
import java.net.Socket;
import java.util.Vector;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;

public class ClientSession implements Runnable {

	private Socket clientSocket; // �������� �Ѱ� ���� Ŭ���̾�Ʈ ����
	private BufferedReader bufferdReader; // Ŭ���̾�Ʈ�� ���� ������ ����
	private BufferedWriter bufferedWriter; // Ŭ���̾�Ʈ���� ������ ����
	private Account loginData; // �α����� ���� ������

	public ClientSession(Socket socket_) {

		bufferdReader = null;
		bufferedWriter = null;
		loginData = null;
		this.clientSocket = socket_;

		InetAddress inetAddress = clientSocket.getInetAddress(); // ������ Ŭ���̾�Ʈ�� IP�� ���� �ܼ�â�� ���
		System.out.println(inetAddress + " connected!");

		// �⺻������ simple json ���̺귯���� �̿��� json ���� �����͸� �ְ� ������, �ۼ��� ������ ������ ���ڿ��̴�.
		try {
			bufferdReader = new BufferedReader(new InputStreamReader(socket_.getInputStream(), "utf-8"));
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket_.getOutputStream(), "utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		System.out.println("ClientSession Start");
		try {
			while (true) { // ������ �����ϴ� ���ȿ��� �޽����� ���� �� �ִ�.

				// �޽��� ������ ���ڿ� ���پ� �޴� ��.
				String strMsg = (String) bufferdReader.readLine();
				if (strMsg == null || strMsg.equals(""))
					break;

				// Ŭ���̾�Ʈ�� ���� ���� ���ڿ��� ó�� �Ѵ�. handler �Լ� ���ο����� json���� �ٲپ� ó�� �Ѵ�.
				try {
					handleMessage(strMsg);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
		}

		Release();
		System.out.println("SessionEnd");
	}

	// Ŭ���̾�Ʈ�� ���� ���� �����͸� ó���ϴ� �޼ҵ�
	private void handleMessage(String message) {

		// �����ð� �̻� �����͸� �ְ� ���� ������ ������ ����Ƿ�, Ŭ���̾�Ʈ���� 60�ʸ��� �� ���� �����͸� �ش�.
		// Ŭ���̾�Ʈ�� ���� ���� �޽���. alive-check ���ڿ��� ������ Ư���� ó�� ���� �޼ҵ带 ���� �Ѵ�.
		if (message.contains("alive-check")) {
			System.out.println("Received " + message);
			return;
		}

		JSONParser parser = new JSONParser();
		Object obj = null;

		// Ŭ���̾�Ʈ�� ���� �Է� ���� ���ڿ��� json���� �Ľ� �Ѵ�.
		try {
			obj = parser.parse(message);
		} catch (Exception e) {
			System.out.println("Received " + message);
			e.printStackTrace();
			return;
		}

		JSONObject jsonObj = (JSONObject) obj;

		EMessageCode eCode = getMessageCode(jsonObj);
		System.out.println("Received " + message + ", Code " + eCode.toString());

		// ��û ��ɿ� �°� �� �޼ҵ带 ȣ���Ͽ� �ڵ带 �л��Ͽ� ó�� �Ѵ�.
		switch (eCode) {
		case eLoginRequest:
			OnLoginRequest(jsonObj); // �α���
			break;

		case eIDCheckRequest:
			OnIDCheckRequest(jsonObj); // ���̵� �ߺ� üũ
			break;

		case eJoinRequest:
			OnJoinRequest(jsonObj); // ����
			break;

		case eMembershipEditRequest:
			OnMembershipEditRequest(jsonObj); // ȸ�� ���� ���� (�г��� �� ����)
			break;

		case eRankingRequest:
			OnRankingRequest(jsonObj); // ��ŷ ��û (10�� �� ���� ��ŷ)
			break;

		case eUpdateGameScoreRequest:
			OnUpdateGameScoreRequest(jsonObj); // ���� ������ MySQL (RDBMS)�� ����
			break;

		case eSelfTestPointUpdateRequest:
			OnSelfTestPointUpdateRequest(jsonObj); // �ڰ����� ��� ���� ����
			break;

		default:
			break;
		}
	}

	// �α��� ó��
	private void OnLoginRequest(JSONObject received) {
		EMessageCode eResponseType = EMessageCode.eLoginResponse;
		JSONObject obj = new JSONObject();

		if (loginData != null) {
			obj.put("message", "�̹� �α����� ���� �Դϴ�.");
			obj.put("result", false);
			this.Send(eResponseType, obj);
			return;
		}

		String loginid = (String) received.get("id");
		String passwd = (String) received.get("pw");
		Account new_login = DBManager.Login(loginid, passwd);
		if (new_login == null || new_login.loginid.equals(loginid) == false) {
			obj.put("message", "���̵� �� �� �Ǿ����ϴ�.");
			obj.put("result", false);
			this.Send(eResponseType, obj);
			return;
		}

		// �н����� ����
		if (new_login.passwrd.equals(passwd) == false) {
			obj.put("message", "�н����尡 �� �� �Ǿ����ϴ�.");
			obj.put("result", false);
			this.Send(eResponseType, obj);
			return;
		}

		loginData = new_login;

		// �α��� ������ DB���� ������ �����͸� Ŭ���̾�Ʈ�� ��ȯ �Ѵ�.
		obj.put("loginid", new_login.loginid);
		obj.put("nickname", new_login.nickname);
		obj.put("passwrd", new_login.passwrd);
		obj.put("scoreMole", new_login.scoreMole);
		obj.put("scorePicture", new_login.scorePicture);
		obj.put("infomation", new_login.infomation);
		obj.put("pointSelfTest", new_login.pointSelfTest);
		obj.put("name", new_login.name);
		obj.put("address", new_login.address);
		obj.put("age", new_login.age);
		obj.put("job", new_login.job);
		obj.put("isFemale", new_login.isFemale);

		obj.put("message", "�α��ο� ���� �Ͽ����ϴ�.");
		obj.put("result", true);
		this.Send(eResponseType, obj);
	}

	// ���� ���̵� �ߺ� Ȯ��
	private void OnIDCheckRequest(JSONObject received) {
		EMessageCode eResponseType = EMessageCode.eIDCheckResponse;
		JSONObject obj = new JSONObject();

		if (loginData != null) {
			obj.put("message", "�̹� �α����� ���� �Դϴ�.");
			obj.put("result", false);
			this.Send(eResponseType, obj);
			return;
		}

		String loginid = (String) received.get("id");
		boolean canUseID = DBManager.CanUseLoginID(loginid);
		if (canUseID == false) {
			obj.put("message", "�̹� ��� ���� ���� ���̵� �Դϴ�.");
			obj.put("result", false);
			this.Send(eResponseType, obj);
			return;
		}

		obj.put("message", "��� ������ ���� ���̵� �Դϴ�.");
		obj.put("result", true);
		this.Send(eResponseType, obj);
	}

	// ȸ������ó��
	private void OnJoinRequest(JSONObject received) {
		EMessageCode eResponseType = EMessageCode.eJoinResponse;
		JSONObject obj = new JSONObject();

		if (loginData != null) {
			obj.put("message", "�̹� �α����� ���� �Դϴ�.");
			obj.put("result", false);
			this.Send(eResponseType, obj);
			return;
		}

		try {
			String name = (String) received.get("name");
			String loginid = (String) received.get("loginid");
			String passwrd = (String) received.get("passwrd");
			String address = (String) received.get("address");
			int age = ((Long) received.get("age")).intValue();
			String job = (String) received.get("job");
			boolean isFemale = (boolean) received.get("isFemale");

			// ���̵� �ߺ� Ȯ��
			boolean canUseID = DBManager.CanUseLoginID(loginid);
			if (canUseID == false) {
				obj.put("message", "�̹� ��� ���� ���� ���̵� �Դϴ�.");
				obj.put("result", false);
				this.Send(eResponseType, obj);
				return;
			}

			// DB�� ���� ������ ����
			boolean result = DBManager.InsertJoinAccount(loginid, name, passwrd, address, age, job, isFemale);
			if (result == false) {
				obj.put("message", "ó���� ���� �߽��ϴ�. �����ڿ��� ���� ��Ź�帳�ϴ�.");
				obj.put("result", false);
				this.Send(eResponseType, obj);
				return;
			}

			// ó�� ����� �ٽ� Ŭ���̾�Ʈ�� �˸���.
			obj.put("name", name);
			obj.put("loginid", loginid);
			obj.put("passwrd", passwrd);
			obj.put("address", address);
			obj.put("age", age);
			obj.put("job", job);
			obj.put("isFemale", isFemale);

		} catch (Exception e) {
			e.printStackTrace();
			obj.put("message", "ó���� ���� �߽��ϴ�. �����ڿ��� ���� ��Ź�帳�ϴ�.");
			obj.put("result", false);
			this.Send(eResponseType, obj);
			return;
		}

		// ���� ����
		obj.put("message", "���Կ� ���� �Ͽ����ϴ�.");
		obj.put("result", true);
		this.Send(eResponseType, obj);
	}

	// �г��� �� ���� ����
	private void OnMembershipEditRequest(JSONObject received) {
		EMessageCode eResponseType = EMessageCode.eMembershipEditResponse;
		JSONObject obj = new JSONObject();

		if (loginData == null) {
			obj.put("message", "�α��� ���°� �ƴմϴ�.");
			obj.put("result", false);
			this.Send(eResponseType, obj);
			return;
		}

		String info = (String) received.get("infomation");
		String nick = (String) received.get("nickname");

		// DB�� ����
		DBManager.UpdateNickAndInfo(loginData.loginid, nick, info);

		obj.put("nickname", nick);
		obj.put("infomation", info);
		obj.put("message", "ó���� ���� �Ͽ����ϴ�.");
		obj.put("result", true);
		this.Send(eResponseType, obj);
	}

	// ��ŷ ������ Ŭ���̾�Ʈ�� ����
	private void OnRankingRequest(JSONObject received) {
		EMessageCode eResponseType = EMessageCode.eRankingResponse;
		JSONObject obj = new JSONObject();

		if (loginData == null) {
			obj.put("message", "�α��� ���°� �ƴմϴ�.");
			obj.put("result", false);
			this.Send(eResponseType, obj);
			return;
		}

		// ��� �Ǵ� ranktype ���ڿ� ������ Ŭ���̾�Ʈ���� ������� DB �÷����� ����.
		String ranktype = (String) received.get("ranktype");
		
		// ��ŷ �����͸� DB ���� ���� �´�.
		Vector<Account> accounts = DBManager.SelectTotalAccountsForRank(ranktype);
		if (accounts == null || accounts.size() == 0) {
			obj.put("message", "��ŷ �����Ͱ� �����ϴ�.");
			obj.put("result", false);
			this.Send(eResponseType, obj);
			return;
		}

		// 10�� ��ŷ
		for (int i = 0; i < accounts.size() && i < 10; ++i) {
			Account accountdata = accounts.get(i);

			int rankNo = i + 1;
			obj.put("rank" + rankNo, accountdata.nickname);
			obj.put("scoreMole" + rankNo, accountdata.scoreMole);
			obj.put("scorePicture" + rankNo, accountdata.scorePicture);
		}

		// ���� ��ŷ
		for (int i = 0; i < accounts.size(); ++i) {
			Account accountdata = accounts.get(i);

			if (accountdata.loginid.equals(loginData.loginid) == false)
				continue;

			int rankNo = i + 1;
			obj.put("myrank", rankNo);
			obj.put("nickname", accountdata.nickname);
			obj.put("scoreMole", accountdata.scoreMole);
			obj.put("scorePicture", accountdata.scorePicture);
		}

		obj.put("ranktype", ranktype);
		obj.put("message", "��ŷ �����͸� ���� ���� �� ���� �Ͽ����ϴ�.");
		obj.put("result", true);
		this.Send(eResponseType, obj);
	}

	// ���� ���� ����
	private void OnUpdateGameScoreRequest(JSONObject received) {
		EMessageCode eResponseType = EMessageCode.eUpdateGameScoreResponse;
		JSONObject obj = new JSONObject();

		if (loginData == null) {
			obj.put("message", "�α��� ���°� �ƴմϴ�.");
			obj.put("result", false);
			this.Send(eResponseType, obj);
			return;
		}

		String gametype = (String) received.get("gametype");
		int score = ((Long) received.get("score")).intValue();

		// �� �������� �°� DB�� ����
		if (gametype.equals("mole")) {
			DBManager.UpdateScoreMole(loginData.loginid, score);
		} else {
			DBManager.UpdateScorePicture(loginData.loginid, score);
		}

		obj.put("message", "���� ���ſ� ���� �Ͽ����ϴ�.");
		obj.put("result", true);
		this.Send(eResponseType, obj);
	}

	// �ڰ����� ��� ���� ����
	private void OnSelfTestPointUpdateRequest(JSONObject received) {
		EMessageCode eResponseType = EMessageCode.eSelfTestPointUpdateResponse;
		JSONObject obj = new JSONObject();

		if (loginData == null) {
			obj.put("message", "�α��� ���°� �ƴմϴ�.");
			obj.put("result", false);
			this.Send(eResponseType, obj);
			return;
		}

		int score = ((Long) received.get("point")).intValue();
		
		
		// DB�� ����
		DBManager.UpdateSelfTest(loginData.loginid, score);

		obj.put("message", "���� ���ſ� ���� �Ͽ����ϴ�.");
		obj.put("result", true);
		this.Send(eResponseType, obj);
	}

	// Ŭ���̾�Ʈ�� ���� ���� ���� �����Ͱ� � ����� ���������� ��û����, EMessageCode�� ã�Ƽ� ��ȯ
	private EMessageCode getMessageCode(JSONObject obj) {
		Object result = obj.get("code");
		if (result == null)
			return EMessageCode.eNone;

		EMessageCode eCode = EMessageCode.valueOf((String) result);
		return eCode;
	}

	// ���� ���� �ڿ����� �ݳ��Ѵ�.
	private void Release() {
		// �������� ���� �ڿ��� ���� �͵� ���� ��ȯ
		try {
			bufferdReader.close();
		} catch (Exception e) {
		}

		try {
			bufferedWriter.close();
		} catch (Exception e) {
		}

		// ������ ���� �������� Close
		try {
			clientSocket.close();
		} catch (Exception e) {
		}

		clientSocket = null;
		bufferdReader = null;
		bufferedWriter = null;
	}

	// ���� Ŭ���̾�Ʈ�� ����
	public void Send(EMessageCode eCode, JSONObject obj) {
		try {
			obj.put("code", eCode.toString()); // � ��ɿ� ���� ó�� ������� ���� ��Ų��.
			bufferedWriter.write(obj.toString() + "\n"); // ���ڿ� ���� ó���̹Ƿ� �������� \n�� �ٿ� �ش�.
			bufferedWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}