package com.ferreusveritas.mcf.network;

import com.ferreusveritas.mcf.peripheral.WebModemPeripheral;
import org.apache.logging.log4j.LogManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;


/**
 * The Communications Thread for ComputerCraft WebServer.
 * <p>
 * This thread is responsible for listening for external connections, receiving
 * data, and passing it to the appropriate WebModem. It must also receive data
 * from a WebModem and transmit it back.
 *
 * @author qidydl
 */
public class CommsThread extends Thread {

    private static CommsThread s_Instance = null;
    private final Semaphore terminate;
    private final int listenPort;
    private final ByteBuffer buffer = ByteBuffer.allocate(16384);
    private final ConcurrentMap<Integer, SocketChannel> sockets;
    private final ConcurrentMap<Integer, WebModemPeripheral> modems;
    private Selector sel;

    /**
     * Create a new Communications Thread.
     *
     * @param port The port to listen for connections on.
     */
    public CommsThread(int port) {
        terminate = new Semaphore(0);
        listenPort = port;
        sockets = new ConcurrentHashMap<>();
        modems = new ConcurrentHashMap<>();
        try {
            sel = Selector.open();
        } catch (IOException e) {
            // We're basically screwed if this happens.
            // Initialization in the run() method checks for this, because
            // blowing up in the constructor is problematic.
            sel = null;
        }
    }

    /**
     * Get the current instance of the CommsThread being used.
     *
     * @return The CommsThread that should be used for communication.
     */
    public static CommsThread getInstance() {
        return s_Instance;
    }

    /**
     * Set the instance of the CommsThread being used.
     *
     * @param ct The CommsThread that should be used for communication.
     */
    public static void setInstance(CommsThread ct) {
        s_Instance = ct;
    }

    /**
     * Tell the communications thread to stop processing and close all open
     * connections.
     */
    public void shutdown() {
        s_Instance = null;

        // Releasing the semaphore allows the thread to exit the loop
        terminate.release();

        // Waking the selector breaks us out of the select() call
        sel.wakeup();
    }

    /**
     * Tell the communications thread that a new modem has been activated.
     *
     * @param computerID The computer that is using the modem.
     * @param modem      The modem that was activated.
     */
    public void registerModem(int computerID, WebModemPeripheral modem) {
        modems.put(computerID, modem);
    }

    /**
     * Tell the communications thread that a modem has been deactivated.
     *
     * @param computerID the computer that is no longer using the modem
     */
    public void unregisterModem(int computerID) {
        modems.remove(computerID);
    }

    public String GetReasonCode(int responseCode) {

        switch (responseCode) {
            // HTTP 1.0 Server status codes -- see RFC 1945
            case 200:
                return "OK";
            case 201:
                return "Created";
            case 202:
                return "Accepted";
            case 204:
                return "No Content";
            case 301:
                return "Moved Permanently";
            case 302:
                return "Moved Temporarily";
            case 304:
                return "Not Modified";
            case 400:
                return "Bad Request";
            case 401:
                return "Unauthorized";
            case 403:
                return "Forbidden";
            case 404:
                return "Not Found";
            case 500:
                return "Internal Server Error";
            case 501:
                return "Not Implemented";
            case 502:
                return "Bad Gateway";
            case 503:
                return "Service Unavailable";

            // HTTP 1.1 Server status codes -- see RFC 2048
            case 100:
                return "Continue";
            case 307:
                return "Temporary Redirect";
            case 405:
                return "Method Not Allowed";
            case 409:
                return "Conflict";
            case 412:
                return "Precondition Failed";
            case 413:
                return "Request Too Long";
            case 414:
                return "Request-URI Too Long";
            case 415:
                return "Unsupported Media Type";
            case 300:
                return "Multiple Choices";
            case 303:
                return "See Other";
            case 305:
                return "Use Proxy";
            case 402:
                return "Payment Required";
            case 406:
                return "Not Acceptable";
            case 407:
                return "Proxy Authentication Required";
            case 408:
                return "Request Timeout";

            case 101:
                return "Switching Protocols";
            case 203:
                return "Non Authoritative Information";
            case 205:
                return "Reset Content";
            case 206:
                return "Partial Content";
            case 504:
                return "Gateway Timeout";
            case 505:
                return "Http Version Not Supported";
            case 410:
                return "Gone";
            case 411:
                return "Length Required";
            case 416:
                return "Requested Range Not Satisfiable";
            case 417:
                return "Expectation Failed";

            // WebDAV Server-specific status codes
            case 102:
                return "Processing";
            case 207:
                return "Multi-Status";
            case 422:
                return "Unprocessable Entity";
            case 419:
                return "Insufficient Space On Resource";
            case 420:
                return "Method Failure";
            case 423:
                return "Locked";
            case 507:
                return "Insufficient Storage";
            case 424:
                return "Failed Dependency";
        }

        return "Unknown";
    }


    /**
     * Transmit a response to a particular connection. This can only be used when the Comms Thread
     * has told a modem that it has a request.
     *
     * @param connection   The connection identifier that was provided by the Comms Thread.
     * @param responseCode The HTTP response code to transmit with the response.
     * @param data         The data to transmit.
     */
    public void transmitResponse(int connection, int responseCode, String data) {
        if (sockets.containsKey(connection)) {
            try {
                SocketChannel conn = sockets.get(connection);
                String reason = GetReasonCode(responseCode);
                data = "HTTP/1.0 " + responseCode + " " + reason + "\n\n" + data;

                // Allocating a buffer every time is not optimal, but it might not be a big deal.
                // *If* it proves to be a problem, this can be converted to a shared or per-thread
                // buffer or something like that.
                ByteBuffer outputBuffer = ByteBuffer.wrap(data.getBytes(StandardCharsets.UTF_8));
                conn.write(outputBuffer);

                SelectionKey key = conn.keyFor(sel);
                key.cancel();
                conn.socket().close();
                sockets.remove(conn.hashCode());
            } catch (UnsupportedEncodingException e) {
                // Should be impossible to get here; UTF-8 is really standard and supports nearly any character
                LogManager.getLogger().error("CCWebServer: CommsThread: Unsupported encoding!", e);
            } catch (IOException e) {
                // Thrown mostly by the socket being closed, which will be detected and handled by
                // the normal processing loop, so we don't need to do anything here.
            }
        }
    }

    /**
     * The thread entry point.
     */
    @Override
    public void run() {
        // Set the thread name, mostly for debugging purposes.
        this.setName("CCWebServer.CommsThread");

        ServerSocketChannel servsock = null;
        boolean initialized = false;
        boolean terminated = true;

        // Don't bother trying to initialize if we couldn't create the Selector
        if (sel != null) {
            // Initialize I/O and start listening for connections
            try {
                servsock = ServerSocketChannel.open();
                servsock.bind(new InetSocketAddress(listenPort));
                servsock.configureBlocking(false);
                servsock.register(sel, SelectionKey.OP_ACCEPT);

                // We only need to do clean-up below if initialization was successful
                initialized = true;

                // Only allow the loop to run if we finished all of the above
                terminated = false;
            } catch (IOException e) {
                // We're basically screwed if this happens.
                LogManager.getLogger().error("CCWebServer Mod: CommsThread: Could not listen for connections!", e);
            }
        }

        // Main processing loop - listen for connections or new data
        while (!terminated) {
            try {
                // Block until something happens. Shutdown will release us from this.
                int num = sel.select();

                if (num > 0) {
                    // Look through all the items that should have something available
                    for (SelectionKey key : sel.selectedKeys()) {
                        if (key.isAcceptable()) {
                            // Accept the incoming connection.
                            SocketChannel conn = ((ServerSocketChannel) key.channel()).accept();
                            conn.configureBlocking(false);
                            conn.register(sel, SelectionKey.OP_READ);
                            sockets.put(conn.hashCode(), conn);

                            //DEBUG
                            //System.out.println("Received connection from " + conn.socket().getRemoteSocketAddress().toString());
                        } else // Everything else is UN-ACCEPTABLE :P
                            if (key.isReadable()) {
                                SocketChannel conn = (SocketChannel) key.channel();

                                if (!processInput(conn)) {
                                    // We couldn't read any data. This *might* be an error, but most likely it's socket
                                    // closure, so clean up.
                                    key.cancel();
                                    conn.socket().close();
                                    sockets.remove(conn.hashCode());

                                    //DEBUG
                                    //System.out.println("Connection closed from " + conn.socket().getRemoteSocketAddress().toString());
                                }
                            }
                    }

                    // Once we're done processing, clear out the set.
                    sel.selectedKeys().clear();
                }

                // Check the semaphore to see if we're shutting down, but don't block.
                terminated = terminate.tryAcquire();
            } catch (ClosedSelectorException e) {
                // We're done, for whatever reason.
                LogManager.getLogger().error("CCWebServer: CommsThread: Selector closed unexpectedly.", e);
                terminated = true;
            } catch (IOException e) {
                // May or may not be able to recover from some situations, for now just give up.
                // In the future this should be moved inside the selectedKeys loop to cancel any keys that
                // are causing problems.
                LogManager.getLogger().error("CCWebServer: CommsThread: Communications failure!", e);
                terminated = true;
            }
        }

        // Clean-up
        if (initialized) {
            try {
                servsock.close();
                sel.close();
            } catch (IOException e) {
                // We don't care, we're shutting down anyway.
            }
        }
    }

    /**
     * Process data received from a remote connection.
     *
     * @param sc The connection to receive data from.
     * @return True if data was actually received, false otherwise.
     * @throws IOException If an error occurs while reading the data.
     */
    private boolean processInput(SocketChannel sc) throws IOException {
        // Read received data into the buffer
        buffer.clear();
        sc.read(buffer);

        // Verify we received some data
        if (buffer.position() == 0) {
            return false;
        }

        // Keep reading data until we've got it all
        StringBuilder inputBuilder = new StringBuilder();
        do {
            // "Flip" the buffer to access the data we just received
            buffer.flip();

            // Copy the data out of the buffer and convert it to a string
            byte[] validData = Arrays.copyOfRange(buffer.array(), 0, buffer.limit());
            String bufferData = new String(validData, StandardCharsets.UTF_8);
            inputBuilder.append(bufferData);

            // Clear the buffer and perform another read
            buffer.clear();
            sc.read(buffer);
        } while (buffer.position() > 0);

        // We have all the data, start processing
        String input = inputBuilder.toString();
        String path = "";
        List<String> params = new ArrayList<String>();

        //System.out.println("Input Received [" + input + "]");

        RequestType mode = RequestType.RAW;

        //Process HTTP Method types
        if (input.startsWith("GET ")) {
            mode = RequestType.GET;
            input = input.substring(4);//Consume "GET "
        } else if (input.startsWith("HEAD ")) {
            mode = RequestType.HEAD;
            input = input.substring(5);//Consume "HEAD "
        } else if (input.startsWith("POST ")) {
            mode = RequestType.POST;
            input = input.substring(5);//Consume "POST "
        }

        if (mode == RequestType.GET || mode == RequestType.HEAD) {
            int s = input.indexOf(' ');
            if (s > 0) {
                input = input.substring(0, s);//Drop the header data after the URL
                //TODO: Build request header and pass it to the modem
            }
        } else if (mode == RequestType.POST) {
            transmitResponse(sc.hashCode(), 405, "Bad Request: Method Not Allowed: " + mode);
            return true;
        } else if (mode == RequestType.RAW) {
            mode = RequestType.GET;//Assume GET without the proper tag
        }


        // First, break apart path and query string
        int split = input.indexOf('?');
        if (split > 0) {
            // First part is the URL path
            path = input.substring(0, split);

            // The rest is all parameter data concatenated together
            String paramData = input.substring(split + 1);
            String[] paramPairs = paramData.split("&");
            for (String pair : paramPairs) {
                params.add(URLDecoder.decode(pair, "UTF-8"));
            }
        } else {
            // No parameters specified
            path = input;
        }

        // Remove any leading/trailing whitespace
        path = path.trim();

        // Now we have an absolute path and parameter data, next we need to examine the path.

        //DEBUG
        //System.out.println("Received request for [" + path + "] with parameters [" + params.toString() + "]");
        //ByteBuffer test = ByteBuffer.allocate(1024);
        //test.put("test\n".getBytes("UTF-8"));
        //test.flip();
        //sc.write(test);
        //DEBUG

        // Strip any leading slashes
        if (path.length() > 0) {
            while (path.startsWith("/")) {
                path = path.substring(1);
            }
        }

        // Break the path into computer ID and everything else
        String computerID = "";
        int computerIDint = 0;
        String remainingPath = "";

        // Path format is <computerId>[/optional further path]
        split = path.indexOf('/');

        if (split > 0) {
            computerID = path.substring(0, split);
            remainingPath = path.substring(split + 1);
        } else {
            computerID = path;
        }

        try {
            computerIDint = Integer.parseInt(computerID);
        } catch (NumberFormatException e) {
            //System.out.print("Bad Request: Computer ID must be a valid integer [" + computerID + "]");
            transmitResponse(sc.hashCode(), 400, "Bad Request: Computer ID must be a valid integer");
        }

        if (modems.containsKey(computerIDint)) {
            //System.out.print("Computer ID processing [" + computerIDint + "]");
            WebModemPeripheral modem = modems.get(computerIDint);
            modem.receiveRequest(sc.hashCode(), computerIDint, remainingPath, params);
        } else {
            //System.out.print("Object Not Found: The specified computer ID does not exist or is not ready.");
            transmitResponse(sc.hashCode(), 404, "Object Not Found: The specified computer ID does not exist or is not ready.");
        }

        return true;
    }

    enum RequestType {
        RAW,
        GET,
        HEAD,
        POST
    }
}
