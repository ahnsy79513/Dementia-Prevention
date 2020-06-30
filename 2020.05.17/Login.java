import java.awt.Font;
import javax.swing.*;

import org.json.simple.JSONObject;

import java.awt.event.*;

// �α��� ó�� ȭ��
public class Login extends JPanel implements ActionListener {

	private JButton btnJoin;
	private JButton btnLogin;
	private JLabel labelTitle;
	private JLabel labelID;
	private JLabel labelPW;
	private JTextField textID;
	private JPasswordField textPW; // �н������ ���ڿ��� ������, '*'�� �����ְ� �Ѵ�.
	private MainWindow mainWindow;

	public Login(MainWindow main) {
		mainWindow = main;
		
		// this.setBackground(Color.white);
		setLayout(null); // ���̾ƿ� ���� ó���Ѵ�.

		btnJoin = new JButton("ȸ������");
		btnLogin = new JButton("�α���");

		Font fontTitle = new Font("����", Font.PLAIN, 30);
		Font fontCommon = new Font("����", Font.PLAIN, 25);

		labelTitle = new JLabel("ġ�� ���� ���α׷�");
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
		this.setVisible(true); // ��� ó�� ���� �� �ٽ� �г��� �����ش�.

		btnJoin.addActionListener(this);
		btnLogin.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		JButton actionBtn = (JButton) e.getSource();

		// �α���
		if (btnLogin == actionBtn) {
			loginRequestToServer();
			return;
		}

		// ȸ������
		if (btnJoin == actionBtn) {
			mainWindow.ShowJoin();
			return;
		}
	}

	// ������ �α��� ��û
	private void loginRequestToServer() {

		// JPasswordField�� text�� �ٷ� ������ �� ���� �Ʒ� ó���� ���ľ� �Ѵ�.
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

		mainWindow.sendMessage(EMessageCode.eLoginRequest, json); // ������ ����
	}
}