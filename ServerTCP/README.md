# TCP Server
```diff
- - - - - - - - TDD FOR LIFE! - - - - - - - -
```

To test with your own hands, open 4 command prompts: 1 and server, 1 proxy and 2 clients
1) compile files

I put absolute path, you may google javac docs to make it more convenient

```bat
your directory> "C:\Program Files\Java\jdk-14.0.2\bin\javac.exe" ServerTCP.java ClientTCP.java ProxyThread.java ServeThread.java
```
2) run end server (order matters!)
```bat
your directory> "C:\Program Files\Java\jdk-14.0.2\bin\java.exe" ru.nsu.fit.lab15.ServerTCP 2525 2 localhost
```
be careful of:
- "java -version" and "absolute path\java.exe -version"
- %CLASSPATH% must be set and NOT! include package

3) run proxy (mind order)
```bat
your directory> "C:\Program Files\Java\jdk-14.0.2\bin\java.exe" ru.nsu.fit.lab15.ServerTCP 3434 2 localhost 2525 localhost
```

4) run clients
```bat
your directory> "C:\Program Files\Java\jdk-14.0.2\bin\java.exe" ru.nsu.fit.lab15.ClientTCP 3434 localhost c
```

5) the client will stay connected until you enter "exit"

6) the number of connections argument is supposed to be a limit of maximum concurrent connections, but actually is also a limit of total connections. 
By the time of writing this instruction I am too tired to fix it. You can fix yourself
