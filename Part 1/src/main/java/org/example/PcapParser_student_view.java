package org.example;

import org.pcap4j.core.*;
import org.pcap4j.packet.BsdLoopbackPacket;
import org.pcap4j.packet.IpV6Packet;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.TcpPacket;

import java.util.List;

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
}