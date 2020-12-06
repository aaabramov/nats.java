// Copyright 2020 The NATS Authors
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at:
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package io.nats.examples;

import io.nats.client.Connection;
import io.nats.client.JetStream;
import io.nats.client.Message;
import io.nats.client.Nats;
import io.nats.client.PublishAck;
import io.nats.client.StreamConfiguration;
import io.nats.client.StreamInfo;
import io.nats.client.StreamConfiguration.StorageType;
import io.nats.client.impl.NatsMessage;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class NatsJsPub {

    static final String usageString = "\nUsage: java NatsJsPub [-s server] [-h headerKey:headerValue]* <subject> <message>\n"
            + "\nUse tls:// or opentls:// to require tls, via the Default SSLContext\n"
            + "\nSet the environment variable NATS_NKEY to use challenge response authentication by setting a file containing your private key.\n"
            + "\nSet the environment variable NATS_CREDS to use JWT/NKey authentication by setting a file containing your user creds.\n"
            + "\nUse the URL for user/pass/token authentication.\n";

    public static void createTestStream(JetStream js, String streamName, String subject)
            throws TimeoutException, InterruptedException {

        StreamConfiguration sc = StreamConfiguration.builder().
            name(streamName).
            storageType(StorageType.File).
            subjects(new String[] { subject }).
            build();
        
        // Add or use an existing stream.
        StreamInfo si = js.addStream(sc);
        System.out.printf("Using stream %s on subject %s created at %s.\n",
           streamName, subject, si.getCreateTime().toLocalTime().toString());
    }
    public static void main(String[] args) {
        ExampleArgs exArgs = ExampleUtils.readPublishArgs(args, usageString);

        try (Connection nc = Nats.connect(ExampleUtils.createExampleOptions(exArgs.server, false))) {

            String hdrNote = exArgs.hasHeaders() ? " with " + exArgs.headers.size() + " header(s)," : "";
            System.out.printf("\nPublishing '%s' on %s,%s server is %s\n\n", exArgs.message, exArgs.subject, hdrNote, exArgs.server);

            // Create a jetstream context.
            JetStream js = nc.jetStream();

            // if a stream name is not provided, attempt to create a test stream.
            if (exArgs.stream == null) {
                createTestStream(js, "test-stream", exArgs.subject); 
            }

            // create a typical NATS message
            Message msg = new NatsMessage.Builder()
                    .subject(exArgs.subject)
                    .headers(exArgs.headers)
                    .data(exArgs.message, StandardCharsets.UTF_8)
                    .build();

            // We'll use the defaults for this simple example, but there are options
            // to contrain publishing to certain streams, expect sequence numbers and
            // more. e.g.:
            //
            // PublishOptions pops = PublishOptions.builder().
            //    stream("test-stream").
            //    expectedLastMsgId("transaction-42").
            //    build();
            // js.publish(msg, pops);

            PublishAck pa = js.publish(msg);
            
            System.out.printf("Published message on subject %s, stream %s, seqno %d.\n",
                   exArgs.subject, pa.getStream(), pa.getSeqno());
        }
        catch (Exception exp) {
            System.err.println(exp);
        }
    }
}