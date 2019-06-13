package fileload;

//接收方.先运行
public class FileReceiveTest{

  public static void main(String[] args) {
      // TODO Auto-generated method stub
      ReceiveFileThread rf=new ReceiveFileThread();
      rf.start();
  }

}