# Use Maven with JDK 22 on amd64 for the x86_64 environment
FROM --platform=linux/amd64 maven:3.9.6-eclipse-temurin-22

# Install libpcap and other networking tools needed for pcap analysis
RUN apt-get update && apt-get install -y \
    libpcap-dev \
    tcpdump \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

CMD ["tail", "-f", "/dev/null"]