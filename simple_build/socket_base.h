
#ifndef SOCKET_BASE_H_
#define SOCKET_BASE_H_

typedef int NativeSocket;

const NativeSocket INVALID_SOCKET = -1;
const int SOCKET_ERROR = -1;

class  SocketBase {
public:
    SocketBase();
    explicit SocketBase(NativeSocket sock);
    ~SocketBase();

    NativeSocket socket() const;
    bool valid() const;
    
    bool Create();
    void Close();
protected:
    NativeSocket socket_;
};


#endif