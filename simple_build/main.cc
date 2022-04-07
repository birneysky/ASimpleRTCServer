// Copyright 2014 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.



#include "hello_shared.h"
#include "hello_static.h"
#include "data_socket.h"
#include "listening_socket.h"
#include "peer_channel.h"
#include "channel_member.h"

#include <stdio.h>
#include <iostream>
#include <vector>
#include <sys/select.h>

int main(int argc, char* argv[]) {
    printf("%s, %s\n", GetStaticText(), GetSharedText());
    const int port = 8888;
    ListeningSocket listener;
    if (!listener.Create()) {
        std::cout << "Failed to create server socket" << std::endl;
        return -1;
    } else if (!listener.Listen(port)) {
        std::cout << "Failed to listen on server socket" << std::endl;
        return -1;
    }

    PeerChannel clients;
    typedef std::vector<DataSocket*> SocketArray;
    SocketArray sockets;
    bool quit = false;
    while (!quit) {
        fd_set socket_set;
        FD_ZERO(&socket_set);
        if (listener.valid())
            FD_SET(listener.socket(), &socket_set);

        for (SocketArray::iterator i = sockets.begin(); i != sockets.end(); ++i)
            FD_SET((*i)->socket(), &socket_set);

        struct timeval timeout = {10, 0};
        if (select(FD_SETSIZE, &socket_set, NULL, NULL, &timeout) == SOCKET_ERROR) {
            std::cout << "select failed" << std::endl;
            break;
        }

        for (SocketArray::iterator i = sockets.begin(); i != sockets.end(); ++i) {
            DataSocket* s = *i;
            bool socket_done = true;
            if (FD_ISSET(s->socket(), &socket_set)) {
                if (s->OnDataAvailable(&socket_done) && s->request_received()) {
                    ChannelMember* member = clients.Lookup(s);
                    if (member || PeerChannel::IsPeerConnection(s)) {
                        if (!member) {
                            if (s->PathEquals("/sign_in")) {
                                clients.AddMember(s);
                            } else {
                                printf("No member found for: %s\n", s->request_path().c_str());
                                s->Send("500 Error", true, "text/plain", "", "Peer most likely gone.");
                            }
                        } else if (member->is_wait_request(s)) {
                            // no need to do anything.
                            socket_done = false;
                        } else {
                            ChannelMember* target = clients.IsTargetedRequest(s);
                            if (target) {
                                member->ForwardRequestToPeer(s, target);
                            } else if (s->PathEquals("/sign_out")) {
                                s->Send("200 OK", true, "text/plain", "", "");
                            } else {
                                printf("Couldn't find target for request: %s\n",
                                        s->request_path().c_str());
                                s->Send("500 Error", true, "text/plain", "",
                                        "Peer most likely gone.");
                             }
                        }
                    }  else {
                        if (quit) {
                            printf("Quitting...\n");
                            FD_CLR(listener.socket(), &socket_set);
                            listener.Close();
                            clients.CloseAll();
                        }
                    }
                } 
            } else {
                socket_done = false;
            }
        }
    }

  
  return 0;
}
