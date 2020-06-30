import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.simple.JSONObject;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

// 메인 클래스
// 각 화면은 해당 메인 클래스를 통해 출력 된다.
class MainWindow extends JFrame implements MouseListener, Runnable, MessageHandler {

	private ClientApplication client; // 서버에 접속 하고 데이터를 주고 받는 기능
	private GameMole gameMole; // 두더지잡기 게임 화면
	private GamePicture gamePicture; // 같은 그림 맞추기 게임 화면
	private Login loginState; // 로그인 화면. 프로그램 최초의 화면
	private Join joinState; // 회원 가입 화면
	private MembershipEdit membershipEditState; // 회원 정보 수정 - 닉네임 및 정보
	private SelectRank selectRankState; // 랭킹을 선택 하는 화면. 여기서 선택을 하면 Rank 화면이 호출 된다.
	private Rank rankState; // 랭킹 10위 출력 화면
	private HomeMenu menuState; // 메뉴 선택 화면
	private SelfTest selfTestState; // 자가진단 화면
	private Thread thread; // 주기적으로 서버에 임의의 데이터를 보내어 접속을 유지 시키는 데 사용
	private Account userData; // 로그인 계정

	public MainWindow() {
		// 타이틀 이름
		super("치매 예방 프로그램");

		client = null;
		userData = null;

		setSize(ConfigDatas.WIN_WIDTH, ConfigDatas.WIN_HEIGHT);
		setResizable(false);
		setVisible(true);
		setLayout(null); // 레이아웃 없이 처리한다.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		addMouseListener(this);
		
		 // 주기적으로 서버에 임의의 데이터를 보내어 접속을 유지 시키는 데 사용
		thread = new Thread(this);
		thread.start();
	}

	// 자가진단 화면을 연다.
	public void ShowSelfTest() {
		HideAll();

		selfTestState = new SelfTest(this);
		this.add(selfTestState);
		this.repaint();
	}

	public void HideSelfTest() {
		if (selfTestState != null) {
			this.remove(selfTestState);
			selfTestState = null;
		}
	}

	// 선택 메뉴 화면을 연다.
	public void ShowHomeMenu() {
		HideAll();

		menuState = new HomeMenu(this);
		this.add(menuState);
		this.repaint();
	}

	public void HideHomeMenu() {
		if (menuState != null) {
			this.remove(menuState);
			menuState = null;
		}
	}

	// 서버로 부터 받은 랭킹  데이터를 보여준다.
	public void ShowRank(JSONObject obj) {
		HideAll();

		rankState = new Rank(this, obj);
		this.add(rankState);
		this.repaint();
	}

	public void HideRank() {
		if (rankState != null) {
			this.remove(rankState);
			rankState = null;
		}
	}

	// 회원 정보 수정 화면을 연다. (닉네임 및 정보)
	public void ShowMembershipEdit() {
		HideAll();

		membershipEditState = new MembershipEdit(this);
		this.add(membershipEditState);
		this.repaint();
	}

	public void HideMembershipEdit() {
		if (membershipEditState != null) {
			this.remove(membershipEditState);
			membershipEditState = null;
		}
	}

	// 로그인 화면을 연다.
	public void ShowLogin() {
		HideAll();

		loginState = new Login(this);
		System.out.println("ShowLogin");
		this.add(loginState);
		this.repaint();
	}

	public void HideLogin() {
		if (loginState != null) {
			this.remove(loginState);
			loginState = null;
		}
	}

	// 회원 가입 화면을 연다.
	public void ShowJoin() {
		HideAll();

		joinState = new Join(this);
		this.add(joinState);
		this.repaint();
	}

	public void HideJoin() {
		if (joinState != null) {
			this.remove(joinState);
			joinState = null;
		}
	}

	// 두더지잡기 화면을 연다.
	public void StartGameMole() {
		HideAll();

		gameMole = new GameMole(this);
		this.add(gameMole);
		this.repaint();
	}

	public void HideGameMole() {
		if (gameMole != null) {
			gameMole.stopGame();
			this.remove(gameMole);
			gameMole = null;
		}
	}

	// 같은 그림 찾기 화면을 연다.
	public void StartGamePicture() {
		HideAll();

		gamePicture = new GamePicture(this);
		this.add(gamePicture);
		this.repaint();
	}

	public void HideGamePicture() {
		if (gamePicture != null) {
			gamePicture.stopGame();
			this.remove(gamePicture);
			gamePicture = null;
		}
	}

	// 랭킹 선택 화면을 연다.
	public void ShowSelectRank() {
		HideAll();

		selectRankState = new SelectRank(this);
		this.add(selectRankState);
		this.repaint();
	}

	public void HideSelectRank() {
		if (selectRankState != null) {
			this.remove(selectRankState);
			selectRankState = null;
		}
	}

	// 모든 화면을 닫는 다.
	private void HideAll() {
		this.HideHomeMenu();
		this.HideJoin();
		this.HideLogin();
		this.HideMembershipEdit();
		this.HideRank();
		this.HideSelectRank();
		this.HideSelfTest();
		this.HideGameMole();
		this.HideGamePicture();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// 클릭한 화면 좌표 출력 해본다.
		System.out.println("X " + e.getX() + ", Y " + e.getY());
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	// 서버로 메시지 전송
	public void sendMessage(EMessageCode eOperation, JSONObject obj) {
		if (client == null || client.IsConnected() == false)
			return;

		obj.put("code", eOperation.toString());
		client.SendMessage(obj.toString());
	}

	@Override
	public void run() {

		// 클라이언트를 서버에 접속 시킨다.
		client = new ClientApplication(this);
		if (client.Connect(ConfigDatas.SERVER_IP, ConfigDatas.SERVER_PORT) == false) {
			int dialogButton = JOptionPane.CLOSED_OPTION;
			JOptionPane.showConfirmDialog(null, "서버 접속에 실패 하였습니다.", "접속 오류", dialogButton);

			// 1초 대기
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}

			// 접속 실패시 프로그램 종료
			System.exit(0);
		}

		// 주기적으로 서버에 임의의 데이터 "alive-check"를 보내어 접속을 유지 시킨다. 문자열 내용은 상관 없다. 주기적으로 데이터를 보내는 것이 중요.
		while (client.IsConnected() == true) {

			try {
				Thread.sleep(60 * 1000); // 60초 간격
			} catch (InterruptedException e) {
				break;
			}

			if (client.IsConnected() == false)
				break;

			// 일정시간 이상 데이터를 주고 받지 않으면 접속이 끊기므로, 클라이언트에서 60초마다 한 번씩 서버로 데이터를 준다.
			client.SendMessage("alive-check");
		}
	}

	// 서버로 부터 받은 데이터를 처리 한다.
	@Override
	public void handleMessage(JSONObject obj) {
		EMessageCode eCode = getMessageCode(obj);
		System.out.println("ReceivedMessageCode " + eCode);

		String msg = (String) obj.get("message");
		boolean result = (boolean) obj.get("result");

		// 서버에서 처리 실패를 알렸다.
		if (result == false) {
			MessageBoxUtil.ShowMessageBox(msg, "실패");
			return;
		}

		// 이하, 처리가 성공 했을 때 수행 된다.
		// 각 메서드에서 처리를 수행하게 하여 코드를 분산 한다.
		switch (eCode) {

		case eIDCheckResponse:
			// 알림 메시지 외에는 특별히 처리 하지 않는 다.
			MessageBoxUtil.ShowMessageBox(msg, "알림");
			break;

		case eLoginResponse:
			this.OnLoginResponse(obj);
			break;

		case eJoinResponse:
			MessageBoxUtil.ShowMessageBox(msg, "알림");
			this.OnJoinResponse(obj);
			break;

		case eMembershipEditResponse:
			MessageBoxUtil.ShowMessageBox(msg, "알림");
			this.OnMembershipEditResponse(obj);
			break;

		case eUpdateGameScoreResponse:
			MessageBoxUtil.ShowMessageBox(msg, "알림");
			break;

		case eRankingResponse:
			MessageBoxUtil.ShowMessageBox(msg, "알림");
			this.ShowRank(obj);
			break;

		case eSelfTestPointUpdateResponse:
			MessageBoxUtil.ShowMessageBox(msg, "알림");
			break;

		default:
			break;
		}
	}

	// 가입 처리 결과
	private void OnJoinResponse(JSONObject received) {
		String name = (String) received.get("name");
		String loginid = (String) received.get("loginid");
		String passwrd = (String) received.get("passwrd");
		String address = (String) received.get("address");
		int age = ((Long) received.get("age")).intValue();
		String job = (String) received.get("job");
		boolean isFemale = (boolean) received.get("isFemale");

		this.userData = new Account();
		this.userData.loginid = loginid;
		this.userData.passwrd = passwrd;
		this.userData.address = address;
		this.userData.age = age;
		this.userData.job = job;
		this.userData.isFemale = isFemale;

		System.out.println("ShowHomeMenu");
		this.ShowHomeMenu(); // 메뉴 선택 화면으로 간다.
	}

	// 로그인 결과
	private void OnLoginResponse(JSONObject received) {
		String name = (String) received.get("name");
		String nickname = (String) received.get("nickname");
		String loginid = (String) received.get("loginid");
		String passwrd = (String) received.get("passwrd");
		String address = (String) received.get("address");
		int age = ((Long) received.get("age")).intValue();
		String job = (String) received.get("job");
		boolean isFemale = (boolean) received.get("isFemale");
		int scoreMole = ((Long) received.get("scoreMole")).intValue();
		int scorePicture = ((Long) received.get("scorePicture")).intValue();
		String infomation = (String) received.get("infomation");
		int pointSelfTest = ((Long) received.get("pointSelfTest")).intValue();

		this.userData = new Account();
		this.userData.loginid = loginid;
		this.userData.nickname = nickname;
		this.userData.passwrd = passwrd;
		this.userData.address = address;
		this.userData.age = age;
		this.userData.job = job;
		this.userData.isFemale = isFemale;
		this.userData.scoreMole = scoreMole;
		this.userData.scorePicture = scorePicture;
		this.userData.infomation = infomation;
		this.userData.pointSelfTest = pointSelfTest;

		System.out.println("ShowHomeMenu");
		this.ShowHomeMenu(); // 메뉴 선택 화면으로 간다.
	}

	// 회원 정보 수정 결과
	private void OnMembershipEditResponse(JSONObject received) {
		String nickname = (String) received.get("nickname");
		String infomation = (String) received.get("infomation");

		this.userData.nickname = nickname;
		this.userData.infomation = infomation;

		System.out.println("ShowHomeMenu");
		this.ShowHomeMenu();
	}

	// 서버로 부터 전송 받은 데이터가 어떤 명령의 처리 결과인지, EMessageCode를 찾아서 반환
	private EMessageCode getMessageCode(JSONObject obj) {
		Object result = obj.get("code");
		if (result == null)
			return EMessageCode.eNone;

		EMessageCode eCode = EMessageCode.valueOf((String) result);
		return eCode;
	}

	public Account GetUserData() {
		return this.userData;
	}
}

// 메인 클래스. MainWindow 객체를 만들고, 첫 화면인 로그인 화면을 보여준다.
public class Main {

	public static void main(String[] args) {
		try {
			MainWindow window = new MainWindow();
			window.ShowLogin(); // 로그인 화면이 첫 페이지다.
		} catch (Exception e_) {
			e_.printStackTrace();
			System.out.println("Quit program");
		}
	}
}