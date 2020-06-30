import java.awt.Font;
import javax.swing.*;

import org.json.simple.JSONObject;

import java.awt.event.*;

// 랭킹 결과를 보여주는 화면
public class Rank extends JPanel implements ActionListener {

	private JLabel labelTitle;
	private JLabel labelSubTitle;
	private JLabel labelRank;
	private JLabel labelNickname;
	private JLabel labelPlaytime;

	private JLabel[] labelRankNum; // 랭킹 번호
	private JLabel labelMyRank; // 예시 - 내 랭킹 (30위) 

	private JButton btnBack; // 이전으로
	private JTextField[] textNicks; // 랭킹에 표기할 닉네임
	private JTextField[] textPlaytimes; // 플레이 시간
	private MainWindow mainWindow;

	public Rank(MainWindow main, JSONObject obj) {
		mainWindow = main;

		setLayout(null); // 레이아웃 없이 처리한다.

		Font fontTitle = new Font("돋움", Font.PLAIN, 30);
		Font fontCommon = new Font("돋움", Font.PLAIN, 15);

		labelTitle = new JLabel("게임 제목");
		labelTitle.setFont(fontTitle);
		labelTitle.setBounds(250, 0, 500, 50);
		add(labelTitle);
		
		labelSubTitle = new JLabel("TOP 10");
		labelSubTitle.setFont(fontTitle);
		labelSubTitle.setBounds(300, 55, 500, 50);
		add(labelSubTitle);	

		btnBack = new JButton("이전으로");
		btnBack.setFont(fontCommon);
		btnBack.addActionListener(this);
		btnBack.setBounds(450, 100, 100, 35);
		add(btnBack);

		labelRank = new JLabel("순위");
		labelRank.setFont(fontCommon);
		labelRank.setBounds(150, 150, 70, 50);
		add(labelRank);

		labelNickname = new JLabel("닉네임");
		labelNickname.setFont(fontCommon);
		labelNickname.setBounds(250, 150, 70, 50);
		add(labelNickname);

		labelPlaytime = new JLabel("플레이 시간");
		labelPlaytime.setFont(fontCommon);
		labelPlaytime.setBounds(430, 150, 100, 50);
		add(labelPlaytime);

		labelMyRank = new JLabel("플레이 시간");
		labelMyRank.setFont(fontCommon);
		labelMyRank.setBounds(430, 150, 100, 50);
		add(labelMyRank);

		// 랭킹 번호
		labelRankNum = new JLabel[10];
		for (int i = 0; i < 10; ++i) {
			JLabel labelRank = new JLabel();
			labelRank = new JLabel(i + 1 + ""); // i + "" 는 숫자를 문자열로
			labelRank.setFont(fontCommon);
			labelRank.setBounds(160, 150 + (i + 1) * 35, 70, 35);
			add(labelRank);

			labelRankNum[i] = labelRank;
		}

		labelMyRank = new JLabel("내 순위 (40)");
		labelMyRank.setFont(fontCommon);
		labelMyRank.setBounds(150, 530, 100, 50);
		add(labelMyRank);

		// 닉네임
		textNicks = new JTextField[11];
		for (int i = 0; i < textNicks.length; ++i) {
			JTextField textField = new JTextField();
			textField = new JTextField();
			textField.setFont(fontCommon);
			textField.setBounds(250, 150 + (i + 1) * 35, 150, 35);
			textField.setEditable(false);
			add(textField);

			textNicks[i] = textField;
		}

		// 플레이 시간
		textPlaytimes = new JTextField[11];
		for (int i = 0; i < textPlaytimes.length; ++i) {
			JTextField textField = new JTextField();
			textField = new JTextField();
			textField.setFont(fontCommon);
			textField.setBounds(430, 150 + (i + 1) * 35, 100, 35);
			textField.setEditable(false);
			add(textField);

			textPlaytimes[i] = textField;
		}
		
		// 서버로 부터 받은 랭킹 데이터를 이용해 화면을 구성
		MakeRank(obj);
		
		setSize(ConfigDatas.WIN_WIDTH, ConfigDatas.WIN_HEIGHT);
		this.setVisible(true); // 모든 처리 종료 후 다시 패널을 보여준다.
	}

	private void MakeRank(JSONObject obj) {

		String rankType = (String) obj.get("ranktype");
		if (rankType.equals("gamescore_mole")) // 두더지게임
		{
			labelTitle.setText("  두더지 잡기");
		} else if (rankType.equals("gamescore_picture")) {
			labelTitle.setText("같은 그림 맞추기");
		} else {
			MessageBoxUtil.ShowMessageBox("서버로 부터 받은 데이터에 오류가 있음", "오류");
			return;
		}

		// 1~10위 랭킹 출력
		for (int i = 0; i < 10; ++i) {
			int rankNo = i + 1;

			try {
				String rankKey = "rank" + rankNo;
				String scoreMoleKey = "scoreMole" + rankNo;
				String scorePictureKey = "scorePicture" + rankNo;

				String nickname = (String) obj.get(rankKey);
				int score = 0;
				if (rankType.equals("gamescore_mole")) {
					score = ((Long) obj.get(scoreMoleKey)).intValue();
				} else {
					score = ((Long) obj.get(scorePictureKey)).intValue();
				}

				textNicks[i].setText(nickname);
				textPlaytimes[i].setText(score + "");
			} catch (Exception e) {
				break;
			}
		}
		
		// 나의 랭킹 출력
		String myNick = (String)obj.get("nickname");
		int myRank = ((Long)obj.get("myrank")).intValue();
		int myScore = 0;
		if (rankType.equals("gamescore_mole")) {
			myScore = ((Long) obj.get("scoreMole")).intValue();
		} else {
			myScore = ((Long) obj.get("scorePicture")).intValue();
		}
		
		labelMyRank.setText("내 순위(" + myRank + ")");
		textPlaytimes[10].setText(myScore + ""); // 배열 10번은 내 랭킹에 쓴다.
		textNicks[10].setText(myNick); // 배열 10번은 내 랭킹에 쓴다.
	}

	public void actionPerformed(ActionEvent e) {
		JButton actionBtn = (JButton) e.getSource();

		// 이전 화면으로
		if (actionBtn == btnBack) {
			this.mainWindow.ShowHomeMenu();
		}
	}
}