@echo off

set host=127.0.0.1
set port=1414
set channel=JAVA.CHANNEL
set manager=Test
set queue=Queue
set threads=10
set delay=500


REM <host> <port> <channel> <manager> <queue> <threads> <threadDelay>
java -cp jmsTool.jar org.erc.jms.Receiver %host% %port% %channel% %manager% %queue% %threads% %delay%