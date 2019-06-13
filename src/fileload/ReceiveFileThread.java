package fileload;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;


public class ReceiveFileThread extends Thread{

    private ServerSocket connectSocket=null;
    private Socket socket=null;
    private JFrame frame;
    private Container contentPanel;
    private JProgressBar progressbar;
    private DataInputStream dis;
    private DataOutputStream dos;
    private RandomAccessFile rad;
    private JLabel label;
    public String filename;
    
    public ReceiveFileThread(){
        frame=new JFrame("接收文件");
        try {
            connectSocket=new ServerSocket(12318);
            socket=connectSocket.accept();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public ReceiveFileThread(String filename){
    	this.filename = filename;
        frame=new JFrame("接收文件");
        try {
            connectSocket=new ServerSocket(12316);
            socket=connectSocket.accept();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void release() throws IOException {
    	if(dis != null)
    		dis.close();
    	if(dos != null)
    		dos.close();
    	if(rad != null)
    		rad.close();
    	if(connectSocket != null)
    		connectSocket.close();
    	if(socket != null)
    		socket.close();    	
    }
    public void stopTransmit() {
        try {
        	socket.close();
        	connectSocket.close();
        	dis.close();
            dos.close();
            rad.close();
            
            
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			System.out.println(e.getMessage().toString());
		}
        JOptionPane.showMessageDialog(frame, "已取消接收，连接关闭！", "提示：", JOptionPane.INFORMATION_MESSAGE);
        label.setText(" 取消接收,连接关闭");
       
    }
    public void run(){
        try {
            dis=new DataInputStream(socket.getInputStream());
            dos=new DataOutputStream(socket.getOutputStream());
            dis.readUTF();

            int permit=JOptionPane.showConfirmDialog(frame, "是否接收文件","文件传输请求：", JOptionPane.YES_NO_OPTION);
            if (permit==JOptionPane.YES_OPTION) {
                String filename=dis.readUTF();
                dos.writeUTF("ok");
                dos.flush();
                File file=new File(filename+".temp");

                rad=new RandomAccessFile(filename+".temp", "rw");

                //获得文件大小
                long size=0;
                if(file.exists()&&file.isFile()){
                    size=file.length();
                }

                dos.writeLong(size);//发送已接收的大小
                dos.flush();
                long allSize=dis.readLong();
                String rsp=dis.readUTF();

                int barSize=(int)(allSize/1024);
                int barOffset=(int)(size/1024);

                //传输界面
                frame.setSize(300,120);
                contentPanel =frame.getContentPane();
                contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
                progressbar = new JProgressBar();//进度条

                label=new JLabel(filename+" 接收中");
                contentPanel.add(label);

                progressbar.setOrientation(JProgressBar.HORIZONTAL);
                progressbar.setMinimum(0);
                progressbar.setMaximum(barSize);
                progressbar.setValue(barOffset);
                progressbar.setStringPainted(true);
                progressbar.setPreferredSize(new Dimension(150, 20));
                progressbar.setBorderPainted(true);
                progressbar.setBackground(Color.red);

                JButton cancel=new JButton("取消");
                
                JPanel barPanel=new JPanel();
                barPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

                barPanel.add(progressbar);
                barPanel.add(cancel);

                contentPanel.add(barPanel);

                cancel.addActionListener(new cancelActionListener());

                frame.setDefaultCloseOperation(
                        JFrame.EXIT_ON_CLOSE);
                frame.setVisible(true);

                //接收文件
                if (rsp.equals("ok")) {
                    rad.seek(size);
                    int length;
                    byte[] buf=new byte[1024];
                    while((length=dis.read(buf, 0, buf.length))!=-1){
                        rad.write(buf,0,length);
                        progressbar.setValue(++barOffset);
                    }
                    System.out.println("end");
                }

                label.setText(filename+" 结束接收");


                /*dis.close();
                dos.close();
                rad.close();*/
                this.release();
                frame.dispose();
                //文件重命名
                if (barOffset>=barSize) {
                    file.renameTo(new File(filename));
                }

            }else{
                /*dis.close();
                dos.close();
                frame.dispose();*/
            	this.release();
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
        	System.out.println("something happened!");
        	System.out.println(e.getMessage().toString());
            //label.setText(" 已取消接收，连接关闭！");
        }finally {
            frame.dispose();
        }
    }

    class cancelActionListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            try {
                dis.close();
                dos.close();
                rad.close();
                
                connectSocket.close();
                socket.close();
                JOptionPane.showMessageDialog(frame, "已取消接收，连接关闭！", "提示：", JOptionPane.INFORMATION_MESSAGE);
                label.setText(" 取消接收,连接关闭");
                
            } catch (IOException e1) {

            }
        }
    }

}