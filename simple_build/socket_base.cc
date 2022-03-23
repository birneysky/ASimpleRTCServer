#include "socket_base.h"
#include <assert.h>
#include <sys/socket.h>
#include <unistd.h>

SocketBase::SocketBase(): 
    socket_(INVALID_SOCKET) {

}

SocketBase::SocketBase(NativeSocket sock) : socket_(sock) {

}

SocketBase::~SocketBase() {
    Close();
}

NativeSocket SocketBase::socket() const { 
    return socket_; 
}
    
bool SocketBase::valid() const {
    return socket_ != INVALID_SOCKET; 
}

bool SocketBase::Create() {
  assert(!valid());
  socket_ = ::socket(AF_INET, SOCK_STREAM, 0);
  return valid();
}

void SocketBase::Close() {
  if (socket_ != INVALID_SOCKET) {
    ::close(socket_);
    socket_ = INVALID_SOCKET;
  }
}