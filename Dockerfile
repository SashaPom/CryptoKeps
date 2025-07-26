FROM ubuntu:latest
LABEL authors="oleksandrpomazan"

ENTRYPOINT ["top", "-b"]