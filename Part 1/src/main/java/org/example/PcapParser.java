package org.example;

import org.pcap4j.core.*;
import org.pcap4j.core.BpfProgram.BpfCompileMode;
import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
import org.pcap4j.packet.*;
import org.pcap4j.packet.namednumber.*;
import org.pcap4j.util.MacAddress;

import java.net.Inet6Address;
import java.util.List;

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

                            // Only inject when the browser asks for the final page
                            if (payloadData.contains("GET /d")) {
                                inject(handle, ipv6, tcp);
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
     *
     * @param handle {@link PcapHandle} handle
     * @param originalIp The captured IPv6 packet (the request)
     * @param originalTcp The captured TCP packet (the request)
     */
    /**
     * Injects a TCP Response (HTTP 302) impersonating the server
     */
    public static void inject(PcapHandle handle, IpV6Packet originalIp, TcpPacket originalTcp) {
        TcpPacket.TcpHeader tcpHeader = originalTcp.getHeader();

        int prevSeqNum = tcpHeader.getSequenceNumber();
        int prevAckNum = tcpHeader.getAcknowledgmentNumber();
        TcpPort srcPort = tcpHeader.getSrcPort();
        TcpPort dstPort = tcpHeader.getDstPort();
        int len = tcpHeader.length();

        TcpPacket.Builder tcpBuilder = new TcpPacket.Builder();
        tcpBuilder.acknowledgmentNumber(prevSeqNum + len)
                .sequenceNumber(prevAckNum)
                .dstPort(srcPort)
                .srcPort(dstPort);
    }
}