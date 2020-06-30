import java.awt.Color;
import java.awt.Font;
import javax.swing.*;
import javax.swing.border.Border;

import org.json.simple.JSONObject;

import java.awt.event.*;

// 회원 가입 처리 화면
public class Join extends JPanel implements ActionListener {

	private JLabel labelTitle;
	private JLabel labelName;
	private JLabel labelID;
	private JLabel labelPW;
	private JLabel labelAddress;
	private JLabel labelAge;
	private JLabel labelJob;
	private JLabel labelGender;

	private JButton btnSubmit;
	private JButton btnIDCheck;

	private JTextField textName;
	private JTextField textID;
	private JPasswordField textPW;
	private JTextArea textAddress;
	private JTextField textAge;
	private JTextField textJob;
	private JRadioButton[] radioGender;
	private MainWindow mainWindow;

	public Join(MainWindow main) {
		mainWindow = main;

		setLayout(null); // 레이아웃 없이 처리한다.

		Font fontTitle = new Font("돋움", Font.PLAIN, 30);
		Font fontCommon = new Font("돋움", Font.PLAIN, 25);
		Font fontText = new Font("돋움", Font.PLAIN, 20);

		labelTitle = new JLabel("치매 예방 프로그램");
		labelTitle.setFont(fontTitle);
		labelTitle.setBounds(220, 55, 300, 50);
		add(labelTitle);

		labelName = new JLabel("이름");
		labelName.setFont(fontCommon);
		labelName.setBounds(150, 100, 70, 50);
		add(labelName);

		labelID = new JLabel("ID");
		labelID.setFont(fontCommon);
		labelID.setBounds(150, 150, 70, 50);
		add(labelID);

		labelPW = new JLabel("PW");
		labelPW.setFont(fontCommon);
		labelPW.setBounds(150, 200, 70, 50);
		add(labelPW);

		labelAddress = new JLabel("주소");
		labelAddress.setFont(fontCommon);
		labelAddress.setBounds(150, 250, 70, 50);
		add(labelAddress);

		labelAge = new JLabel("나이");
		labelAge.setFont(fontCommon);
		labelAge.setBounds(150, 400, 70, 50);
		add(labelAge);

		labelJob = new JLabel("직업");
		labelJob.setFont(fontCommon);
		labelJob.setBounds(150, 450, 70, 50);
		add(labelJob);

		labelGender = new JLabel("성별");
		labelGender.setFont(fontCommon);
		labelGender.setBounds(400, 400, 70, 50);
		add(labelGender);

		btnSubmit = new JButton("완료");
		btnSubmit.setFont(fontText);
		btnSubmit.addActionListener(this);
		btnSubmit.setBounds(310, 555, 100, 35);
		add(btnSubmit);

		btnIDCheck = new JButton("중복확인");
		btnIDCheck.setFont(fontText);
		btnIDCheck.addActionListener(this);
		btnIDCheck.setBounds(400, 155, 120, 35);
		add(btnIDCheck);

		textName = new JTextField();
		textName.setFont(fontText);
		textName.setBounds(215, 105, 170, 35);
		add(textName);

		textID = new JTextField();
		textID.setFont(fontText);
		textID.setBounds(215, 155, 170, 35);
		add(textID);

		textPW = new JPasswordField();
		textPW.setFont(fontText);
		textPW.setEchoChar('*');
		textPW.setBounds(215, 205, 170, 35);
		add(textPW);

		textAddress = new JTextArea();
		textAddress.setFont(fontText);
		textAddress.setBounds(215, 255, 350, 105);
		Border border = BorderFactory.createLineBorder(Color.GRAY);
		textAddress.setBorder(border);
		add(textAddress);

		textAge = new JTextField();
		textAge.setFont(fontText);
		textAge.setBounds(215, 410, 100, 35);
		add(textAge);

		textJob = new JTextField();
		textJob.setFont(fontText);
		textJob.setBounds(215, 460, 100, 35);
		add(textJob);

		radioGender = new JRadioButton[2];
		ButtonGroup group = new ButtonGroup();

		for (int i = 0; i < radioGender.length; i++) {
			radioGender[i] = new JRadioButton(i == 0 ? "남" : "여");
			radioGender[i].setFont(fontCommon);
			group.add(radioGender[i]);
			add(radioGender[i]);

			if (i == 0) {
				radioGender[i].setBounds(460, 400, 70, 50);
			} else {
				radioGender[i].setBounds(460, 450, 70, 50);
			}
		}

		setSize(ConfigDatas.WIN_WIDTH, ConfigDatas.WIN_HEIGHT);
		this.setVisible(true); // 모든 처리 종료 후 다시 패널을 보여준다.
	}

	private boolean IsFemale() {
		if (radioGender[0].isSelected() == true) // 남자
			return false; // 여성이 아니다

		return true; // 남성이다
	}

	public void actionPerformed(ActionEvent e) {
		JButton actionBtn = (JButton) e.getSource();

		if (actionBtn == btnSubmit) {
			
			// 패스워드 JPasswordField 는 text를 바로 가져올 수 없고 아래 처리를 거쳐야 한다.
			char[] passwd = this.textPW.getPassword();
			String pw = "";
			if (passwd != null) {
				for (int i = 0; i < passwd.length; ++i) {
					pw += passwd[i];
				}
			}
						
			// 회원가입
			JSONObject json = new JSONObject();
			json.put("name", textName.getText());
			json.put("loginid", textID.getText());
			json.put("passwrd", pw);
			json.put("address", textAddress.getText());
			json.put("age", Integer.parseInt(textAge.getText()));
			json.put("job", textJob.getText());
			json.put("isFemale", IsFemale());

			mainWindow.sendMessage(EMessageCode.eJoinRequest, json); // 서버로 전송

		} else if (actionBtn == btnIDCheck) {
			// 아이디 중복 체크
			JSONObject json = new JSONObject();
			json.put("id", textID.getText());
			mainWindow.sendMessage(EMessageCode.eIDCheckRequest, json); // 서버로 전송

		}
	}
}