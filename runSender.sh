#/bin/bash


host=127.0.0.1
port=1414
channel=JAVA.CHANNEL
manager=Test
queue=Queue  
threads=10
delay=500
file=file.xml

# <host> <port> <channel> <manager> <queue> <file> <threads> <threadDelay>
java -cp jmsTool.jar org.erc.jms.Sender $host $port $channel $manager $queue $file $threads $delay
