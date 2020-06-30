import java.awt.Font;
import javax.swing.*;
import org.json.simple.JSONObject;
import java.awt.event.*;

// 회원 정보 수정 - 닉네임 및 정보
public class MembershipEdit extends JPanel implements ActionListener {

	private JLabel labelTitle;
	private JLabel labelID;
	private JLabel labelPW;
	private JLabel labelNickname;
	private JLabel labelInfomation;

	private JButton btnSubmit;
	private JButton btnCancel;

	private JTextField textID;
	private JPasswordField textPW;
	private JTextField textNickName;
	private JTextField textInfomation;
	private MainWindow mainWindow;

	public MembershipEdit(MainWindow main) {
		mainWindow = main;

		setLayout(null); // 레이아웃 없이 처리한다.

		Font fontTitle = new Font("돋움", Font.PLAIN, 30);
		Font fontCommon = new Font("돋움", Font.PLAIN, 20);

		labelTitle = new JLabel("회원 정보 수정");
		labelTitle.setFont(fontTitle);
		labelTitle.setBounds(220, 55, 300, 50);
		add(labelTitle);

		labelID = new JLabel("ID");
		labelID.setFont(fontCommon);
		labelID.setBounds(150, 140, 70, 50);
		add(labelID);

		labelPW = new JLabel("PW");
		labelPW.setFont(fontCommon);
		labelPW.setBounds(150, 190, 70, 50);
		add(labelPW);

		labelNickname = new JLabel("닉네임");
		labelNickname.setFont(fontCommon);
		labelNickname.setBounds(150, 270, 70, 50);
		add(labelNickname);

		labelInfomation = new JLabel("치매 진단 정보");
		labelInfomation.setFont(fontCommon);
		labelInfomation.setBounds(80, 320, 200, 50);
		add(labelInfomation);

		btnCancel = new JButton("취소");
		btnCancel.setFont(fontCommon);
		btnCancel.addActionListener(this);
		btnCancel.setBounds(310, 420, 80, 35);
		add(btnCancel);

		btnSubmit = new JButton("완료");
		btnSubmit.setFont(fontCommon);
		btnSubmit.addActionListener(this);
		btnSubmit.setBounds(210, 420, 80, 35);
		add(btnSubmit);

		textID = new JTextField();
		textID.setFont(fontCommon);
		textID.setBounds(220, 140, 210, 35);
		textID.setEditable(false);
		add(textID);

		textPW = new JPasswordField();
		textPW.setFont(fontCommon);
		textPW.setEchoChar('*');
		textPW.setBounds(220, 190, 210, 35);
		textPW.setEditable(false);
		add(textPW);

		textNickName = new JTextField();
		textNickName.setFont(fontCommon);
		textNickName.setBounds(220, 280, 350, 35);
		add(textNickName);

		textInfomation = new JTextField();
		textInfomation.setFont(fontCommon);
		textInfomation.setBounds(220, 330, 350, 35);
		add(textInfomation);

		// 아이디, 패스워드 및 이미 설정 했다면 닉네임 및 정보를 텍스트로 출력 해준다.
		InitTextFields();

		setSize(ConfigDatas.WIN_WIDTH, ConfigDatas.WIN_HEIGHT);
		this.setVisible(true); // 모든 처리 종료 후 다시 패널을 보여준다.
	}

	private void InitTextFields() {
		Account userData = mainWindow.GetUserData();
		if (userData == null)
			return;

		textNickName.setText(userData.nickname);
		textInfomation.setText(userData.infomation);
		textID.setText(userData.loginid);
		textPW.setText(userData.passwrd);
	}

	public void actionPerformed(ActionEvent e) {
		JButton actionBtn = (JButton) e.getSource();

		// 서버로 전송
		if (actionBtn == btnSubmit) {
						
			// 닉네임 및 정보 변경
			JSONObject json = new JSONObject();

			json.put("nickname", textNickName.getText());
			json.put("infomation", textInfomation.getText());

			mainWindow.sendMessage(EMessageCode.eMembershipEditRequest, json);
		} else if (actionBtn == btnCancel) {
			// 다시 메뉴로 돌아간다.
			mainWindow.ShowHomeMenu();
		}
	}
}