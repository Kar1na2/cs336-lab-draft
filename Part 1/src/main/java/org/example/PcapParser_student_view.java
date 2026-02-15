package org.example;

import org.pcap4j.core.*;
import org.pcap4j.core.BpfProgram.BpfCompileMode;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.packet.*;
import org.pcap4j.packet.namednumber.*;

import java.net.Inet6Address;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;
import java.time.ZoneId;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class PcapParser_student_view {
    /**
     * Main method to run this code
     * you will only be commenting out lines depending on the task you are performing
     * Refer to the lab spec to know what the tasks are
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) throws PcapNativeException, NotOpenException {
        PcapNetworkInterface device = getInterface();
        if (device == null) {
            System.err.println("No Interfaces found.");
            return;
        }

        listen(device);
    }

    /**
     * This method finds the network interface device to listen on
     *
     * We'll be listening on "Adapter for loopback traffic", if this returns a wrong interface
     * then run the helper method provided below and change the index to the one that says
     * "NPF_loopback" on windows | will double check on mac and wsl later
     * and IP addresses of
     * "/0:0:0:0:0:0:0:1"
     * "/127.0.0.1"
     *
     * @return a {@link PcapNetworkInterface} interface
     */
    public static PcapNetworkInterface getInterface() throws PcapNativeException {
        List<PcapNetworkInterface> allDevs = Pcaps.findAllDevs();
        if (allDevs == null || allDevs.isEmpty()) return null;

        return allDevs.get(9);
    }

    /**
     * Helper method to find the correct {@link PcapNetworkInterface} device
     *
     * @param allDevs a list of {@link PcapNetworkInterface} devices
     */
    private static void getDevs(List<PcapNetworkInterface> allDevs) {
        for (PcapNetworkInterface device : allDevs) {
            System.out.println(device.getName());
            for (PcapAddress addr : device.getAddresses()) {
                System.out.println(addr.getAddress());
            }
        }
    }

    /**
     * Captures the TCP packets from simple server
     *
     * @param device a {@link PcapNetworkInterface} device
     */
    public static void listen(PcapNetworkInterface device) throws NotOpenException, PcapNativeException {
        // let's confirm we're listening to the correct device
        // It should say "NPF_Loopback" | will double-check for wsl and mac later
        System.out.println("Listening on device: " + device.getName());

        PcapNetworkInterface.PromiscuousMode mode = PcapNetworkInterface.PromiscuousMode.PROMISCUOUS;
        PcapHandle handle = device.openLive(65536, mode, 10);
        handle.setFilter("tcp port 8090", BpfProgram.BpfCompileMode.OPTIMIZE);

        PacketListener listener = pcapPacket -> {
            Packet packet = pcapPacket.getPacket();

            try {
                if (packet instanceof BsdLoopbackPacket loopback) {

                    Packet payload = loopback.getPayload();
                    if (payload == null) return;

                    byte[] rawData = payload.getRawData();
                    if (rawData.length == 0) return;

                    IpV6Packet ipv6 = IpV6Packet.newPacket(rawData, 0, rawData.length);

                    if (ipv6.getPayload() instanceof TcpPacket tcp) {
                        // remove these comments once you are on step 5
                        /*
                        if (tcp.getPayload() != null) {
                            String payloadData = new String(tcp.getPayload().getRawData());

                            if (payloadData.contains("HTTP/1.1 302 Found")) {
                                flowLabel.set(ipv6.getHeader().getFlowLabel());
                            }

                            if (payloadData.contains("GET /d HTTP/1.1")) {
                                inject(handle, flowLabel.get(), ipv6, tcp);
                            }
                        }
                         */

                        printTcp(tcp);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        try {
            handle.loop(25, listener);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        handle.close();
    }

    /**
     * Helper method that prints {@link TcpPacket} packet in clean format
     *
     * @param packet {@link TcpPacket} packet
     */
    private static void printTcp(TcpPacket packet) {
        System.out.println("TCP Packet Captured:");
        System.out.println("  Port: " + packet.getHeader().getDstPort());
        if (packet.getPayload() != null) {
            System.out.println("  Data: " + new String(packet.getPayload().getRawData()));
        }
        System.out.println("---------------------------------");
    }

    /**
     * Injects a TCP packet that redirects from final destination
     * Read the comment below to start filling in this method
     *
     * References / Look at
     * https://www.javadoc.io/static/org.pcap4j/pcap4j/2.0.0-alpha.2/org/pcap4j/packet/IpV6Packet.IpV6Header.html
     * https://www.javadoc.io/static/org.pcap4j/pcap4j/2.0.0-alpha.2/org/pcap4j/packet/IpV6Packet.IpV6FlowLabel.html
     * https://www.javadoc.io/static/org.pcap4j/pcap4j/2.0.0-alpha.2/org/pcap4j/packet/UnknownPacket.Builder.html
     * https://kaitoy.github.io/pcap4j/javadoc/latest/en/org/pcap4j/core/PcapHandle.html
     * https://www.javadoc.io/static/org.pcap4j/pcap4j/2.0.0-alpha.2/org/pcap4j/packet/IpV6Packet.Builder.html
     * https://javadoc.io/static/org.pcap4j/pcap4j/2.0.0-alpha.1/org/pcap4j/packet/Packet.Builder.html
     * https://javadoc.io/static/org.pcap4j/pcap4j/2.0.0-alpha.1/org/pcap4j/packet/TcpPacket.Builder.html
     * https://javadoc.io/static/org.pcap4j/pcap4j/2.0.0-alpha.1/org/pcap4j/packet/TcpPacket.TcpHeader.html
     * https://javadoc.io/static/org.pcap4j/pcap4j/2.0.0-alpha.1/org/pcap4j/packet/TcpPacket.html
     * https://javadoc.io/static/org.pcap4j/pcap4j/2.0.0-alpha.1/org/pcap4j/core/Inets.html#AF_INET6
     * https://javadoc.io/static/org.pcap4j/pcap4j/2.0.0-alpha.1/org/pcap4j/packet/namednumber/ProtocolFamily.html#PF_INET6
     * https://javadoc.io/static/org.pcap4j/pcap4j/2.0.0-alpha.1/org/pcap4j/packet/Packet.html
     * https://javadoc.io/static/org.pcap4j/pcap4j/2.0.0-alpha.1/org/pcap4j/packet/BsdLoopbackPacket.html
     *
     * @param handle {@link PcapHandle} handle
     * @param originalIp The captured IPv6 packet (the request)
     * @param flowLabel Flow Label of previous redirect packets
     * @param originalTcp The captured TCP packet (the request)
     */
    /**
     * Injects a TCP Response (HTTP 302) impersonating the server
     */
    public static void inject(PcapHandle handle, IpV6Packet.IpV6FlowLabel flowLabel, IpV6Packet originalIp, TcpPacket originalTcp) throws NotOpenException, PcapNativeException {
        String date = DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneId.of("GMT")));

        String data = "HTTP/1.1 302 Found\r\n"
                + "Date: " + date + "\r\n"
                + "Location: /secret\r\n"
                + "Content-length: 0\r\n"
                + "Server: Jetty(11.0.24)\r\n"
                + "\r\n";

        byte[] http_payload = data.getBytes(StandardCharsets.UTF_8);
        UnknownPacket.Builder http_builder = new UnknownPacket.Builder();
        http_builder.rawData(http_payload);

        // todo! fill in this method
        /*
            This method will create a packet with the following payload given above and inject it into the loopback so that
            instead of going to localhost:8090/d it redirects straight to localhost:8090/secret

            don't try to go to /secret manually, you'll be redirected straight to /d

            To understand how to make a packet, we must first be able to visualize it.
            Similarly to Risc-v's instruction format, Packets itself in its raw data form are data bunched up together.

            ex:
            a Risc-v R type instruction has 32 bits where bits 0-6 represents opcode, 7-11 represents rd, 12-14 funct, and so on
            a Packet's raw data's first few hexes represents the interface device (Null / loopback | DLT_NULL) then it's family protocol (24 representing IPv6 and so on)

            With this image in mind we need to create 3 different packets and later assemble them in this order
                Loopback Packet -> IPv6 Packet -> TCP Packet -> HTTP Packet

            The code for encapsulation and HTTPPacket has already been given to you, what you need to do now are the following
                1. Fill in the values and information for TcpPacket using step 4
                2. Fill in the values and information for IPv6Packet using step 4
                3. Fill in the value for BSDLoopBack Packet

            The required documentation links have been given to you in this method's javaodc.
            When testing, it is recommended that you look for the packet through WIRESHARK (remove tcp.port == 8090 filter)
            as if the packet is rejected / not injected properly then it will not show up in the print statements and will show up as a black entry on Wireshark

            When debugging, or playing around with building a packet, you can check the accuracy through the raw data
            (a hint to see whether it's made properly is by comparing it to previous 302 redirect packets's raw data)
         */

        TcpPacket.Builder tcpBuilder = new TcpPacket.Builder();

        IpV6Packet.Builder ipv6Builder = new IpV6Packet.Builder();
        ipv6Builder.payloadBuilder(tcpBuilder);

        BsdLoopbackPacket.Builder loopBackBuilder = new BsdLoopbackPacket.Builder();
        loopBackBuilder.payloadBuilder(ipv6Builder);

        BsdLoopbackPacket packet = loopBackBuilder.build();
        handle.sendPacket(packet);
    }
}