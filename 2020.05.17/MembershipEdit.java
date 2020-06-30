import java.awt.Font;
import javax.swing.*;
import org.json.simple.JSONObject;
import java.awt.event.*;

// ȸ�� ���� ���� - �г��� �� ����
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

		setLayout(null); // ���̾ƿ� ���� ó���Ѵ�.

		Font fontTitle = new Font("����", Font.PLAIN, 30);
		Font fontCommon = new Font("����", Font.PLAIN, 20);

		labelTitle = new JLabel("ȸ�� ���� ����");
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

		labelNickname = new JLabel("�г���");
		labelNickname.setFont(fontCommon);
		labelNickname.setBounds(150, 270, 70, 50);
		add(labelNickname);

		labelInfomation = new JLabel("ġ�� ���� ����");
		labelInfomation.setFont(fontCommon);
		labelInfomation.setBounds(80, 320, 200, 50);
		add(labelInfomation);

		btnCancel = new JButton("���");
		btnCancel.setFont(fontCommon);
		btnCancel.addActionListener(this);
		btnCancel.setBounds(310, 420, 80, 35);
		add(btnCancel);

		btnSubmit = new JButton("�Ϸ�");
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

		// ���̵�, �н����� �� �̹� ���� �ߴٸ� �г��� �� ������ �ؽ�Ʈ�� ��� ���ش�.
		InitTextFields();

		setSize(ConfigDatas.WIN_WIDTH, ConfigDatas.WIN_HEIGHT);
		this.setVisible(true); // ��� ó�� ���� �� �ٽ� �г��� �����ش�.
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

		// ������ ����
		if (actionBtn == btnSubmit) {
						
			// �г��� �� ���� ����
			JSONObject json = new JSONObject();

			json.put("nickname", textNickName.getText());
			json.put("infomation", textInfomation.getText());

			mainWindow.sendMessage(EMessageCode.eMembershipEditRequest, json);
		} else if (actionBtn == btnCancel) {
			// �ٽ� �޴��� ���ư���.
			mainWindow.ShowHomeMenu();
		}
	}
}