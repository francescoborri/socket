chat-compile:
	javac -d out/production/ -classpath src/tcp/chat/ src/tcp/chat/client/* src/tcp/chat/server/*

run-chat-server:
	cd out/production; \
	java tcp.chat.server.ChatServerExecutor

run-chat-client:
	cd out/production; \
	java tcp.chat.client.ChatClientExecutor
