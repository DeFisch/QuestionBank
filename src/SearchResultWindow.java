import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class SearchResultWindow extends JFrame{
	JFrame thisWindow;
	JScrollPane mainPanel;
	JPanel questionPanel;
	Question selectedQ;
	SearchResultWindow(ArrayList<Question> results){
		super();
		thisWindow = this;
		this.setSize(500,500);
		this.setLocationRelativeTo(null);
		if(results.size() == 0) {
			this.add(new JLabel("No result found, please verify your search keywords"));
		}
		JPanel inScrollPane = new JPanel();
		JScrollPane displayList = new JScrollPane(inScrollPane);
		mainPanel = displayList;
		this.add(displayList);
		inScrollPane.setLayout(new BoxLayout(inScrollPane,BoxLayout.Y_AXIS));
		ArrayList<JButton> buttonList = new ArrayList<JButton>();
		ActionListener buttonToggled = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String selectedQuestion = arg0.getActionCommand();
				for(Question question:results) {
					String qText = (String)question.getQuestion();
					if(qText.equals(selectedQuestion))
						selectedQ = question;
				}
				QuestionPanel qPane = new QuestionPanel(selectedQ);
				mainPanel.setVisible(false);
				thisWindow.add(qPane);
				thisWindow.revalidate();
			}
		};
		for(Question q:results) {
			JButton butt = new JButton((String)q.getQuestion());
			butt.setSize(500,40);
			butt.addActionListener(buttonToggled);
			buttonList.add(butt);
			inScrollPane.add(butt);
		}
		inScrollPane.setSize(getPreferredSize());
		displayList.setSize(getPreferredSize());
		this.setVisible(true);
	}
	
	public class QuestionPanel extends JPanel{
		QuestionPanel(Question q){
			super();
			questionPanel = this;
			JTextArea displayProblem;
			String question = (String)q.getQuestion();
			displayProblem = new JTextArea(question);
			this.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
			this.add(displayProblem);
			JTextArea displayAnswer;
			String answer = (String)q.getAnswer();
			displayAnswer = new JTextArea(answer);
			displayAnswer.setBackground(new Color(156, 255, 165));
			JButton editTags = new JButton("edit tags");
			JButton showAnswer = new JButton("show answer");
			JButton back = new JButton("back");
			JButton saveChanges = new JButton("save changes");
			saveChanges.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int response = JOptionPane.showConfirmDialog(thisWindow, "Save all the changes to your question/answer?");
					if(response == JOptionPane.YES_OPTION) {
						q.setQuestion(displayProblem.getText());
						q.setAnswer(displayAnswer.getText());
						JOptionPane.showMessageDialog(thisWindow, "Question saved!");
					}
				}
			});
			showAnswer.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					showAnswer.setVisible(false);
					back.setVisible(false);
					displayAnswer.setVisible(true);
					displayAnswer.setSize(getPreferredSize());
					questionPanel.add(displayAnswer);
					back.setVisible(true);
					questionPanel.add(editTags);
					questionPanel.add(saveChanges);
					questionPanel.add(back);
					questionPanel.revalidate();
					thisWindow.revalidate();
				}
			});
			back.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					questionPanel.setVisible(false);
					mainPanel.setVisible(true);
				}
			});
			editTags.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					JFrame tagWindow  = new JFrame("Edit Tags");
					tagWindow.setLayout(new BoxLayout(tagWindow.getContentPane(),BoxLayout.Y_AXIS));
					tagWindow.setLocationRelativeTo(null);
					String tagNames = "";
					for(Tag t:q.getTags()) {
						tagNames += "[" + t.getName() + "]\n";
					}
					JTextArea jta = new JTextArea(tagNames);
					tagWindow.add(jta);
					JButton confirm = new JButton("save changes");
					tagWindow.add(confirm);
					tagWindow.setSize(getPreferredSize());
					confirm.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							String textInput = jta.getText();
							//check whether all previous tags are present
							ArrayList<Tag> tags = q.getTags();
							for(int i = 0; i < tags.size(); i++) {
								Tag t = tags.get(i);
								if(!textInput.toLowerCase().equals(t.getName().toLowerCase())) {
									MainWindow.MainPanel.updateTag(t.getName(), q, false);//tag removed
									i--;
								}
							}
							//check whether new tags added
							while(textInput.contains("[") && textInput.contains("]")) {
								int front = textInput.indexOf('[');
								int back = textInput.indexOf(']');
								String tagName = textInput.substring(front+1,back);
								if(back != textInput.length()-1)
									textInput = textInput.substring(back+1);
								else
									textInput = "";
								boolean newTag = true;
								for(Tag t:q.getTags()) {
									if(t.getName().toLowerCase().equals(tagName.toLowerCase()))
										newTag = false;
								}
								if(newTag) {
									MainWindow.MainPanel.updateTag(tagName, q, true);
								}
							}
						JOptionPane.showMessageDialog(null, "Change Saved!");
						}
					});
					tagWindow.setVisible(true);
				}
			});
			displayAnswer.setLineWrap(true);
			displayProblem.setLineWrap(true);
			this.add(editTags);
			this.add(showAnswer);
			this.add(back);
		}
	}
}
