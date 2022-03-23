#ifndef CHANNEL_MEMBER_H_
#define CHANNEL_MEMBER_H_

#include<string>
#include<queue>

// extern const char kPeerIdHeader[];
// extern const char* kRequestPaths[];


  static const char kPeerIdHeader[]  = "Pragma: ";
  static const char* kRequestPaths[]  = {
    "/wait",
    "/sign_out",
    "/message",
};

enum RequestPathIndex { kWait, kSignOut, kMessage };

class DataSocket;
class ChannelMember {
public:
  

public:
  explicit ChannelMember(DataSocket* socket);
  ~ChannelMember();

  bool connected() const;
  int id() const;
  void set_disconnected();
  bool is_wait_request(DataSocket* ds) const;
  const std::string& name() const;

  bool TimedOut();

  std::string GetPeerIdHeader() const;

  bool NotifyOfOtherMember(const ChannelMember& other);

  // Returns a string in the form "name,id\n".
  std::string GetEntry() const;

  void ForwardRequestToPeer(DataSocket* ds, ChannelMember* peer);

  void OnClosing(DataSocket* ds);

  void QueueResponse(const std::string& status,
                     const std::string& content_type,
                     const std::string& extra_headers,
                     const std::string& data);

  void SetWaitingSocket(DataSocket* ds);

 protected:
  struct QueuedResponse {
    std::string status, content_type, extra_headers, data;
  };

  DataSocket* waiting_socket_;
  int id_;
  bool connected_;
  time_t timestamp_;
  std::string name_;
  std::queue<QueuedResponse> queue_;
  static int s_member_id_;
};


#endif