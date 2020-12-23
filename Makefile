chat-compile:
	javac -d out/production/socket -classpath src/tcp/chat/ src/tcp/chat/client/* src/tcp/chat/server/*

run-chat-server:
	java -classpath out/production/socket/ tcp.chat.server.ChatServerExecutor

run-chat-client:
	java -classpath out/production/socket/ tcp.chat.client.ChatClientExecutor
