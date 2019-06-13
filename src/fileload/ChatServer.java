package fileload;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

@SuppressWarnings("all")
public class ChatServer {

	private ServerSocket serverSocket;
	private Socket socket;
	private SendFileThread sf = null;
	private boolean stopBit = true;
	private Thread receiveThread = null;
	public ChatServer() {
		try {
			serverSocket = new ServerSocket(8089);
			socket = serverSocket.accept();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {

		// Thread sendThread = new Thread(new SendThread());
		receiveThread = new Thread(new ReceiveThread());
		// sendThread.start();
		receiveThread.start();
	}
	public void stopThreads() {
		stopBit = false;
		receiveThread.interrupt();
	}
	public ArrayList<String> getAllFile(String path){
		File f = new File(path);
		if (!f.exists()) {
			System.out.println(path + " not exists");
			return null;
		}

		File fa[] = f.listFiles();
		//String [] fileList = new String [1000];
		ArrayList<String> fileList = new ArrayList<String> ();
		for (int i = 0; i < fa.length; i++) {
			File fs = fa[i];
			if (fs.isDirectory()) {
				fileList.add(fs.getName()+" [目录]");
				//System.out.println(fs.getName() + " [目录]");
			} else if(fs != null){
				fileList.add(fs.getName()+"");
				//System.out.println(fs.getName());
			}
		}
		return fileList;
		
	}
	public ArrayList<String> getFileEnd(String path, String suffix){
		File f = new File(path);
		if (!f.exists()) {
			System.out.println(path + " not exists");
			return null;
		}

		File fa[] = f.listFiles();
		//String [] fileList = new String [1000];
		ArrayList<String> fileList = new ArrayList<String> ();
		for (int i = 0; i < fa.length; i++) {
			File fs = fa[i];
			if(fs.getName().endsWith(suffix)) {
				fileList.add(fs.getName()+"");
			}
		}
		return fileList;
		
	}
	public static void main(String[] args) {

		ChatServer chatServer = new ChatServer();
		Scanner cin = new Scanner(System.in);
		chatServer.start();
	}

	public class SendThread implements Runnable {

		@Override
		public void run() {
			try {
				OutputStream outputStream = socket.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(outputStream);
				PrintWriter pw = new PrintWriter(osw, true);
				Scanner scanner = new Scanner(System.in);

				while (true && stopBit) {
					pw.println(scanner.nextLine());
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public class ReceiveThread implements Runnable {

		@Override
		public void run() {
			System.out.println("the server thread is running!");
			try {
				// 输入流
				InputStream inputStream = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(inputStream);
				BufferedReader br = new BufferedReader(isr);
				// 输出流
				OutputStream outputStream = socket.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(outputStream);
				PrintWriter pw = new PrintWriter(osw, true);
				while (true && stopBit) {
					
					String temp = br.readLine();
					String[] strArray = temp.split(" ");
					System.out.println(strArray[0]);
					// System.out.println(strArray[1]);
					try {
						Thread.sleep(3);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String path = "E:\\downloads\\";
					if (strArray[0].equals("get")) {

						String realpath = path + strArray[1];
						sf = new SendFileThread(realpath);
						sf.start();

					} else if (strArray[0].equals("stop")) {
						sf.stopTransmit();
					} else if (strArray[0].equals("find")) {
						String realpath = path + strArray[1];
						File file = new File(realpath);
						if (file.exists()) {
							pw.println("01");
						} else {
							pw.println("00");
						}
					} else if (strArray[0].equals("cancel")) {
						sf.offset = 0;
						sf.stopTransmit();
					} else if (strArray[0].equals("ls")) {
						ArrayList<String> fileList = null;
						if(strArray.length == 1) {
							fileList = getAllFile(path);
						}else if(strArray.length == 2) {
							fileList = getFileEnd(path, strArray[1]); 
						}
						for(int i = 0; i < fileList.size(); i++) {
							pw.println(fileList.get(i));
						}
					}else if(strArray[0].equals("quit")) {
						stopThreads();
					}

				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
