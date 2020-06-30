import java.awt.Font;
import javax.swing.*;

import org.json.simple.JSONObject;

import java.awt.event.*;

// 로그인 처리 화면
public class Login extends JPanel implements ActionListener {

	private JButton btnJoin;
	private JButton btnLogin;
	private JLabel labelTitle;
	private JLabel labelID;
	private JLabel labelPW;
	private JTextField textID;
	private JPasswordField textPW; // 패스워드는 문자열을 가리고, '*'로 보여주게 한다.
	private MainWindow mainWindow;

	public Login(MainWindow main) {
		mainWindow = main;
		
		// this.setBackground(Color.white);
		setLayout(null); // 레이아웃 없이 처리한다.

		btnJoin = new JButton("회원가입");
		btnLogin = new JButton("로그인");

		Font fontTitle = new Font("돋움", Font.PLAIN, 30);
		Font fontCommon = new Font("돋움", Font.PLAIN, 25);

		labelTitle = new JLabel("치매 예방 프로그램");
		labelTitle.setFont(fontTitle);

		labelID = new JLabel("ID");
		labelID.setFont(fontCommon);

		labelPW = new JLabel("PW");
		labelPW.setFont(fontCommon);

		labelTitle.setBounds(220, 55, 300, 50);

		textID = new JTextField();
		textID.setFont(fontCommon);

		textPW = new JPasswordField();
		textPW.setFont(fontCommon);

		textPW.setEchoChar('*');

		labelID.setBounds(150, 150, 50, 50);
		textID.setBounds(200, 150, 250, 50);
		btnJoin.setBounds(460, 150, 90, 50);

		labelPW.setBounds(150, 200, 50, 50);
		textPW.setBounds(200, 200, 250, 50);
		btnLogin.setBounds(460, 200, 90, 50);

		add(labelTitle);
		add(labelID);
		add(labelPW);
		add(labelTitle);
		add(btnJoin);
		add(btnLogin);
		add(textID);
		add(textPW);

		setSize(ConfigDatas.WIN_WIDTH, ConfigDatas.WIN_HEIGHT);
		this.setVisible(true); // 모든 처리 종료 후 다시 패널을 보여준다.

		btnJoin.addActionListener(this);
		btnLogin.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		JButton actionBtn = (JButton) e.getSource();

		// 로그인
		if (btnLogin == actionBtn) {
			loginRequestToServer();
			return;
		}

		// 회원가입
		if (btnJoin == actionBtn) {
			mainWindow.ShowJoin();
			return;
		}
	}

	// 서버로 로그인 요청
	private void loginRequestToServer() {

		// JPasswordField는 text를 바로 가져올 수 없고 아래 처리를 거쳐야 한다.
		char[] passwd = this.textPW.getPassword();
		String pw = "";
		if (passwd != null) {
			for (int i = 0; i < passwd.length; ++i) {
				pw += passwd[i];
			}
		}

		JSONObject json = new JSONObject();
		json.put("id", this.textID.getText());
		json.put("pw", pw);

		mainWindow.sendMessage(EMessageCode.eLoginRequest, json); // 서버로 전송
	}
}