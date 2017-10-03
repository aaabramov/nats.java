/*
 *  Copyright (c) 2015-2016 Apcera Inc. All rights reserved. This program and the accompanying
 *  materials are made available under the terms of the MIT License (MIT) which accompanies this
 *  distribution, and is available at http://opensource.org/licenses/MIT
 */

package io.nats.client;

/*
 * This is the implementation of the AsyncSubscription interface.
 *
 */
class AsyncSubscriptionImpl extends SubscriptionImpl implements AsyncSubscription {

    private MessageHandler msgHandler;

    AsyncSubscriptionImpl(ConnectionImpl nc, String subj, String queue,
                          MessageHandler cb) {
        this(nc, subj, queue, cb, false);
    }

    AsyncSubscriptionImpl(ConnectionImpl nc, String subj, String queue,
                          MessageHandler cb, boolean useMsgDlvPool) {
        super(nc, subj, queue, DEFAULT_MAX_PENDING_MSGS, DEFAULT_MAX_PENDING_BYTES, useMsgDlvPool);
        this.msgHandler = cb;
    }

    @Override
    @Deprecated
    public void start() {
        /* Deprecated */
    }

    @Override
    public void setMessageHandler(MessageHandler cb) {
        this.msgHandler = cb;
    }

    @Override
    public MessageHandler getMessageHandler() {
        return msgHandler;
    }

}
