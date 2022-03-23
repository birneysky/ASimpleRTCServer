#ifndef LISTENING_SOCKET_H_
#define LISTENING_SOCKET_H_

#include "socket_base.h"


class DataSocket;
class ListeningSocket: public SocketBase {
public:
  ListeningSocket();

  bool Listen(unsigned short port);
  DataSocket* Accept() const;
};


#endif