FROM minio/minio

EXPOSE 9000
EXPOSE 9001

ENV MINIO_ROOT_USER=admin
ENV MINIO_ROOT_PASSWORD=admin123

CMD ["server", "/data", "--console-address", ":9001"]
