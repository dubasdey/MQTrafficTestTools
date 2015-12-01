@echo off

set host=180.106.44.192
set port=1414
set channel=JAVA.CHANNEL
set manager=Test
set queue=erodriguez_CutoverQueue
set threads=10
set delay=500
set file=message.xml

REM <host> <port> <channel> <manager> <queue> <file> <threads> <threadDelay>
java -cp jmsTool.jar org.erc.jms.Sender %host% %port% %channel% %manager% %queue% %file% %threads% %delay%