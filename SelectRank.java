import java.awt.Font;
import javax.swing.*;
import org.json.simple.JSONObject;
import java.awt.event.*;

// ��ŷ�� �����ϴ� ȭ��
public class SelectRank extends JPanel implements ActionListener {

	private JLabel labelTitle;

	private JButton btnShowRankPicture;
	private JButton btnShowRankMole;
	private MainWindow mainWindow;

	public SelectRank(MainWindow main) {
		mainWindow = main;

		setLayout(null); // ���̾ƿ� ���� ó���Ѵ�.

		Font fontTitle = new Font("����", Font.PLAIN, 30);
		Font fontCommon = new Font("����", Font.PLAIN, 25);

		labelTitle = new JLabel("��ŷ ���� ����");
		labelTitle.setFont(fontTitle);
		labelTitle.setBounds(240, 55, 300, 50);
		add(labelTitle);

		btnShowRankPicture = new JButton("���� �׸� ã��");
		btnShowRankPicture.setFont(fontCommon);
		btnShowRankPicture.addActionListener(this);
		btnShowRankPicture.setBounds(200, 135, 300, 50);
		add(btnShowRankPicture);

		btnShowRankMole = new JButton("�δ��� ����");
		btnShowRankMole.setFont(fontCommon);
		btnShowRankMole.addActionListener(this);
		btnShowRankMole.setBounds(200, 205, 300, 50);
		add(btnShowRankMole);

		setSize(ConfigDatas.WIN_WIDTH, ConfigDatas.WIN_HEIGHT);
		this.setVisible(true); // ��� ó�� ���� �� �ٽ� �г��� �����ش�.
	}

	public void actionPerformed(ActionEvent e) {
		JButton actionBtn = (JButton) e.getSource();

		// ��ŷ ��û
		if (actionBtn == btnShowRankPicture) { // ���� �׸� ���߱�

			JSONObject json = new JSONObject();
			json.put("ranktype", "gamescore_picture");
			mainWindow.sendMessage(EMessageCode.eRankingRequest, json); // ������ ����

		} else if (actionBtn == btnShowRankMole) { // �δ��� ����

			JSONObject json = new JSONObject();
			json.put("ranktype", "gamescore_mole");
			mainWindow.sendMessage(EMessageCode.eRankingRequest, json); // ������ ����

		}
	}
}