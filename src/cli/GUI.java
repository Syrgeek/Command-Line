package cli;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextPane;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;

import java.awt.Color;
import java.awt.BorderLayout;

import javax.swing.JScrollBar;

import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Stack;

public class GUI {

	private JFrame frmCli;
	boolean moreUsed = false;
	boolean lessUsed = false;
	int start;
	int end;
	ArrayList<String> arr;
	JTextArea textArea = new JTextArea();
	String name = "";
	Command c = new Command();
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
					window.frmCli.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI() {
		String UserName = "", DeviceName = "";
		try {
			UserName = System.getProperty("user.name");
			DeviceName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		name = UserName + "@" + DeviceName + ":~";
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private int numTyped = 0;

	private void initialize() {
		frmCli = new JFrame();
		frmCli.setTitle("CLI");
		frmCli.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmCli.setBounds(0, 0, 670, 391);

		textArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				char ch = arg0.getKeyChar();
				if (Character.isDefined(ch) && arg0.getKeyCode() != KeyEvent.VK_BACK_SPACE)
					numTyped++;
				//System.out.println(numTyped);
				
				///////////////////////// O //////////////////////////////
				int kc = arg0.getKeyCode();
				if (kc != KeyEvent.VK_DOWN && kc != KeyEvent.VK_UP && kc != KeyEvent.VK_LEFT && kc != KeyEvent.VK_RIGHT)
					textArea.setCaretPosition(textArea.getText().length());
				if (kc == KeyEvent.VK_BACK_SPACE && numTyped == 0)
					arg0.consume();
				else if (kc == KeyEvent.VK_BACK_SPACE)
					numTyped--;
				/////////////////////////////////////////////////////////

				if (arg0.getKeyCode() == KeyEvent.VK_SPACE && (moreUsed || lessUsed)) {
					display();
				}
				if (arg0.getKeyChar() != '\n')
					return;

				numTyped = 0;
				String cmd = "";
				int i = textArea.getText().length() - 1;
				while (i >= 0) {
					if (textArea.getText().charAt(i) == '\n')
						break;
					cmd = textArea.getText().charAt(i) + cmd;
					i--;
				}
				//System.out.println(cmd);
				
				for(int j=0; j<cmd.length(); j++){
					if(cmd.charAt(j) == ' '){
						cmd = cmd.substring(j+1);
						break;
					}
				}
				textArea.append("" + '\n');
				
				//System.out.println(cmd);
				String[] commands = cmd.split(";");

				for (String cm : commands) {
					String command = "", argument = "";
					int idx = 0;
					while (idx < cm.length() && cm.charAt(idx) != ' ') {
						command += cm.charAt(idx);
						idx++;
					}
					idx++;

					while (idx < cm.length()) {
						argument += cm.charAt(idx);
						idx++;
					}

					int id = c.getID(command);

					if (command.equals("exit")) {
						if (argument != "")
							textArea.append("exit has no arguments!\n");
						else
							System.exit(0);
					} else if (command.equals("clear")) {
						if (argument != "")
							textArea.append("clear has no arguments!\n");
						else
							textArea.setText("");
					} else if (command.equals("more") || command.equals("less")) {
						lessMore(argument, command);
					} else if (id != -1) {
						if (!argument.contains(">")) {
							textArea.append(c.execute(id, argument) + '\n');
						} else {
							operator(id, argument);
						}
					} else {
						textArea.append(command + ": command not found\n");
					}

					System.out.println(cmd);
					arg0.consume();
					
				}
				textArea.append(name + c.dir + "$ ");
			}
		});
		
		textArea.setCaretColor(Color.white);
		textArea.setFont(new Font("Consolas", Font.BOLD, 13));
		textArea.setForeground(Color.WHITE);
		textArea.setBackground(new Color(102, 0, 102));
		textArea.setText(name + c.dir + "$ ");
		textArea.setBounds(0, 0, 653, 352);

		frmCli.getContentPane().setLayout(null);
		JScrollPane scrollPane = new JScrollPane(textArea);
		scrollPane.setBounds(0, 0, 653, 352);
		frmCli.getContentPane().add(scrollPane);

	}

	private void lessMore(String argument, String command) {
		if (argument.isEmpty()) {
			textArea.append(command + " has one argument!\n");
			return;
		}
		String loc1 = "", absLoc1;
		char delim = ' ';
		int j = 0;
		if (argument.charAt(0) == '"') {
			delim = '"';
			j++;
		}
		for (; j < argument.length(); j++) {
			if (argument.charAt(j) == delim)
				break;
			loc1 += argument.charAt(j);
		}
		j++;

		
		absLoc1 = c.getAbsolute(loc1);

		if (j < argument.length() || argument.isEmpty()) {
			textArea.append(command + " has only one argument!\n");
		} else {

			File file1 = new File(absLoc1);
			if (!file1.exists()) {
				textArea.append("The file is invalid\n");
			} else {
				BufferedReader br;
				String currentLine;
				try {
					arr = new ArrayList<String>();
					br = new BufferedReader(new FileReader(absLoc1));
					while ((currentLine = br.readLine()) != null) {
						arr.add(currentLine);
					}
					start = 0;
					end = Math.min(19, arr.size());

					if (end <= arr.size()) {
						String show = "";
						for (int indx = start; indx < end; ++indx) {
							show += (arr.get(indx) + '\n');
						}
						textArea.setText(show);
						if (end < arr.size()) {
							textArea.append("--more--\n");
							if (command.equals("more"))
								moreUsed = true;
							else
								lessUsed = true;
						}
					}

				} catch (Exception errore) {
					errore.printStackTrace();
				}
			}
		}

	}

	private void display() {
		start = (Math.min(end + 19, arr.size()) - 19);
		end = Math.min(start + 19, arr.size());
		if (lessUsed)
			start = 0;
		if (end <= arr.size()) {
			String show = "";
			for (int indx = start; indx < end; ++indx) {
				show += (arr.get(indx) + '\n');
			}
			textArea.setText(show);
			if (end < arr.size())
				textArea.append("--more--\n");
			else {
				textArea.append("--End--\n");
				moreUsed = false;
				lessUsed = false;
			}
		}
		if (lessUsed)
			start = (Math.min(end + 19, arr.size()) - 19);

	}

	private void operator(int id, String argument) {
		String[] args = argument.split(">");
		for (int j = 0; j < args.length; j++) {
			args[j] = args[j].trim();
			args[j] = args[j].replaceAll("\"", "");
		}
		if (args.length == 2) {
			try {
				FileWriter fw = new FileWriter(c.getAbsolute(args[1]));
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(c.execute(id, args[0]));
				bw.close();
			} catch (IOException e) {
				textArea.append("Invalid operand\n");
			}
		} else if (args.length == 3) {
			try {
				String abs = c.getAbsolute(args[2]);
				if (!new File(abs).exists())
					new File(abs).createNewFile();

				FileWriter fw = new FileWriter(abs, true);
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(c.execute(id, args[0]));
				bw.close();
			} catch (IOException e) {
				textArea.append("Invalid operand\n");
			}
		} else {
			textArea.append("Invalid use of > operator");
		}

	}

}
