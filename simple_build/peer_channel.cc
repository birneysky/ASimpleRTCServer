#include "peer_channel.h"
#include "data_socket.h"
#include "utils.h"
#include "channel_member.h"

#include <assert.h>
#include <string>

PeerChannel::PeerChannel() {}

PeerChannel::~PeerChannel() { DeleteAll(); }

const PeerChannel::Members& PeerChannel::members() const {
    return members_;
}


bool PeerChannel::IsPeerConnection(const DataSocket* ds) {
    assert(ds);
    return (ds->method() == DataSocket::POST && ds->content_length() > 0) ||
           (ds->method() == DataSocket::GET && ds->PathEquals("/sign_in"));
}


ChannelMember* PeerChannel::Lookup(DataSocket* ds) const {
    assert(ds);

    if (ds->method() != DataSocket::GET && ds->method() != DataSocket::POST)
        return NULL;

    size_t i = 0;
    for (; i < ARRAYSIZE(kRequestPaths); ++i) {
    if (ds->PathEquals(kRequestPaths[i]))
        break;
    }

    if (i == ARRAYSIZE(kRequestPaths))
        return NULL;

    std::string args(ds->request_arguments());
    static const char kPeerId[] = "peer_id=";
    size_t found = args.find(kPeerId);
    if (found == std::string::npos)
        return NULL;

    int id = atoi(&args[found + ARRAYSIZE(kPeerId) - 1]);
    Members::const_iterator iter = members_.begin();
    for (; iter != members_.end(); ++iter) {
        if (id == (*iter)->id()) {
            if (i == kWait)
                (*iter)->SetWaitingSocket(ds);
            if (i == kSignOut)
                (*iter)->set_disconnected();
            return *iter;
        }
    }

    return NULL;
}


ChannelMember* PeerChannel::IsTargetedRequest(const DataSocket* ds) const {
  assert(ds);
  // Regardless of GET or POST, we look for the peer_id parameter
  // only in the request_path.
  const std::string& path = ds->request_path();
  size_t args = path.find('?');
  if (args == std::string::npos)
    return NULL;
  size_t found;
  const char kTargetPeerIdParam[] = "to=";
  do {
    found = path.find(kTargetPeerIdParam, args);
    if (found == std::string::npos)
      return NULL;
    if (found == (args + 1) || path[found - 1] == '&') {
      found += ARRAYSIZE(kTargetPeerIdParam) - 1;
      break;
    }
    args = found + ARRAYSIZE(kTargetPeerIdParam) - 1;
  } while (true);
  int id = atoi(&path[found]);
  Members::const_iterator i = members_.begin();
  for (; i != members_.end(); ++i) {
    if ((*i)->id() == id) {
      return *i;
    }
  }
  return NULL;
}


bool PeerChannel::AddMember(DataSocket* ds) {
assert(IsPeerConnection(ds));
  ChannelMember* new_guy = new ChannelMember(ds);
  Members failures;
  BroadcastChangedState(*new_guy, &failures);
  HandleDeliveryFailures(&failures);
  members_.push_back(new_guy);

  printf("New member added (total=%s): %s\n",
         size_t2str(members_.size()).c_str(), new_guy->name().c_str());

  // Let the newly connected peer know about other members of the channel.
  std::string content_type;
  std::string response = BuildResponseForNewMember(*new_guy, &content_type);
  ds->Send("200 Added", true, content_type, new_guy->GetPeerIdHeader(),
           response);
  return true;
}


void PeerChannel::CloseAll() {
    Members::const_iterator i = members_.begin();
  for (; i != members_.end(); ++i) {
    (*i)->QueueResponse("200 OK", "text/plain", "", "Server shutting down");
  }
  DeleteAll();
}


void PeerChannel::OnClosing(DataSocket* ds) {
for (Members::iterator i = members_.begin(); i != members_.end(); ++i) {
    ChannelMember* m = (*i);
    m->OnClosing(ds);
    if (!m->connected()) {
      i = members_.erase(i);
      Members failures;
      BroadcastChangedState(*m, &failures);
      HandleDeliveryFailures(&failures);
      delete m;
      if (i == members_.end())
        break;
    }
  }
  printf("Total connected: %s\n", size_t2str(members_.size()).c_str());
}

void PeerChannel::CheckForTimeout() {
      for (Members::iterator i = members_.begin(); i != members_.end(); ++i) {
    ChannelMember* m = (*i);
    if (m->TimedOut()) {
      printf("Timeout: %s\n", m->name().c_str());
      m->set_disconnected();
      i = members_.erase(i);
      Members failures;
      BroadcastChangedState(*m, &failures);
      HandleDeliveryFailures(&failures);
      delete m;
      if (i == members_.end())
        break;
    }
  }
}

void PeerChannel::DeleteAll() {
      for (Members::iterator i = members_.begin(); i != members_.end(); ++i)
    delete (*i);
  members_.clear();
}
void PeerChannel::BroadcastChangedState(const ChannelMember& member,
                                        Members* delivery_failures) {
  // This function should be called prior to DataSocket::Close().
  assert(delivery_failures);

  if (!member.connected()) {
    printf("Member disconnected: %s\n", member.name().c_str());
  }

  Members::iterator i = members_.begin();
  for (; i != members_.end(); ++i) {
    if (&member != (*i)) {
      if (!(*i)->NotifyOfOtherMember(member)) {
        (*i)->set_disconnected();
        delivery_failures->push_back(*i);
        i = members_.erase(i);
        if (i == members_.end())
          break;
      }
    }
  }

}
void PeerChannel::HandleDeliveryFailures(Members* failures) {
      assert(failures);

  while (!failures->empty()) {
    Members::iterator i = failures->begin();
    ChannelMember* member = *i;
    assert(!member->connected());
    failures->erase(i);
    BroadcastChangedState(*member, failures);
    delete member;
  }
}


std::string PeerChannel::BuildResponseForNewMember(const ChannelMember& member,
                    std::string* content_type) {
                          assert(content_type);

  *content_type = "text/plain";
  // The peer itself will always be the first entry.
  std::string response(member.GetEntry());
  for (Members::iterator i = members_.begin(); i != members_.end(); ++i) {
    if (member.id() != (*i)->id()) {
      assert((*i)->connected());
      response += (*i)->GetEntry();
    }
  }

  return response;
                    }