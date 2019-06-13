package fileload;

//·¢ËÍ·½
public class FileSendTest{

  public static void main(String[] args) {
      // TODO Auto-generated method stub
	  String path = "E:\\downloads\\fortest.mp4";
      SendFileThread sf=new SendFileThread(path);
      sf.start();
  }

}