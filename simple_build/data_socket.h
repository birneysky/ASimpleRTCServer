
#ifndef DATA_SOCKET_H_
#define DATA_SOCKET_H_

typedef int NativeSocket;

const NativeSocket INVALID_SOCKET = -1;

class  SocketBase {
public:
    SocketBase(): socket_(INVALID_SOCKET){}
    explicit SocketBase(NativeSocket sock) : socket_(sock) {}
    ~SocketBase() { Close(); }

    NativeSocket socket() const { return socket_; }
    bool valid() const { return socket_ != INVALID_SOCKET; }
    
    bool Create();
    void Close();
private:
    NativeSocket socket_;
};



#endif