// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.



#include "hello_shared.h"
#include "hello_static.h"
#include "data_socket.h"
#include "listening_socket.h"

#include <stdio.h>
#include <iostream>
#include <vector>
#include <sys/select.h>

int main(int argc, char* argv[]) {
  printf("%s, %s\n", GetStaticText(), GetSharedText());
  const int port = 8888;
  ListeningSocket listener;
  if (!listener.Create()) {
    std::cout << "Failed to create server socket" << std::endl;
    return -1;
  } else if (!listener.Listen(port)) {
    std::cout << "Failed to listen on server socket" << std::endl;
    return -1;
  }

  typedef std::vector<DataSocket*> SocketArray;
  SocketArray sockets;
  bool quit = false;
  while (!quit) {
    fd_set socket_set;
    FD_ZERO(&socket_set);
    if (listener.valid())
      FD_SET(listener.socket(), &socket_set);

      for (SocketArray::iterator i = sockets.begin(); i != sockets.end(); ++i)
        FD_SET((*i)->socket(), &socket_set);

    struct timeval timeout = {10, 0};
    if (select(FD_SETSIZE, &socket_set, NULL, NULL, &timeout) == SOCKET_ERROR) {
      std::cout << "select failed" << std::endl;
      break;
    }
  }
  
  return 0;
}
