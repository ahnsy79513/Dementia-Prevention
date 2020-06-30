import java.awt.Font;
import javax.swing.*;

import java.awt.event.*;

// �޴� ���� ȭ��
public class HomeMenu extends JPanel implements ActionListener {

	private JLabel labelTitle;

	private JButton btnSelfTest;
	private JButton btnGamePicture;
	private JButton btnGameMole;
	private JButton btnRank;
	private JButton btnMembershipEdit;
	private MainWindow mainWindow;
	
	public HomeMenu(MainWindow main) {
		mainWindow = main;
		
		setLayout(null); // ���̾ƿ� ���� ó���Ѵ�.
		
		Font fontTitle = new Font("����", Font.PLAIN, 30);
		Font fontCommon = new Font("����", Font.PLAIN, 25);
		Font fontText = new Font("����", Font.PLAIN, 20);
		
		labelTitle = new JLabel("ġ�� ���� ���α׷�");
		labelTitle.setFont(fontTitle);
		labelTitle.setBounds(220, 55, 300, 50);
		add(labelTitle);
		
		btnSelfTest = new JButton("�ڰ� ���� �׽�Ʈ");
		btnSelfTest.setFont(fontText);
		btnSelfTest.addActionListener(this);
		btnSelfTest.setBounds(240, 150, 200, 50);
		add(btnSelfTest);
		
		btnGamePicture = new JButton("���� �׸� ã��");
		btnGamePicture.setFont(fontText);
		btnGamePicture.addActionListener(this);
		btnGamePicture.setBounds(240, 200, 200, 50);
		add(btnGamePicture);
	
		btnGameMole = new JButton("�δ��� ����");
		btnGameMole.setFont(fontText);
		btnGameMole.addActionListener(this);
		btnGameMole.setBounds(240, 250, 200, 50);
		add(btnGameMole);
		
		btnRank = new JButton("��ŷ ���� ����");
		btnRank.setFont(fontText);
		btnRank.addActionListener(this);
		btnRank.setBounds(240, 300, 200, 50);
		add(btnRank);
		
		btnMembershipEdit = new JButton("ȸ�� ���� ����");
		btnMembershipEdit.setFont(fontText);
		btnMembershipEdit.addActionListener(this);
		btnMembershipEdit.setBounds(240, 350, 200, 50);
		add(btnMembershipEdit);

		setSize(ConfigDatas.WIN_WIDTH, ConfigDatas.WIN_HEIGHT);
		this.setVisible(true); // ��� ó�� ���� �� �ٽ� �г��� �����ش�.
	}

	public void actionPerformed(ActionEvent e) {
		JButton actionBtn = (JButton) e.getSource();

		if (actionBtn == btnSelfTest) {
			mainWindow.ShowSelfTest(); // �ڰ�����
		} else if (actionBtn == btnGamePicture) {
			mainWindow.StartGamePicture(); // ���� �׸� ���߱�
		} else if (actionBtn == btnGameMole) {
			mainWindow.StartGameMole(); // �δ��� ���
		} else if (actionBtn == btnRank) {
			mainWindow.ShowSelectRank(); // ��ŷ ���� ȭ��
		} else if (actionBtn == btnMembershipEdit) {
			mainWindow.ShowMembershipEdit(); // ȸ������ ����
		}
	}
}