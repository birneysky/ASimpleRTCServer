#include "channel_member.h"
#include "data_socket.h"
#include "utils.h"

const size_t kMaxNameLength = 512;
int ChannelMember::s_member_id_ = 0;

ChannelMember::ChannelMember(DataSocket* socket):
    waiting_socket_(NULL), id_(++s_member_id_), connected_(true), timestamp_(time(NULL)) {
  assert(socket);
  assert(socket->method() == DataSocket::GET);
  assert(socket->PathEquals("/sign_in"));
  name_ = socket->request_arguments();
  if (name_.empty())
    name_ = "peer_" + int2str(id_);
  else if (name_.length() > kMaxNameLength)
    name_.resize(kMaxNameLength);

  std::replace(name_.begin(), name_.end(), ',', '_');
}

 ChannelMember::~ChannelMember() {}

bool ChannelMember::connected() const{
    return connected_;
}

int ChannelMember::id() const {
    return id_;
}
  
void ChannelMember::set_disconnected() {
    connected_ = false;
}

bool ChannelMember::is_wait_request(DataSocket* ds) const {
    return ds && ds->PathEquals(kRequestPaths[kWait]);
}

const std::string& ChannelMember::name() const {
    return name_;
}

bool ChannelMember::TimedOut()  {
    return waiting_socket_ == NULL && (time(NULL) - timestamp_) > 30;
}

std::string ChannelMember::GetPeerIdHeader() const {
    std::string ret(kPeerIdHeader + int2str(id_) + "\r\n");
    return ret;
}

bool ChannelMember::NotifyOfOtherMember(const ChannelMember& other) {
    assert(&other != this);
    QueueResponse("200 OK", "text/plain", GetPeerIdHeader(), other.GetEntry());
    return true;
}


std::string ChannelMember::GetEntry() const {
    assert(name_.length() <= kMaxNameLength);

    // name, 11-digit int, 1-digit bool, newline, null
    char entry[kMaxNameLength + 15];
    snprintf(entry, sizeof(entry), "%s,%d,%d\n",
            name_.substr(0, kMaxNameLength).c_str(), id_, connected_);
    return entry;
}

void ChannelMember::ForwardRequestToPeer(DataSocket* ds, ChannelMember* peer) {
    assert(peer);
    assert(ds);

    std::string extra_headers(GetPeerIdHeader());

    if (peer == this) {
        ds->Send("200 OK", true, ds->content_type(), extra_headers, ds->data());
    } else {
        printf("Client %s sending to %s\n", name_.c_str(), peer->name().c_str());
        peer->QueueResponse("200 OK", ds->content_type(), extra_headers,
                        ds->data());
        ds->Send("200 OK", true, "text/plain", "", "");
    }
}

void ChannelMember::OnClosing(DataSocket* ds) {
    if (ds == waiting_socket_) {
        waiting_socket_ = NULL;
        timestamp_ = time(NULL);
    }
}

void ChannelMember::QueueResponse(const std::string& status,
                                  const std::string& content_type,
                                  const std::string& extra_headers,
                                  const std::string& data) {
    if (waiting_socket_) {
        assert(queue_.empty());
        assert(waiting_socket_->method() == DataSocket::GET);
        bool ok =
            waiting_socket_->Send(status, true, content_type, extra_headers, data);
        if (!ok) {
        printf("Failed to deliver data to waiting socket\n");
        }
        waiting_socket_ = NULL;
        timestamp_ = time(NULL);
    } else {
        QueuedResponse qr;
        qr.status = status;
        qr.content_type = content_type;
        qr.extra_headers = extra_headers;
        qr.data = data;
        queue_.push(qr);
    }
}

void ChannelMember::SetWaitingSocket(DataSocket* ds) {
    assert(ds->method() == DataSocket::GET);
    if (ds && !queue_.empty()) {
        assert(waiting_socket_ == NULL);
        const QueuedResponse& response = queue_.front();
        ds->Send(response.status, true, response.content_type,
                response.extra_headers, response.data);
        queue_.pop();
    } else {
        waiting_socket_ = ds;
    }
}