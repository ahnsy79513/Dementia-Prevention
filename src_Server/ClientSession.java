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

	private Socket clientSocket; // 서버에서 넘겨 받은 클라이언트 소켓
	private BufferedReader bufferdReader; // 클라이언트로 부터 데이터 수신
	private BufferedWriter bufferedWriter; // 클라이언트에게 데이터 전송
	private Account loginData; // 로그인한 유저 데이터

	public ClientSession(Socket socket_) {

		bufferdReader = null;
		bufferedWriter = null;
		loginData = null;
		this.clientSocket = socket_;

		InetAddress inetAddress = clientSocket.getInetAddress(); // 접속한 클라이언트의 IP를 서버 콘솔창에 출력
		System.out.println(inetAddress + " connected!");

		// 기본적으로 simple json 라이브러리를 이용해 json 으로 데이터를 주고 받으며, 송수신 데이터 포맷은 문자열이다.
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
			while (true) { // 서버가 동작하는 동안에만 메시지를 받을 수 있다.

				// 메시지 전송은 문자열 한줄씩 받는 다.
				String strMsg = (String) bufferdReader.readLine();
				if (strMsg == null || strMsg.equals(""))
					break;

				// 클라이언트로 부터 받은 문자열을 처리 한다. handler 함수 내부에서는 json으로 바꾸어 처리 한다.
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

	// 클라이언트로 부터 받은 데이터를 처리하는 메소드
	private void handleMessage(String message) {

		// 일정시간 이상 데이터를 주고 받지 않으면 접속이 끊기므로, 클라이언트에서 60초마다 한 번씩 데이터를 준다.
		// 클라이언트의 연결 유지 메시지. alive-check 문자열을 받으면 특별한 처리 없이 메소드를 종료 한다.
		if (message.contains("alive-check")) {
			System.out.println("Received " + message);
			return;
		}

		JSONParser parser = new JSONParser();
		Object obj = null;

		// 클라이언트로 부터 입력 받은 문자열을 json으로 파싱 한다.
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

		// 요청 명령에 맞게 각 메소드를 호출하여 코드를 분산하여 처리 한다.
		switch (eCode) {
		case eLoginRequest:
			OnLoginRequest(jsonObj); // 로그인
			break;

		case eIDCheckRequest:
			OnIDCheckRequest(jsonObj); // 아이디 중복 체크
			break;

		case eJoinRequest:
			OnJoinRequest(jsonObj); // 가입
			break;

		case eMembershipEditRequest:
			OnMembershipEditRequest(jsonObj); // 회원 정보 수정 (닉네임 및 정보)
			break;

		case eRankingRequest:
			OnRankingRequest(jsonObj); // 랭킹 요청 (10위 및 나의 랭킹)
			break;

		case eUpdateGameScoreRequest:
			OnUpdateGameScoreRequest(jsonObj); // 게임 점수를 MySQL (RDBMS)에 저장
			break;

		case eSelfTestPointUpdateRequest:
			OnSelfTestPointUpdateRequest(jsonObj); // 자가진단 결과 점수 저장
			break;

		default:
			break;
		}
	}

	// 로그인 처리
	private void OnLoginRequest(JSONObject received) {
		EMessageCode eResponseType = EMessageCode.eLoginResponse;
		JSONObject obj = new JSONObject();

		if (loginData != null) {
			obj.put("message", "이미 로그인한 상태 입니다.");
			obj.put("result", false);
			this.Send(eResponseType, obj);
			return;
		}

		String loginid = (String) received.get("id");
		String passwd = (String) received.get("pw");
		Account new_login = DBManager.Login(loginid, passwd);
		if (new_login == null || new_login.loginid.equals(loginid) == false) {
			obj.put("message", "아이디가 잘 못 되었습니다.");
			obj.put("result", false);
			this.Send(eResponseType, obj);
			return;
		}

		// 패스워드 오류
		if (new_login.passwrd.equals(passwd) == false) {
			obj.put("message", "패스워드가 잘 못 되었습니다.");
			obj.put("result", false);
			this.Send(eResponseType, obj);
			return;
		}

		loginData = new_login;

		// 로그인 성공시 DB에서 가져온 데이터를 클라이언트로 반환 한다.
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

		obj.put("message", "로그인에 성공 하였습니다.");
		obj.put("result", true);
		this.Send(eResponseType, obj);
	}

	// 계정 아이디 중복 확인
	private void OnIDCheckRequest(JSONObject received) {
		EMessageCode eResponseType = EMessageCode.eIDCheckResponse;
		JSONObject obj = new JSONObject();

		if (loginData != null) {
			obj.put("message", "이미 로그인한 상태 입니다.");
			obj.put("result", false);
			this.Send(eResponseType, obj);
			return;
		}

		String loginid = (String) received.get("id");
		boolean canUseID = DBManager.CanUseLoginID(loginid);
		if (canUseID == false) {
			obj.put("message", "이미 사용 중인 계정 아이디 입니다.");
			obj.put("result", false);
			this.Send(eResponseType, obj);
			return;
		}

		obj.put("message", "사용 가능한 계정 아이디 입니다.");
		obj.put("result", true);
		this.Send(eResponseType, obj);
	}

	// 회원가입처리
	private void OnJoinRequest(JSONObject received) {
		EMessageCode eResponseType = EMessageCode.eJoinResponse;
		JSONObject obj = new JSONObject();

		if (loginData != null) {
			obj.put("message", "이미 로그인한 상태 입니다.");
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

			// 아이디 중복 확인
			boolean canUseID = DBManager.CanUseLoginID(loginid);
			if (canUseID == false) {
				obj.put("message", "이미 사용 중인 계정 아이디 입니다.");
				obj.put("result", false);
				this.Send(eResponseType, obj);
				return;
			}

			// DB에 계정 데이터 삽입
			boolean result = DBManager.InsertJoinAccount(loginid, name, passwrd, address, age, job, isFemale);
			if (result == false) {
				obj.put("message", "처리에 실패 했습니다. 관리자에게 문의 부탁드립니다.");
				obj.put("result", false);
				this.Send(eResponseType, obj);
				return;
			}

			// 처리 결과를 다시 클라이언트에 알린다.
			obj.put("name", name);
			obj.put("loginid", loginid);
			obj.put("passwrd", passwrd);
			obj.put("address", address);
			obj.put("age", age);
			obj.put("job", job);
			obj.put("isFemale", isFemale);

		} catch (Exception e) {
			e.printStackTrace();
			obj.put("message", "처리에 실패 했습니다. 관리자에게 문의 부탁드립니다.");
			obj.put("result", false);
			this.Send(eResponseType, obj);
			return;
		}

		// 가입 성공
		obj.put("message", "가입에 성공 하였습니다.");
		obj.put("result", true);
		this.Send(eResponseType, obj);
	}

	// 닉네임 및 정보 변경
	private void OnMembershipEditRequest(JSONObject received) {
		EMessageCode eResponseType = EMessageCode.eMembershipEditResponse;
		JSONObject obj = new JSONObject();

		if (loginData == null) {
			obj.put("message", "로그인 상태가 아닙니다.");
			obj.put("result", false);
			this.Send(eResponseType, obj);
			return;
		}

		String info = (String) received.get("infomation");
		String nick = (String) received.get("nickname");

		// DB에 저장
		DBManager.UpdateNickAndInfo(loginData.loginid, nick, info);

		obj.put("nickname", nick);
		obj.put("infomation", info);
		obj.put("message", "처리에 성공 하였습니다.");
		obj.put("result", true);
		this.Send(eResponseType, obj);
	}

	// 랭킹 데이터 클라이언트에 전송
	private void OnRankingRequest(JSONObject received) {
		EMessageCode eResponseType = EMessageCode.eRankingResponse;
		JSONObject obj = new JSONObject();

		if (loginData == null) {
			obj.put("message", "로그인 상태가 아닙니다.");
			obj.put("result", false);
			this.Send(eResponseType, obj);
			return;
		}

		// 사용 되는 ranktype 문자열 내용은 클라이언트에서 보내어온 DB 컬럼값과 같다.
		String ranktype = (String) received.get("ranktype");
		
		// 랭킹 데이터를 DB 에서 가져 온다.
		Vector<Account> accounts = DBManager.SelectTotalAccountsForRank(ranktype);
		if (accounts == null || accounts.size() == 0) {
			obj.put("message", "랭킹 데이터가 없습니다.");
			obj.put("result", false);
			this.Send(eResponseType, obj);
			return;
		}

		// 10위 랭킹
		for (int i = 0; i < accounts.size() && i < 10; ++i) {
			Account accountdata = accounts.get(i);

			int rankNo = i + 1;
			obj.put("rank" + rankNo, accountdata.nickname);
			obj.put("scoreMole" + rankNo, accountdata.scoreMole);
			obj.put("scorePicture" + rankNo, accountdata.scorePicture);
		}

		// 나의 랭킹
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
		obj.put("message", "랭킹 데이터를 가져 오는 데 성공 하였습니다.");
		obj.put("result", true);
		this.Send(eResponseType, obj);
	}

	// 게임 점수 갱신
	private void OnUpdateGameScoreRequest(JSONObject received) {
		EMessageCode eResponseType = EMessageCode.eUpdateGameScoreResponse;
		JSONObject obj = new JSONObject();

		if (loginData == null) {
			obj.put("message", "로그인 상태가 아닙니다.");
			obj.put("result", false);
			this.Send(eResponseType, obj);
			return;
		}

		String gametype = (String) received.get("gametype");
		int score = ((Long) received.get("score")).intValue();

		// 각 게임종목에 맞게 DB에 저장
		if (gametype.equals("mole")) {
			DBManager.UpdateScoreMole(loginData.loginid, score);
		} else {
			DBManager.UpdateScorePicture(loginData.loginid, score);
		}

		obj.put("message", "점수 갱신에 성공 하였습니다.");
		obj.put("result", true);
		this.Send(eResponseType, obj);
	}

	// 자가진단 결과 점수 갱신
	private void OnSelfTestPointUpdateRequest(JSONObject received) {
		EMessageCode eResponseType = EMessageCode.eSelfTestPointUpdateResponse;
		JSONObject obj = new JSONObject();

		if (loginData == null) {
			obj.put("message", "로그인 상태가 아닙니다.");
			obj.put("result", false);
			this.Send(eResponseType, obj);
			return;
		}

		int score = ((Long) received.get("point")).intValue();
		
		
		// DB에 저장
		DBManager.UpdateSelfTest(loginData.loginid, score);

		obj.put("message", "점수 갱신에 성공 하였습니다.");
		obj.put("result", true);
		this.Send(eResponseType, obj);
	}

	// 클라이언트로 부터 전송 받은 데이터가 어떤 명령을 목적으로한 요청인지, EMessageCode를 찾아서 반환
	private EMessageCode getMessageCode(JSONObject obj) {
		Object result = obj.get("code");
		if (result == null)
			return EMessageCode.eNone;

		EMessageCode eCode = EMessageCode.valueOf((String) result);
		return eCode;
	}

	// 내가 가진 자원들을 반납한다.
	private void Release() {
		// 소켓으로 부터 자원을 얻은 것들 부터 반환
		try {
			bufferdReader.close();
		} catch (Exception e) {
		}

		try {
			bufferedWriter.close();
		} catch (Exception e) {
		}

		// 소켓은 가장 마지막에 Close
		try {
			clientSocket.close();
		} catch (Exception e) {
		}

		clientSocket = null;
		bufferdReader = null;
		bufferedWriter = null;
	}

	// 나의 클라이언트에 전송
	public void Send(EMessageCode eCode, JSONObject obj) {
		try {
			obj.put("code", eCode.toString()); // 어떤 명령에 대한 처리 결과인지 포함 시킨다.
			bufferedWriter.write(obj.toString() + "\n"); // 문자열 단위 처리이므로 마지막에 \n를 붙여 준다.
			bufferedWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}