#ifndef DATA_SOCKET_H_
#define DATA_SOCKET_H_

#include <string>

#include "socket_base.h"


// Represents an HTTP server socket.
class DataSocket: public SocketBase {
public:
    enum RequestMethod { INVALID, GET, POST, OPTIONS, };
public:
    static const char kCrossOriginAllowHeaders[];
public:
    explicit DataSocket(NativeSocket socket);
    ~DataSocket();

    bool headers_received() const;
    RequestMethod method() const;
    const std::string& request_path() const;
    std::string request_arguments() const;
    const std::string& data();
    const std::string& content_type() const;
    size_t content_length() const;
    bool request_received() const;
    bool data_received() const;

     // Checks if the request path (minus arguments) matches a given path.
    bool PathEquals(const char* path) const;

    // Called when we have received some data from clients.
    // Returns false if an error occurred.
    bool OnDataAvailable(bool* close_socket);

    // Send a raw buffer of bytes.
    bool Send(const std::string& data) const;

    // Send an HTTP response.  The |status| should start with a valid HTTP
    // response code, followed by a string.  E.g. "200 OK".
    // If |connection_close| is set to true, an extra "Connection: close" HTTP
    // header will be included.  |content_type| is the mime content type, not
    // including the "Content-Type: " string.
    // |extra_headers| should be either empty or a list of headers where each
    // header terminates with "\r\n".
    // |data| is the body of the message.  It's length will be specified via
    // a "Content-Length" header.
    bool Send(const std::string& status,
            bool connection_close,
            const std::string& content_type,
            const std::string& extra_headers,
            const std::string& data) const;
 // Clears all held state and prepares the socket for receiving a new request.
  void Clear();

 protected:
    // A fairly relaxed HTTP header parser.  Parses the method, path and
    // content length (POST only) of a request.
    // Returns true if a valid request was received and no errors occurred.
    bool ParseHeaders();

    // Figures out whether the request is a GET or POST and what path is
    // being requested.
    bool ParseMethodAndPath(const char* begin, size_t len);

    // Determines the length of the body and it's mime type.
    bool ParseContentLengthAndType(const char* headers, size_t length);

protected:
    RequestMethod method_;
    size_t content_length_;
    std::string content_type_;
    std::string request_path_;
    std::string request_headers_;
    std::string data_;
};



#endif