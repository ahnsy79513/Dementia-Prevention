import java.awt.Font;
import javax.swing.*;

import org.json.simple.JSONObject;

import java.awt.event.*;

// ��ŷ ����� �����ִ� ȭ��
public class Rank extends JPanel implements ActionListener {

	private JLabel labelTitle;
	private JLabel labelSubTitle;
	private JLabel labelRank;
	private JLabel labelNickname;
	private JLabel labelPlaytime;

	private JLabel[] labelRankNum; // ��ŷ ��ȣ
	private JLabel labelMyRank; // ���� - �� ��ŷ (30��) 

	private JButton btnBack; // ��������
	private JTextField[] textNicks; // ��ŷ�� ǥ���� �г���
	private JTextField[] textPlaytimes; // �÷��� �ð�
	private MainWindow mainWindow;

	public Rank(MainWindow main, JSONObject obj) {
		mainWindow = main;

		setLayout(null); // ���̾ƿ� ���� ó���Ѵ�.

		Font fontTitle = new Font("����", Font.PLAIN, 30);
		Font fontCommon = new Font("����", Font.PLAIN, 15);

		labelTitle = new JLabel("���� ����");
		labelTitle.setFont(fontTitle);
		labelTitle.setBounds(250, 0, 500, 50);
		add(labelTitle);
		
		labelSubTitle = new JLabel("TOP 10");
		labelSubTitle.setFont(fontTitle);
		labelSubTitle.setBounds(300, 55, 500, 50);
		add(labelSubTitle);	

		btnBack = new JButton("��������");
		btnBack.setFont(fontCommon);
		btnBack.addActionListener(this);
		btnBack.setBounds(450, 100, 100, 35);
		add(btnBack);

		labelRank = new JLabel("����");
		labelRank.setFont(fontCommon);
		labelRank.setBounds(150, 150, 70, 50);
		add(labelRank);

		labelNickname = new JLabel("�г���");
		labelNickname.setFont(fontCommon);
		labelNickname.setBounds(250, 150, 70, 50);
		add(labelNickname);

		labelPlaytime = new JLabel("�÷��� �ð�");
		labelPlaytime.setFont(fontCommon);
		labelPlaytime.setBounds(430, 150, 100, 50);
		add(labelPlaytime);

		labelMyRank = new JLabel("�÷��� �ð�");
		labelMyRank.setFont(fontCommon);
		labelMyRank.setBounds(430, 150, 100, 50);
		add(labelMyRank);

		// ��ŷ ��ȣ
		labelRankNum = new JLabel[10];
		for (int i = 0; i < 10; ++i) {
			JLabel labelRank = new JLabel();
			labelRank = new JLabel(i + 1 + ""); // i + "" �� ���ڸ� ���ڿ���
			labelRank.setFont(fontCommon);
			labelRank.setBounds(160, 150 + (i + 1) * 35, 70, 35);
			add(labelRank);

			labelRankNum[i] = labelRank;
		}

		labelMyRank = new JLabel("�� ���� (40)");
		labelMyRank.setFont(fontCommon);
		labelMyRank.setBounds(150, 530, 100, 50);
		add(labelMyRank);

		// �г���
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

		// �÷��� �ð�
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
		
		// ������ ���� ���� ��ŷ �����͸� �̿��� ȭ���� ����
		MakeRank(obj);
		
		setSize(ConfigDatas.WIN_WIDTH, ConfigDatas.WIN_HEIGHT);
		this.setVisible(true); // ��� ó�� ���� �� �ٽ� �г��� �����ش�.
	}

	private void MakeRank(JSONObject obj) {

		String rankType = (String) obj.get("ranktype");
		if (rankType.equals("gamescore_mole")) // �δ�������
		{
			labelTitle.setText("  �δ��� ���");
		} else if (rankType.equals("gamescore_picture")) {
			labelTitle.setText("���� �׸� ���߱�");
		} else {
			MessageBoxUtil.ShowMessageBox("������ ���� ���� �����Ϳ� ������ ����", "����");
			return;
		}

		// 1~10�� ��ŷ ���
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
		
		// ���� ��ŷ ���
		String myNick = (String)obj.get("nickname");
		int myRank = ((Long)obj.get("myrank")).intValue();
		int myScore = 0;
		if (rankType.equals("gamescore_mole")) {
			myScore = ((Long) obj.get("scoreMole")).intValue();
		} else {
			myScore = ((Long) obj.get("scorePicture")).intValue();
		}
		
		labelMyRank.setText("�� ����(" + myRank + ")");
		textPlaytimes[10].setText(myScore + ""); // �迭 10���� �� ��ŷ�� ����.
		textNicks[10].setText(myNick); // �迭 10���� �� ��ŷ�� ����.
	}

	public void actionPerformed(ActionEvent e) {
		JButton actionBtn = (JButton) e.getSource();

		// ���� ȭ������
		if (actionBtn == btnBack) {
			this.mainWindow.ShowHomeMenu();
		}
	}
}