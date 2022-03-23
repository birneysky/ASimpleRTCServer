#ifndef PEER_CHANNEL_H_
#define PEER_CHANNEL_H_

#include <vector>

class ChannelMember;
class DataSocket;
class PeerChannel {
public:
    typedef std::vector<ChannelMember*> Members;

    PeerChannel();

    ~PeerChannel();

    const Members& members() const;

    // Returns true if the request should be treated as a new ChannelMember
    // request.  Otherwise the request is not peerconnection related.
    static bool IsPeerConnection(const DataSocket* ds);

    // Finds a connected peer that's associated with the |ds| socket.
    ChannelMember* Lookup(DataSocket* ds) const;

    // Checks if the request has a "peer_id" parameter and if so, looks up the
    // peer for which the request is targeted at.
    ChannelMember* IsTargetedRequest(const DataSocket* ds) const;

    // Adds a new ChannelMember instance to the list of connected peers and
    // associates it with the socket.
    bool AddMember(DataSocket* ds);

    // Closes all connections and sends a "shutting down" message to all
    // connected peers.
    void CloseAll();

    // Called when a socket was determined to be closing by the peer (or if the
    // connection went dead).
    void OnClosing(DataSocket* ds);

    void CheckForTimeout();

protected:
    void DeleteAll();
    void BroadcastChangedState(const ChannelMember& member,
                                Members* delivery_failures);
    void HandleDeliveryFailures(Members* failures);

    // Builds a simple list of "name,id\n" entries for each member.
    std::string BuildResponseForNewMember(const ChannelMember& member,
                                        std::string* content_type);

protected:
    Members members_;
};


#endif