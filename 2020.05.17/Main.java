import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.json.simple.JSONObject;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

// ���� Ŭ����
// �� ȭ���� �ش� ���� Ŭ������ ���� ��� �ȴ�.
class MainWindow extends JFrame implements MouseListener, Runnable, MessageHandler {

	private ClientApplication client; // ������ ���� �ϰ� �����͸� �ְ� �޴� ���
	private GameMole gameMole; // �δ������ ���� ȭ��
	private GamePicture gamePicture; // ���� �׸� ���߱� ���� ȭ��
	private Login loginState; // �α��� ȭ��. ���α׷� ������ ȭ��
	private Join joinState; // ȸ�� ���� ȭ��
	private MembershipEdit membershipEditState; // ȸ�� ���� ���� - �г��� �� ����
	private SelectRank selectRankState; // ��ŷ�� ���� �ϴ� ȭ��. ���⼭ ������ �ϸ� Rank ȭ���� ȣ�� �ȴ�.
	private Rank rankState; // ��ŷ 10�� ��� ȭ��
	private HomeMenu menuState; // �޴� ���� ȭ��
	private SelfTest selfTestState; // �ڰ����� ȭ��
	private Thread thread; // �ֱ������� ������ ������ �����͸� ������ ������ ���� ��Ű�� �� ���
	private Account userData; // �α��� ����

	public MainWindow() {
		// Ÿ��Ʋ �̸�
		super("ġ�� ���� ���α׷�");

		client = null;
		userData = null;

		setSize(ConfigDatas.WIN_WIDTH, ConfigDatas.WIN_HEIGHT);
		setResizable(false);
		setVisible(true);
		setLayout(null); // ���̾ƿ� ���� ó���Ѵ�.
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		addMouseListener(this);
		
		 // �ֱ������� ������ ������ �����͸� ������ ������ ���� ��Ű�� �� ���
		thread = new Thread(this);
		thread.start();
	}

	// �ڰ����� ȭ���� ����.
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

	// ���� �޴� ȭ���� ����.
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

	// ������ ���� ���� ��ŷ  �����͸� �����ش�.
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

	// ȸ�� ���� ���� ȭ���� ����. (�г��� �� ����)
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

	// �α��� ȭ���� ����.
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

	// ȸ�� ���� ȭ���� ����.
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

	// �δ������ ȭ���� ����.
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

	// ���� �׸� ã�� ȭ���� ����.
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

	// ��ŷ ���� ȭ���� ����.
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

	// ��� ȭ���� �ݴ� ��.
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
		// Ŭ���� ȭ�� ��ǥ ��� �غ���.
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

	// ������ �޽��� ����
	public void sendMessage(EMessageCode eOperation, JSONObject obj) {
		if (client == null || client.IsConnected() == false)
			return;

		obj.put("code", eOperation.toString());
		client.SendMessage(obj.toString());
	}

	@Override
	public void run() {

		// Ŭ���̾�Ʈ�� ������ ���� ��Ų��.
		client = new ClientApplication(this);
		if (client.Connect(ConfigDatas.SERVER_IP, ConfigDatas.SERVER_PORT) == false) {
			int dialogButton = JOptionPane.CLOSED_OPTION;
			JOptionPane.showConfirmDialog(null, "���� ���ӿ� ���� �Ͽ����ϴ�.", "���� ����", dialogButton);

			// 1�� ���
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}

			// ���� ���н� ���α׷� ����
			System.exit(0);
		}

		// �ֱ������� ������ ������ ������ "alive-check"�� ������ ������ ���� ��Ų��. ���ڿ� ������ ��� ����. �ֱ������� �����͸� ������ ���� �߿�.
		while (client.IsConnected() == true) {

			try {
				Thread.sleep(60 * 1000); // 60�� ����
			} catch (InterruptedException e) {
				break;
			}

			if (client.IsConnected() == false)
				break;

			// �����ð� �̻� �����͸� �ְ� ���� ������ ������ ����Ƿ�, Ŭ���̾�Ʈ���� 60�ʸ��� �� ���� ������ �����͸� �ش�.
			client.SendMessage("alive-check");
		}
	}

	// ������ ���� ���� �����͸� ó�� �Ѵ�.
	@Override
	public void handleMessage(JSONObject obj) {
		EMessageCode eCode = getMessageCode(obj);
		System.out.println("ReceivedMessageCode " + eCode);

		String msg = (String) obj.get("message");
		boolean result = (boolean) obj.get("result");

		// �������� ó�� ���и� �˷ȴ�.
		if (result == false) {
			MessageBoxUtil.ShowMessageBox(msg, "����");
			return;
		}

		// ����, ó���� ���� ���� �� ���� �ȴ�.
		// �� �޼��忡�� ó���� �����ϰ� �Ͽ� �ڵ带 �л� �Ѵ�.
		switch (eCode) {

		case eIDCheckResponse:
			// �˸� �޽��� �ܿ��� Ư���� ó�� ���� �ʴ� ��.
			MessageBoxUtil.ShowMessageBox(msg, "�˸�");
			break;

		case eLoginResponse:
			this.OnLoginResponse(obj);
			break;

		case eJoinResponse:
			MessageBoxUtil.ShowMessageBox(msg, "�˸�");
			this.OnJoinResponse(obj);
			break;

		case eMembershipEditResponse:
			MessageBoxUtil.ShowMessageBox(msg, "�˸�");
			this.OnMembershipEditResponse(obj);
			break;

		case eUpdateGameScoreResponse:
			MessageBoxUtil.ShowMessageBox(msg, "�˸�");
			break;

		case eRankingResponse:
			MessageBoxUtil.ShowMessageBox(msg, "�˸�");
			this.ShowRank(obj);
			break;

		case eSelfTestPointUpdateResponse:
			MessageBoxUtil.ShowMessageBox(msg, "�˸�");
			break;

		default:
			break;
		}
	}

	// ���� ó�� ���
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
		this.ShowHomeMenu(); // �޴� ���� ȭ������ ����.
	}

	// �α��� ���
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
		this.ShowHomeMenu(); // �޴� ���� ȭ������ ����.
	}

	// ȸ�� ���� ���� ���
	private void OnMembershipEditResponse(JSONObject received) {
		String nickname = (String) received.get("nickname");
		String infomation = (String) received.get("infomation");

		this.userData.nickname = nickname;
		this.userData.infomation = infomation;

		System.out.println("ShowHomeMenu");
		this.ShowHomeMenu();
	}

	// ������ ���� ���� ���� �����Ͱ� � ����� ó�� �������, EMessageCode�� ã�Ƽ� ��ȯ
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

// ���� Ŭ����. MainWindow ��ü�� �����, ù ȭ���� �α��� ȭ���� �����ش�.
public class Main {

	public static void main(String[] args) {
		try {
			MainWindow window = new MainWindow();
			window.ShowLogin(); // �α��� ȭ���� ù ��������.
		} catch (Exception e_) {
			e_.printStackTrace();
			System.out.println("Quit program");
		}
	}
}