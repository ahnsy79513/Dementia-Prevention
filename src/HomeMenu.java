import java.awt.Font;
import javax.swing.*;

import java.awt.event.*;

// 메뉴 선택 화면
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
		
		setLayout(null); // 레이아웃 없이 처리한다.
		
		Font fontTitle = new Font("돋움", Font.PLAIN, 30);
		Font fontCommon = new Font("돋움", Font.PLAIN, 25);
		Font fontText = new Font("돋움", Font.PLAIN, 20);
		
		labelTitle = new JLabel("치매 예방 프로그램");
		labelTitle.setFont(fontTitle);
		labelTitle.setBounds(220, 55, 300, 50);
		add(labelTitle);
		
		btnSelfTest = new JButton("자가 진단 테스트");
		btnSelfTest.setFont(fontText);
		btnSelfTest.addActionListener(this);
		btnSelfTest.setBounds(240, 150, 200, 50);
		add(btnSelfTest);
		
		btnGamePicture = new JButton("같은 그림 찾기");
		btnGamePicture.setFont(fontText);
		btnGamePicture.addActionListener(this);
		btnGamePicture.setBounds(240, 200, 200, 50);
		add(btnGamePicture);
	
		btnGameMole = new JButton("두더지 게임");
		btnGameMole.setFont(fontText);
		btnGameMole.addActionListener(this);
		btnGameMole.setBounds(240, 250, 200, 50);
		add(btnGameMole);
		
		btnRank = new JButton("랭킹 점수 보기");
		btnRank.setFont(fontText);
		btnRank.addActionListener(this);
		btnRank.setBounds(240, 300, 200, 50);
		add(btnRank);
		
		btnMembershipEdit = new JButton("회원 정보 수정");
		btnMembershipEdit.setFont(fontText);
		btnMembershipEdit.addActionListener(this);
		btnMembershipEdit.setBounds(240, 350, 200, 50);
		add(btnMembershipEdit);

		setSize(ConfigDatas.WIN_WIDTH, ConfigDatas.WIN_HEIGHT);
		this.setVisible(true); // 모든 처리 종료 후 다시 패널을 보여준다.
	}

	public void actionPerformed(ActionEvent e) {
		JButton actionBtn = (JButton) e.getSource();

		if (actionBtn == btnSelfTest) {
			mainWindow.ShowSelfTest(); // 자가진단
		} else if (actionBtn == btnGamePicture) {
			mainWindow.StartGamePicture(); // 같은 그림 맞추기
		} else if (actionBtn == btnGameMole) {
			mainWindow.StartGameMole(); // 두더지 잡기
		} else if (actionBtn == btnRank) {
			mainWindow.ShowSelectRank(); // 랭킹 선택 화면
		} else if (actionBtn == btnMembershipEdit) {
			mainWindow.ShowMembershipEdit(); // 회원정보 수정
		}
	}
}