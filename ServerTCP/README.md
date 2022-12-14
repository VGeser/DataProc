# TCP Server

To test with your own hands, open 4 command prompts: 1 end server, 1 proxy and 2 clients
1) compile files

I put absolute path, you may google javac docs to make it more convenient

```bat
your directory> "C:\Program Files\Java\jdk-14.0.2\bin\javac.exe" Server.java Client.java Proxy.java
```
2) run end server (order matters!)
```bat
your directory> "C:\Program Files\Java\jdk-14.0.2\bin\java.exe" ru.nsu.fit.nioproxy.Server 2525 
```
be careful of:
- "java -version" and "absolute path\java.exe -version"
- %CLASSPATH% must be set and NOT! include package

3) run proxy (order matters!)
```bat
your directory> "C:\Program Files\Java\jdk-14.0.2\bin\java.exe" ru.nsu.fit.nioproxy.Proxy 3434 <your-local-IP> 2525
```
For some reason InetAddress.getByName(String address) does not recognize "localhost" or loopback A.K.A. 127.0.0.1. In my case, I put 169.254.248.109, which is my grey public IP

4) run clients
```bat
your directory> "C:\Program Files\Java\jdk-14.0.2\bin\java.exe" ru.nsu.fit.nioproxy.Client 3434
```

5) the client will stay connected until you enter "exit"
