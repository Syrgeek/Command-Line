package cli;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;

public class Command {

	private ArrayList<String> commands = new ArrayList<String>() {
		{
			add("pwd");
			add("cd");
			add("ls");
			add("cp");
			add("date");
			add("mkdir");
			add("cat");
			add("rm");
			add("rmdir");
			add("mv");
			add("help");
			add("grep");
			add("args");
			add("find");

		}
	};
	public static String dir = "D:\\";
	private static String defaultDir = "D:\\";
	private String errorMsg;
	private String Help = "pwd: Display current user directory.\r\n"
			+ "cd new_directory: Changes the current directory to another one.\r\n"
			+ "ls: Display list of current dicectory contents.\r\n"
			+ "cp: If the last argument names an existing directory, cp copies each other given file into a file with the same name in that directory. Otherwise, if only two files are given, it copies the first onto the second. It is an error if the last argument is not a directory and more than two files are given. By default, it does not copy directories.\r\n"
			+ "date:  Current date/time\r\n" + "mkdir directory_name: creates a directory with each given name.\r\n"
			+ "cat  myfile.txt myfile2.txt: Concatenate files and print on the standard output.\r\ncat myfile.txt ;to display the content of this file\r\n"
			+ "rm file.txt: Removes each specified file. By default, it does not remove directories.\r\n"
			+ "rmdir /home/mydir: Removes each given directory.\r\n"
			+ "mv /home/myfile.txt /root/myfile.txt: If the last argument names an existing directory, mv moves each other given file into a file with the same name in that directory. Otherwise, if only two files are given, it moves the first onto the second.\r\n"
			+ "clear: Clear the console.\r\n" + "exit: Stop all.\r\n"
			+ "more myfile.txt: Let us display and scroll down the output in one direction only.\r\n"
			+ "less myfile.txt: Like more but more enhanced. It support scroll forward and backward (by arrows).\r\n"
			+ "grep word file.txt: Search for the given text in the given file and write each matching line to standard outpu\r\n"
			+ "args Command: list all parameters for a specific command.\r\n";

	public int getID(String cmd) {
		for (int i = 0; i < commands.size(); ++i) {
			if (commands.get(i).equals(cmd))
				return i;
		}
		return -1;
	}

	public String execute(int id, String arg) {
		switch (id) {
		case 0: {
			if (pwdValid(arg))
				return pwdExe();
			break;
		}
		case 1: {
			return cdExe(arg);
		}
		case 2: {
			return lsExe(arg);
		}
		case 3: {
			return cpExe(arg);
		}
		case 4: {
			return dateExe();
		}
		case 5: {
			return mkdirExe(arg);
		}
		case 6: {
			if (catValid(arg))
				return catExe(arg);
			break;
		}
		case 7:
			return rmExe(arg);
		case 8:
			return rmdirExe(arg);
		case 9:
			return mvExe(arg);
		case 10:
			return Help;
		case 11: {
			if (grepValid(arg))
				return grepExe(arg);
			break;
		}
		case 12: {
			return args(arg);
		}
		case 13: {
			return findExe(arg);
		}
		default:
			return errorMsg;
		}
		return errorMsg;
	}

	public String getAbsolute(String path) {
		if (path.length() > 1 && path.charAt(1) == ':') {
			return path;
		} else {
			if (dir.charAt(dir.length() - 1) != '\\')
				return dir + '\\' + path;
			else
				return dir + path;
		}
	}
	// ***************************************************************

	private ArrayList<String> getArgs(String input) {
		input = input.trim();

		ArrayList<String> args = new ArrayList<String>();
		String arr[] = input.split(" ");

		String curr = "";
		boolean openQuote = false;
		for (int i = 0; i < arr.length; i++) {
			if (!openQuote) {
				curr = arr[i];
				if (!curr.isEmpty() && curr.charAt(0) == '\"') {
					curr = curr.substring(1, curr.length());
					openQuote = true;
				}
			} else {
				curr += ' ' + arr[i];
			}

			if (openQuote) {
				if (!curr.isEmpty() && curr.charAt(curr.length() - 1) == '\"') {
					curr = curr.substring(0, curr.length() - 1);
					openQuote = false;
				}
			}

			//System.out.println(curr);
			if (!openQuote && !curr.isEmpty()) {
				args.add(curr);
			}
		}

		return args;
	}

	// ***************************************************************
	// grep
	// grep validation
	public boolean grepValid(String cmd) {
		String loc = "", absLoc, word = "";
		char delim = ' ';
		int i = 0;
		if (cmd.charAt(0) == '"') {
			delim = '"';
			i++;
		}
		for (; i < cmd.length(); i++) {
			if (cmd.charAt(i) == delim)
				break;
			word += cmd.charAt(i);
		}
		i++;
		
		System.out.println(word);
		
		if(delim == '"')
			i++;
		
		if (i < cmd.length()) {
			delim = ' ';
			if (cmd.charAt(i) == '"') {
				delim = '"';
				i++;
			}
			for (; i < cmd.length(); i++) {
				if (cmd.charAt(i) == delim)
					break;
				loc += cmd.charAt(i);
			}
			i++;
			
			System.out.println(loc);

			if (i < cmd.length()) {
				errorMsg = "grep has only tow arguments!";
				return false;
			}
		} else {
			errorMsg = "grep has tow arguments (word and file)";
			return false;
		}

		absLoc = getAbsolute(loc);
		File file1 = new File(absLoc);
		if (!file1.exists()) {
			errorMsg = "The file is invalid";
			return false;
		}

		return true;

	}

	// grep execution
	public String grepExe(String cmd) {
		String loc = "", absLoc, word = "";
		char delim = ' ';
		int i = 0;
		if (cmd.charAt(0) == '"') {
			delim = '"';
			i++;
		}
		for (; i < cmd.length(); i++) {
			if (cmd.charAt(i) == delim)
				break;
			word += cmd.charAt(i);
		}
		i++;
		
		if(delim == '"')
			i++;

		if (i < cmd.length()) {
			delim = ' ';
			if (cmd.charAt(i) == '"') {
				delim = '"';
				i++;
			}
			for (; i < cmd.length(); i++) {
				if (cmd.charAt(i) == delim)
					break;
				loc += cmd.charAt(i);
			}
			i++;
		}

		absLoc = getAbsolute(loc);
		File file1 = new File(absLoc);
		String ret = "";
		BufferedReader br;
		String currentLine;
		try {
			br = new BufferedReader(new FileReader(absLoc));
			while ((currentLine = br.readLine()) != null) {
				if (currentLine.contains(word))
					ret += (currentLine + '\n');
			}
		} catch (Exception errore) {
			errore.printStackTrace();
		}

		return ret;
	}

	// *********************************************************************
	// cat
	// cat validation
	public boolean catValid(String cmd) {
		String loc1 = "", loc2 = "", absLoc1, absLoc2;
		char delim = ' ';
		int i = 0;
		if (cmd.charAt(0) == '"') {
			delim = '"';
			i++;
		}
		for (; i < cmd.length(); i++) {
			if (cmd.charAt(i) == delim)
				break;
			loc1 += cmd.charAt(i);
		}
		i++;
		absLoc1 = getAbsolute(loc1);

		boolean flag = false;
		if (i < cmd.length()) { // there is another file
			flag = true;
			delim = ' ';
			if (cmd.charAt(i) == '"') {
				delim = '"';
				i++;
			}
			for (; i < cmd.length(); i++) {
				if (cmd.charAt(i) == delim)
					break;
				loc2 += cmd.charAt(i);
			}
			i++;
			if (i < cmd.length()) {
				errorMsg = "cat has maximum tow arguments!";
				return false;
			}
		}

		File file1 = new File(absLoc1);
		if (!file1.exists()) {
			if (flag)
				errorMsg = "First file is invalid";
			else
				errorMsg = "The file is invalid";
			return false;
		}

		if (flag) {
			absLoc2 = getAbsolute(loc2);
			File file2 = new File(absLoc2);
			if (file2.exists())
				return true;
			else {
				errorMsg = "Second file is invalid";
				return false;
			}
		}

		return true;

	}

	// cat execution
	public String catExe(String cmd) {
		String loc1 = "", loc2 = "", absLoc1, absLoc2, ret = "";
		char delim = ' ';
		int i = 0;
		if (cmd.charAt(0) == '"') {
			delim = '"';
			i++;
		}
		for (; i < cmd.length(); i++) {
			if (cmd.charAt(i) == delim)
				break;
			loc1 += cmd.charAt(i);
		}
		i++;
		absLoc1 = getAbsolute(loc1);

		boolean flag = false;
		if (i < cmd.length()) { // there is another file
			flag = true;
			delim = ' ';
			if (cmd.charAt(i) == '"') {
				delim = '"';
				i++;
			}
			for (; i < cmd.length(); i++) {
				if (cmd.charAt(i) == delim)
					break;
				loc2 += cmd.charAt(i);
			}
		}

		BufferedReader br;
		String currentLine;
		try {
			br = new BufferedReader(new FileReader(absLoc1));
			while ((currentLine = br.readLine()) != null) {
				ret += (currentLine + "\r\n");
			}
		} catch (Exception errore) {
			errore.printStackTrace();
		}

		if (flag) {
			absLoc2 = getAbsolute(loc2);
			try {
				br = new BufferedReader(new FileReader(absLoc2));
				while ((currentLine = br.readLine()) != null) {
					ret += (currentLine + "\r\n");
				}
			} catch (Exception errore) {
				errore.printStackTrace();
			}
		}
		return ret;
	}

	// ***************************************************************
	// pwd
	// pwd validation
	public boolean pwdValid(String cmd) {
		String wd = dir;
		if (wd == "")
			return false;
		else {
			File f = new File(wd);
			return (f.isDirectory());
		}
	}

	// pwd execution
	public String pwdExe() {
		return dir;
	}

	// ***************************************************************
	// date
	public String dateExe() {
		Date date = new Date();
		return date.toString();
	}

	// ***************************************************************

	// cd execution
	String cdExe(String cmd) {
		ArrayList<String> args = getArgs(cmd);
		if (args.size() > 1)
			return "Invalid operand\r\n";

		if (args.isEmpty()) {
			dir = defaultDir;
		} else if (cmd.equals("..")) {
			if (new File(dir).getParent() != null) {
				dir = new File(dir).getParent();
			}
		} else {
			String newCmd = getAbsolute(args.get(0));
			if (new File(newCmd).isDirectory()) {
				dir = newCmd;
			} else {
				return "No such file or directory\r\n";
			}
		}
		return "";
	}

	// ***************************************************************
	// cp
	// cp execution
	public String cpExe(String cmd) {
		// TODO Auto-generated method stub

		ArrayList<String> args = getArgs(cmd);
		if (args.size() == 0)
			return "cp: missing file operands\r\n";
		else if (args.size() == 1)
			return "cp: missing destination file operand after '" + args.get(0) + "'\r\n";
		else if (args.size() != 2)
			return "cp: extra invalid operands" + "'\r\n";

		String absLoc1 = getAbsolute(args.get(0)), absLoc2 = getAbsolute(args.get(1));
		if (!new File(absLoc1).exists()) {
			return "cp: '" + args.get(0) + "' No souch file or directory\r\n";
		}

		Path src = Paths.get(absLoc1);
		Path tar = Paths.get(absLoc2);
		CopyOption[] options = new CopyOption[] { StandardCopyOption.REPLACE_EXISTING };
		try {
			if (new File(tar.toString()).isDirectory())
				Files.copy(src, tar.resolve(src.getFileName()), options);
			else
				Files.copy(src, tar, options);
		} catch (IOException e) {
			return "cp: '" + args.get(1) + "' No souch file or directory\r\n";
		}
		return "";
	}

	// *********************************************************************************
	// ls
	// ls execution
	public String lsExe(String input) {
		ArrayList<String> args = getArgs(input);
		if (args.size() > 1)
			return "ls: extra invalid operands\r\n";

		String dirc = "";
		if (args.size() == 0)
			dirc = dir;
		else
			dirc = getAbsolute(args.get(0));

		if (!new File(dirc).isDirectory())
			return "ls: cannot access " + dirc + ": No such file or directory\r\n";
		File f = new File(dirc);
		File[] files = f.listFiles();
		String ret = "";
		for (File file : files) {
			if (!file.isHidden())
				ret += (file.getName() + "\r\n");
		}
		return ret;
	}

	// ******************************************
	// mkdir
	// mkdir execution
	public String mkdirExe(String cmd) {
		String abs = getAbsolute(cmd);
		if (new File(abs).exists())
			return "mkdir: cannot create directory '" + cmd + "': Directory exists.\r\n";

		new File(abs).mkdir();
		return "";
	}

	// ******************************************************************
	// rm
	// rm execution
	public String rmExe(String cmd) {
		ArrayList<String> args = getArgs(cmd);
		if (args.size() == 0)
			return "rm: misssing operand\r\n";
		if (args.size() > 1)
			return "rm: extra invalid operands\r\n";

		String abs = getAbsolute(args.get(0));
		if (!new File(abs).exists())
			return "rm: '" + args.get(0) + "' No souch file or directory\n";

		if (!new File(abs).delete())
			return "rm: cannot remove '" + args.get(0) + "'\r\n";

		return "";
	}

	// ***************************************************
	// rmdir
	// rmdir execution
	public String rmdirExe(String cmd) {
		ArrayList<String> args = getArgs(cmd);
		if (args.size() == 0)
			return "rmdir: misssing operand\r\n";
		if (args.size() > 1)
			return "rmdir: extra invalid operands\r\n";

		String abs = getAbsolute(args.get(0));
		File dirD = new File(abs);
		if (!dirD.exists()) {
			return "rmdir: '" + args.get(0) + "' No souch directory\r\n";
		}
		if (!dirD.delete()) {
			File[] lst = dirD.listFiles();
			for (File cur : lst) {
				rmdirExe("\"" + cur.getAbsolutePath() + "\"");
			}
			dirD.delete();
		}
		return "";
	}

	// ***************************************************
	// mv
	// mvExe
	public String mvExe(String cmd) {
		// TODO Auto-generated method stub

		ArrayList<String> args = getArgs(cmd);
		if (args.size() == 0)
			return "mv: missing file operands\r\n";
		else if (args.size() == 1)
			return "mv: missing destination file operand after '" + args.get(0) + "'\r\n";
		else if (args.size() != 2)
			return "mv: extra invalid operands" + "'\r\n";

		String absLoc1 = getAbsolute(args.get(0)), absLoc2 = getAbsolute(args.get(1));
		if (!new File(absLoc1).exists()) {
			return "mv: '" + args.get(0) + "' No souch file or directory\r\n";
		}

		Path src = Paths.get(absLoc1);
		Path tar = Paths.get(absLoc2);
		CopyOption[] options = new CopyOption[] { StandardCopyOption.REPLACE_EXISTING };
		try {
			if (new File(tar.toString()).isDirectory())
				Files.move(src, tar.resolve(src.getFileName()), options);
			else
				Files.move(src, tar, options);
		} catch (IOException e) {
			return "mv: '" + args.get(1) + "' No souch file or directory\r\n";
		}
		return "";
	}

	// ***************************************************
	public String args(String cmd) {

		ArrayList<String> args = getArgs(cmd);
		if (args.size() == 0)
			return "args: missing file operands\r\n";
		else if (args.size() != 1)
			return "args: extra invalid operands" + "'\r\n";
		if (cmd.equals("less"))
			return "less [File.txt]";
		else if (cmd.equals("more"))
			return "more [File.txt]";

		String[] cmds = new String[15];
		cmds[0] = "Invalid Command\r\n";
		cmds[1] = "pwd []: No arguments\r\n";
		cmds[2] = "cd []: Changes the current directory to the default directory\r\n"
				+ "cd [..]: Changes the current directory to the parent directory\r\n"
				+ "cd [new_directory]: Changes the current directory to new_directoy.\r\n";
		cmds[3] = "ls []: Displays list of current directory contents.\r\n"
				+ "ls [directory]: Displays list of specified directory contents.\r\n";
		cmds[4] = "cp [src_File] [tar_File]: Replace tar_File with src_File\r\n"
				+ "cp [src_File] [tar_Directory]: Copies src_File to tar_Directory\r\n";
		cmds[5] = "date []: No arguments\r\n";
		cmds[6] = "mkdir [new_Direcory]\r\n";
		cmds[7] = "cat [File_1] [File_2]:  Concatenate files and print on the standard output.\r\n"
				+ "cat [File]: Displays the content of this file\r\n";
		cmds[8] = "rm [File_to_be_removed]\r\n";
		cmds[9] = "rmdir [Folder_to_be_removed]\r\n";
		cmds[10] = "mv [src_File] [tar_File]: Replaces tar_File with src_File then deletes original src_file\r\n"
				+ "mv [src_File] [tar_Directory]: Copies src_File to tar_Directory then deletes original src_file\r\n";
		cmds[11] = "help []: No arguments\r\n";
		cmds[12] = "grep [text] [File]\r\n";
		cmds[13] = "args [command]\r\n";
		cmds[14] = "args [File_Name] [Directory]\r\n";

		int id = getID(cmd) + 1;
		return cmds[id];
	}

	// ***************************************************
	// find
	// find execution
	public String findExe(String cmd) {
		
		ArrayList<String> args = getArgs(cmd);
		if (args.size() <= 1)
			return "find: misssing operand\r\n";
		if (args.size() > 2)
			return "find: extra invalid operands\r\n";

		String abs = getAbsolute(args.get(1));
		File dirF = new File(abs);
		//System.out.println(abs);
		
		if (!dirF.exists()) {
			return "find: '" + args.get(1) + "' No souch directory\r\n";
		}
		String res = "";
		File[] lst = dirF.listFiles();
		for (File cur : lst) {
			;
			if (cur.isFile() && cur.getName().equals(args.get(0))) {
				res += cur.getAbsolutePath() + "\r\n";
			} else if (cur.isDirectory() && !cur.isHidden()) {
				res += findExe(args.get(0) + "  \"" + cur.getAbsolutePath() + "\"");
			}
		}
		return res;
	}
}