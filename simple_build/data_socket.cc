#include "data_socket.h"

#include <netinet/in.h>
#include <sys/select.h>
#include <sys/socket.h>
#include <assert.h>
#include <unistd.h>


bool SocketBase::Create() {
  assert(!valid());
  socket_ = ::socket(AF_INET, SOCK_STREAM, 0);
  return valid();
}

void SocketBase::Close() {
  if (socket_ != INVALID_SOCKET) {
    close(socket_);
    socket_ = INVALID_SOCKET;
  }
}