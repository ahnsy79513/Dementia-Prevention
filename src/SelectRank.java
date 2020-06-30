import java.awt.Font;
import javax.swing.*;
import org.json.simple.JSONObject;
import java.awt.event.*;

// 랭킹을 선택하는 화면
public class SelectRank extends JPanel implements ActionListener {

	private JLabel labelTitle;

	private JButton btnShowRankPicture;
	private JButton btnShowRankMole;
	private MainWindow mainWindow;

	public SelectRank(MainWindow main) {
		mainWindow = main;

		setLayout(null); // 레이아웃 없이 처리한다.

		Font fontTitle = new Font("돋움", Font.PLAIN, 30);
		Font fontCommon = new Font("돋움", Font.PLAIN, 25);

		labelTitle = new JLabel("랭킹 점수 보기");
		labelTitle.setFont(fontTitle);
		labelTitle.setBounds(240, 55, 300, 50);
		add(labelTitle);

		btnShowRankPicture = new JButton("같은 그림 찾기");
		btnShowRankPicture.setFont(fontCommon);
		btnShowRankPicture.addActionListener(this);
		btnShowRankPicture.setBounds(200, 135, 300, 50);
		add(btnShowRankPicture);

		btnShowRankMole = new JButton("두더지 게임");
		btnShowRankMole.setFont(fontCommon);
		btnShowRankMole.addActionListener(this);
		btnShowRankMole.setBounds(200, 205, 300, 50);
		add(btnShowRankMole);

		setSize(ConfigDatas.WIN_WIDTH, ConfigDatas.WIN_HEIGHT);
		this.setVisible(true); // 모든 처리 종료 후 다시 패널을 보여준다.
	}

	public void actionPerformed(ActionEvent e) {
		JButton actionBtn = (JButton) e.getSource();

		// 랭킹 요청
		if (actionBtn == btnShowRankPicture) { // 같은 그림 맞추기

			JSONObject json = new JSONObject();
			json.put("ranktype", "gamescore_picture");
			mainWindow.sendMessage(EMessageCode.eRankingRequest, json); // 서버로 전송

		} else if (actionBtn == btnShowRankMole) { // 두더지 게임

			JSONObject json = new JSONObject();
			json.put("ranktype", "gamescore_mole");
			mainWindow.sendMessage(EMessageCode.eRankingRequest, json); // 서버로 전송

		}
	}
}