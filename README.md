# FileTransfer
模拟多线程传输文件，模拟ftp的命令操作，支持断点续传

1、打开服务器端与客户端，双端确认通信成功 OK
2、get [filename]从服务器端获取一个文件名未filename的文件OK
3、stop 暂停当前文件的传输，但不删除本地的temp缓存，再次使用get请求时可以断点续传OK
4、cancel 取消本次文件的传输，删除本地的temp缓存，将服务器端记录的文件位置置为0 OK
5、ls 列出当前服务器端的所有文件  ls [suffix]列出后缀为suffix的所有文件OK
6、quit 断开本次连接，通信与文件传输的所有线程关闭OK
7、find [filename]查找有没有文件名为filename 的文件OK
8、服务器返回状态：  00文件不存在   01文件存在
